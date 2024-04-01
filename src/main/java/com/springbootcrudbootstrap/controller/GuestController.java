package com.springbootcrudbootstrap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.jar.JarException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbootcrudbootstrap.bean.GuestBean;
import com.springbootcrudbootstrap.model.Guest;
import com.springbootcrudbootstrap.service.GuestService;
import com.springbootcrudbootstrap.service.ReportService;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
public class GuestController {

	@Autowired 
	GuestService guestService;
	
	@Autowired 
	ReportService reportService;
	
	public static final String path = "guest";

	private static final String PARAM_ID = "id";
	
	@GetMapping("/index")
	public String ShowHomePageIndex(HttpServletRequest httpServletRequest,Model model){
		return path + "/" +"index";
	}
	
	@GetMapping("/newGuest")
	public String ShowAddForm(HttpServletRequest httpServletRequest,Model model){
		return path + "/" +"add-form";
	}
	
	@GetMapping("/editGuest/{id}")
	public String ShowEditCustomer(HttpServletRequest httpServletRequest,Model model,final @PathVariable(PARAM_ID) long id){
		Guest guest = guestService.findId(id);
		model.addAttribute("guest",guest);
		return path + "/" +"edit-form";
	}
	
	@PostMapping("/newGuest/submit")
	public String AddNew(HttpServletRequest httpServletRequest,Model model,final @Validated GuestBean params,RedirectAttributes redirectAttrs)throws Exception{
		redirectAttrs.addFlashAttribute("guest" , params);
		redirectAttrs.addFlashAttribute("messageType" , "Info");
		redirectAttrs.addFlashAttribute("message" , "Success");
		guestService.saveGuest(params);

		
		return "redirect:" + "/newGuest";
	}
	
	@PostMapping("/editGuest/submit")
	public String EditGuest(HttpServletRequest httpServletRequest,Model model,final @Validated GuestBean params,RedirectAttributes redirectAttrs)throws Exception{
		redirectAttrs.addFlashAttribute("guest" , params);
		redirectAttrs.addFlashAttribute("messageType" , "Info");
		redirectAttrs.addFlashAttribute("message" , "Success");
		guestService.updateGuest(params.getId(),params);
		
		return "redirect:" + "/editGuest/"+params.getId();
	}
	
	@PostMapping("/deleteGuest/{id}")
	public String DeleteGuest(HttpServletRequest httpServletRequest,Model model,final @PathVariable(PARAM_ID) long id){
		guestService.deleteById(id); 
		return "redirect:" + "/"; 
	}
	
	@GetMapping("/listGuest")
	public String ShowGuestPagin(HttpServletRequest httpServletRequest,Model model,@RequestParam(value = "mySearch", required = false) String mySearch,@RequestParam(value = "page", required = false) Integer page){
		if(page == null){page = 1;}
		int totalRecord = Integer.valueOf(guestService.findAll().size());
		int totalPage = PagingUtils.totalPage(page, totalRecord);
		int startPage = PagingUtils.startPage(page, totalRecord);
		int endPage = PagingUtils.endPage(page, totalRecord);
		int firstRecord = PagingUtils.firstRecord(page, totalRecord);
		int endRecord = PagingUtils.endRecord(page, totalRecord);
		
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", totalPage);
		
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("infoPage","Showing "+firstRecord+" to "+endRecord+" of "+totalRecord+" entries");
		model.addAttribute("guestList",guestService.findGuestSearch(mySearch,page,PagingUtils.getRecordperpage()));
		model.addAttribute("mySearch", mySearch);
		return path + "/" +"list-guest";
	}
	
	@GetMapping("/report")
	@ResponseBody
	public String ShowReport(Model model ,HttpServletRequest httpServletRequest,HttpServletResponse response) throws JarException, IOException, JRException{
		List<Map<String, ?>> listCustomer =  reportService.findAllReportCustomer();
		model.addAttribute("listCustomer",listCustomer);
		ServletContext servletContext = httpServletRequest.getSession().getServletContext();
	    String sourceFileName = servletContext.getRealPath("/WEB-INF/template/guestReport.jrxml");
		InputStream inputStream = new FileInputStream(new File(sourceFileName));
		JRDataSource jrDataSource = new JRBeanCollectionDataSource(listCustomer);
		DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
	    JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
		JasperReport report = JasperCompileManager.compileReport(inputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(report,null,jrDataSource);
		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		return path + "/" +"list-guest";
	}

}
