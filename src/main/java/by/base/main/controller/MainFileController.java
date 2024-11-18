package by.base.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.Route;
import by.base.main.model.Truck;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.TruckService;
import by.base.main.service.util.MailService;
import by.base.main.service.util.POIExcel;

@Controller
@RequestMapping(path = "file")
public class MainFileController {
	
	@Autowired
	TruckService truckService;
	
	@Autowired
	POIExcel poiExcel;
	
	@Autowired
	RouteService routeService;
	
	@Autowired
	MailService mailService;
	
    @RequestMapping(value="/echo", method=RequestMethod.GET)
    public @ResponseBody String handleFileUpload(HttpServletRequest request) throws IOException{
    	System.out.println("MainFileController ECHO");
    	 return "echo";
    }
	
    @RequestMapping(value="/sendFileAgree", method=RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public @ResponseBody String handleFileUpload(@RequestParam(value = "agreePersonalData", required = false) MultipartFile mulFile2,  HttpServletRequest request) throws IOException, ServletException{
    	String name = request.getHeader("ynp");
    	saveFile(mulFile2, request, name);
    	return "Good!";
    }   
    
    @RequestMapping(value="/sendContract", method=RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public @ResponseBody String sendContract(@RequestParam(value = "contract", required = false) MultipartFile mulFile2,  HttpServletRequest request) throws IOException, ServletException, ServiceException{
    	String name = request.getHeader("companyName");
    	File file = poiExcel.getFileByMultipart(mulFile2);
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				mailService.sendEmailWhithFileToUser(request, "Договор от перевозчика "+name, "", file, "ArtyuhevichO@dobronom.by");    //GrushevskiyD@dobronom.by	 
			}
		}).start();
    	
    	return "Good!";
    }
    
    /**
	 * Метод отвечает за скачивание документа инструкции для графика поставок для ТО
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/delivery-schedule-to/downdoad/instruction-trading-objects")
	public String downdoadIncotermsInsuranceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"instruction-trading-objects.docx");
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/Инструкция по графикам поставок на ТО.docx");
			//  Создать выходной поток
			out = response.getOutputStream();
			//  Создать буфер
			byte buffer[] = new byte[1024];
			int len = 0;
			//  Прочитать содержимое входного потока в буфер в цикле
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			in.close();
			out.close();
		}
		return "complited";
	}
	
	private File convertMultiPartToFile(MultipartFile file, HttpServletRequest request ) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
	    File convFile = new File(appPath + "resources/others/"+file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
	    return convFile;
	}
	
	
	private void saveFile(MultipartFile file, HttpServletRequest request, String name) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		File directory = new File(appPath + "resources/others/fileAgree/");
		if (directory.mkdir()) {
            System.out.println("папка создана");
        }
		System.out.println(appPath);
		String[] findType = file.getOriginalFilename().split("\\.");
		String type = "." + findType[findType.length-1];
	    File convFile = new File(appPath + "resources/others/fileAgree/"+name+type);
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
//	    return convFile;
	}

}
