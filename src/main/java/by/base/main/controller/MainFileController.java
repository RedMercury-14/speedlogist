package by.base.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tika.Tika;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import by.base.main.aspect.TimedExecution;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.dto.ImageDTO;
import by.base.main.dto.MarketDataFor330Request;
import by.base.main.dto.MarketDataFor330Responce;
import by.base.main.dto.MarketDataForRequestDto;
import by.base.main.dto.MarketErrorDto;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.dto.ReportRow;
import by.base.main.dto.RoadTransportDto;
import by.base.main.model.Act;
import by.base.main.model.Message;
import by.base.main.model.MyFile;
import by.base.main.model.Order;
import by.base.main.model.OrderProduct;
import by.base.main.model.Rotation;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.ActService;
import by.base.main.service.FileService;
import by.base.main.service.MarketAPI;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.RotationService;
import by.base.main.service.RouteService;
import by.base.main.service.ServiceException;
import by.base.main.service.TruckService;
import by.base.main.service.UserService;
import by.base.main.service.util.CustomJSONParser;
import by.base.main.service.util.MailService;
import by.base.main.service.util.OrderCreater;
import by.base.main.service.util.POIExcel;

//@Controller
@RestController
@RequestMapping(path = "file", produces = "application/json")
public class MainFileController {
	
	@Autowired
	private TruckService truckService;
	
	@Autowired
	private POIExcel poiExcel;
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private MarketAPI marketAPI;
	
	@Autowired
	private RotationService rotationService;
	
	@Autowired
	private ActService actService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderCreater orderCreater;
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();
	
	/**
	 * Сохранение нескольких  файлов с привязкой к маршруту
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadArrayFilesForPrilesie", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	@TimedExecution
	public Map<String, Object> loadArrayFileForPrilesie(@RequestParam("files") MultipartFile[] files) throws IOException {
	    Map<String, Object> response = new HashMap<>();
	    List<String> uploadedFileNames = new ArrayList<>();
	    
	    Tika tika = new Tika();

	    for (MultipartFile file : files) {
	    	
	    	
		    String detectedType = tika.detect(file.getInputStream());
		    System.out.println(detectedType);

//		    if (!detectedType.startsWith("image/")) {
//		        throw new IllegalArgumentException("Подозрительный тип файла: " + detectedType);
//		    }	    	
//	    	
//	        if (!file.isEmpty()) {
//	        	fileService.saveFileByRoute(file, idRoute, getThisUser());
//	            uploadedFileNames.add(file.getOriginalFilename());
//	        }
	    }
	    
	    response.put("uploadedFiles", uploadedFileNames);
	    response.put("total", uploadedFileNames.size());
	    return response;
	}
	
	/**
	 * Возвращает изображения по idRoute в формате json, изображения в base64
	 * @param idRoute
	 * @return
	 */
	@GetMapping("/images/base64/byRoute/{idRoute}")
	public ResponseEntity<List<ImageDTO>> getBase64ImagesByRoute(@PathVariable Integer idRoute) {
	    List<MyFile> files = fileService.getFilesByIdRoute(idRoute);

	    List<ImageDTO> images = files.stream()
	        .filter(f -> f.getContentType() != null && f.getContentType().startsWith("image/"))
	        .map(f -> new ImageDTO(
	            f.getFileName(),
	            f.getContentType(),
	            Base64.getEncoder().encodeToString(f.getData())
	        ))
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(images);
	}

	
	/**
	 * скачка одного файла
	 * @param id
	 * @return
	 */
	@GetMapping("/downloadFileByRoute/zip/{id}")
	public void downloadMultipleFilesAsZip(@PathVariable Integer id, HttpServletResponse response) throws IOException {
    	
    	List<MyFile> files = fileService.getFilesByIdRoute(id);

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"files.zip\"");

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (MyFile file : files) {
                ZipEntry zipEntry = new ZipEntry(file.getFileName());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(file.getData());
                zipOut.closeEntry();
            }
            zipOut.finish();
        }
    }
	
	/**
	 * Метод, который отдаёт ID ИЗОБРАЖЕНИЙ по маршруту
	 * @param idRoute
	 * @return
	 */
	@GetMapping("/images/byRoute/{idRoute}")
	@TimedExecution
	public ResponseEntity<List<Long>> getImageIdsByRoute(@PathVariable Integer idRoute) {
	    List<MyFile> files = fileService.getFilesByIdRoute(idRoute);
	    List<Long> imageIds = files.stream()
	            .filter(f -> f.getContentType() != null && f.getContentType().startsWith("image/"))
	            .map(MyFile::getIdFiles)
	            .collect(Collectors.toList());

	    return ResponseEntity.ok(imageIds);
	}
	
	/**
	 * Метод, который отдаёт ID файлов по маршруту
	 * @param idRoute
	 * @return
	 */
	@GetMapping("/files/byRoute/{idRoute}")
	@TimedExecution
	public ResponseEntity<List<Long>> getFilesIdsByRoute(@PathVariable Integer idRoute) {
	    List<MyFile> files = fileService.getFilesByIdRoute(idRoute);
	    List<Long> imageIds = files.stream()
	            .filter(f -> f.getContentType() != null)
	            .map(MyFile::getIdFiles)
	            .collect(Collectors.toList());

	    return ResponseEntity.ok(imageIds);
	}
	
	/**
	 * Сохранение одного файла БД
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadFileTest", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	@TimedExecution
	public Map<String, Object> handleFileUpload(@RequestParam("excel") MultipartFile file) throws IOException {
	    Map<String, Object> response = new HashMap<>();
	    fileService.saveMultipartFile(file);
	    response.put("fileName", file.getOriginalFilename());
	    response.put("size", file.getSize());
	    response.put("contentType", file.getContentType());
	    response.put("success", true);
	    return response;
	}
	
	/**
	 * Сохранение файла с привязкой к маршруту
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadFileForRoute", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	@TimedExecution
	public Map<String, Object> loadFileForRoute(@RequestParam("file") MultipartFile file, @RequestParam("idRoute") Integer idRoute) throws IOException {
	    Map<String, Object> response = new HashMap<>();
	    Long id = fileService.saveFileByRoute(file, idRoute, getThisUser());
	    response.put("status", "200");
	    response.put("idFile", id);
	    return response;
	}
	
	/**
	 * Сохранение нескольких  файлов с привязкой к маршруту
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadArrayFilesForRoute", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	@TimedExecution
	public Map<String, Object> loadArrayFileForRoute(@RequestParam("files") MultipartFile[] files, @RequestParam("idRoute") Integer idRoute) throws IOException {
	    Map<String, Object> response = new HashMap<>();
	    List<String> uploadedFileNames = new ArrayList<>();

	    for (MultipartFile file : files) {
	        if (!file.isEmpty()) {
	        	fileService.saveFileByRoute(file, idRoute, getThisUser());
	            uploadedFileNames.add(file.getOriginalFilename());
	        }
	    }
	    
	    response.put("uploadedFiles", uploadedFileNames);
	    response.put("total", uploadedFileNames.size());
	    return response;
	}
	
	/**
	 * охранение нескольких файлов в БД
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/loadArrayFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> handleMultipleFilesUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
	    Map<String, Object> response = new HashMap<>();
	    List<String> uploadedFileNames = new ArrayList<>();

	    for (MultipartFile file : files) {
	        if (!file.isEmpty()) {
	            fileService.saveMultipartFile(file);
	            uploadedFileNames.add(file.getOriginalFilename());
	        }
	    }

	    response.put("status", "200");
	    response.put("uploadedFiles", uploadedFileNames);
	    response.put("total", uploadedFileNames.size());
	    response.put("success", true);
	    return response;
	}
	
	/**
	 * скачка одного файла
	 * @param id
	 * @return
	 */
//	@GetMapping("/downloadFile/{id}")
//    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
//    	
//    	MyFile file = fileService.getFileById(id);
//
//        if (file == null) {
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(file.getContentType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//                .body(file.getData());
//    }
	@GetMapping("/downloadFile/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
	    MyFile file = fileService.getFileById(id);

	    if (file == null) {
	        return ResponseEntity.noContent().build();
	    }
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(file.getContentType()));
//	    headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFileName()).build());
	    headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFileName(), StandardCharsets.UTF_8).build());
	    headers.setCacheControl("public, max-age=3600");
	    return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
	}
    
    @PostMapping("/deleteFile")
    public Map<String, Object> deleteFileById(@RequestParam("id") Long id) {
    	Map<String, Object> responce = new HashMap<String, Object>();
        boolean deleted = fileService.deleteByIdFromStatus(id, getThisUser());
        if (deleted) {
        	responce.put("status", "200");
        	responce.put("message", "Файл успешно удалён");        	
            return responce;
        } else {
        	responce.put("status", "100");
        	responce.put("message", "Файл не найден");        	
            return responce;
        }
    }
	
	/**
     * @param request
     * @param response
     * @param dateStart
     * @param dateFinish
     * @throws IOException
     * @throws ParseException
     * Метод для создания и скачивания excel-файла транспортного отчёта
     * за заданный период времени
     * @author Ira
     */
    @RequestMapping("/get-road-tarnsport-report/{dateStart}&{dateFinish}")
    @TimedExecution
    public void getRoadTransportReport(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable String dateStart,
                                  @PathVariable String dateFinish) throws IOException, ParseException {
        Date dateFrom = Date.valueOf(dateStart);
        Date dateTo = Date.valueOf(dateFinish);
        LocalDate startDate = dateFrom.toLocalDate();
        LocalDate finishDate = dateTo.toLocalDate();

        java.util.Date routeService1 = new java.util.Date();
        List <Route> routes = routeService.getRouteListByDatesCreate(dateFrom, dateTo);
        java.util.Date routeService2 = new java.util.Date();
        System.out.println(routeService2.getTime() - routeService1.getTime() + " ms - get route list");

        List<String> idRoutes = new ArrayList<>();
        for (Route route : routes) {
            idRoutes.add(route.getIdRoute().toString());
        }
        Map<String, List<Message>> messageMap = routeService.routesWithMessages(idRoutes);
        List<RoadTransportDto> roadTransportDTOList = new ArrayList<>();

        java.util.Date forRoute1 = new java.util.Date();
        for (Route route : routes) {
            RoadTransportDto roadTransportDTO = new RoadTransportDto();

//            java.util.Date actService1 = new java.util.Date();

            List<Act> acts = actService.getActsByRouteId(route.getIdRoute().toString(), startDate, finishDate);
//            java.util.Date actService2 = new java.util.Date();
//            System.out.println(actService2.getTime() - actService1.getTime() + " ms - act service");
            if (!acts.isEmpty() && acts.get(0).getDocumentsArrived() != null) {
                roadTransportDTO.setDocumentsArrived(acts.get(0).getDocumentsArrived());
            }
            if (route.getWay() != null) {
                if (route.getWay().equals("Импортный") || route.getWay().equals("Импорт")) {
                    roadTransportDTO.setImportOrExport("Импорт");
                } else if (route.getWay().equals("Экспортный") || route.getWay().equals("Экспорт")) {
                    roadTransportDTO.setImportOrExport("Экспорт");
                } else if (route.getWay().equals("АХО")) {
                    roadTransportDTO.setImportOrExport("АХО");
                } else if(route.getWay().equals("РБ")) {
                	roadTransportDTO.setImportOrExport("РБ");
                }
            }

            roadTransportDTO.setRouteId(route.getIdRoute().toString());

            if (!route.getOrders().isEmpty()){
                Set<Order> orders = route.getOrders();
                List<String> requestIDs = new ArrayList<>();
                StringBuilder builder = new StringBuilder();

                for (Order order : orders) {
                    requestIDs.add(order.getIdOrder().toString());
                }
                builder.append(String.join(", ", requestIDs));
                roadTransportDTO.setRequestId(builder.toString());
                roadTransportDTO.setSupplier(route.getOrders().stream().toList().get(0).getCounterparty());
                roadTransportDTO.setRequestInitiator(route.getOrders().stream().toList().get(0).getManager().split(";")[0]);
                roadTransportDTO.setDateRequestReceiving(route.getOrders().stream().toList().get(0).getDateCreate());
                roadTransportDTO.setCargoReadiness(route.getOrders().stream().toList().get(0).getFirstLoadSlot());
                roadTransportDTO.setLoadingOnRequest(route.getOrders().stream().toList().get(0).getFirstLoadSlot());
                roadTransportDTO.setUKZ(route.getOrders().stream().toList().get(0).getControl() ? "Необходима сверка УКЗ" : "Нет");
                Set<RouteHasShop> routeHasShops = route.getRoteHasShop();
                for (RouteHasShop routeHasShop : routeHasShops) {
                    if(routeHasShop.getPosition().contains("Выгрузка")){
                        roadTransportDTO.setUnloadingWarehouse(routeHasShop.getAddress());
                    }
                }
            }

            roadTransportDTO.setResponsibleLogist(route.getLogistInfo() != null ? route.getLogistInfo().split(";")[0] : null);
            roadTransportDTO.setActualLoading(route.getDateLoadActually());
            roadTransportDTO.setCarrier(route.getUser() == null ? null : route.getUser().getCompanyName());
            roadTransportDTO.setTenderParticipants(messageMap.get(route.getIdRoute().toString()) == null ? null : String.valueOf(messageMap.get(route.getIdRoute().toString()).size()));

            if (messageMap.get(route.getIdRoute().toString()) != null) {
                for (Message message : messageMap.get(route.getIdRoute().toString())) {
                    if (message.getStatus().equals("1") && message.getIdRoute() != null && message.getCurrency() != null) {
                        roadTransportDTO.setBid(message.getText());
                        roadTransportDTO.setBidCurrency(message.getCurrency());
                        roadTransportDTO.setBidComment(message.getComment());
                        break;
                    }
                }
            }

            roadTransportDTO.setTruckNumber(route.getTruck() == null ? null : route.getTruck().getNumTruck());
            roadTransportDTO.setTruckType(route.getTruck() == null ? null : route.getTruck().getTypeTrailer());
            roadTransportDTO.setTemperature(route.getTemperature());
            roadTransportDTO.setWeight(route.getTotalCargoWeight());
            roadTransportDTOList.add(roadTransportDTO);
        }
        java.util.Date forRoute2 = new java.util.Date();
        System.out.println(forRoute2.getTime() - forRoute1.getTime() + " ms - routes filling for");

        String appPath = request.getServletContext().getRealPath("");
        String folderPath = appPath + "resources/others/roadTransportReport.xlsx";

        java.util.Date poiexcel1 = new java.util.Date();

        try {
            poiExcel.generateRoadTransportReport(roadTransportDTOList, folderPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        java.util.Date poiexcel2 = new java.util.Date();
        System.out.println(poiexcel2.getTime() - poiexcel1.getTime() + " ms - excel");

        java.util.Date write1 = new java.util.Date();

        response.setHeader("content-disposition", "attachment;filename="+"roadTransportReport.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try (FileInputStream in = new FileInputStream(appPath + "resources/others/roadTransportReport.xlsx"); OutputStream out = response.getOutputStream()) {
            // Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
            //  Создать выходной поток
            //  Создать буфер
            byte buffer[] = new byte[1024];
            int len = 0;
            //  Прочитать содержимое входного потока в буфер в цикле
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.util.Date write2 = new java.util.Date();
        System.out.println(write2.getTime() - write1.getTime() + " ms - write");


    }
	
	/**
	 * Скачивание таблицы с актуальными ротациями
	 * @author Ira
	 */
	@RequestMapping("/rotations/get-actual-rotations-excel")
	@TimedExecution
	public void getActualRotationsExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
	    String appPath = request.getRealPath("/");

	    String filepath = appPath + "resources/others/actual-rotations.xlsx";

	    List<Rotation> rotations = rotationService.getActualRotations();
	    try {
	        poiExcel.generateActualRotationsExcel(rotations, filepath);
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    response.setHeader("content-disposition", "attachment;filename="+"actual-rotations.xlsx");
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	    response.setHeader("content-disposition", "attachment;filename="+"actual-rotations.xlsx");
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    try (FileInputStream in = new FileInputStream(filepath); OutputStream out = response.getOutputStream()) {
	        // Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
	        //  Создать выходной поток
	        //  Создать буфер
	        byte buffer[] = new byte[1024];
	        int len = 0;
	        //  Прочитать содержимое входного потока в буфер в цикле
	        while ((len = in.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Метод отвечает за скачивание документа инструкции для ротаций
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @author Ira
	 */
	@RequestMapping("/rotations/download/instruction-rotations")
	public void downloadRotationsHelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String appPath = request.getServletContext().getRealPath("");
	    //File file = new File(appPath + "resources/others/Speedlogist.apk");
	    response.setHeader("content-disposition", "attachment;filename="+"instruction-rotations.docx");
	    response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
	    FileInputStream in = null;
	    OutputStream out = null;
	    try {
	        // Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
	        in = new FileInputStream(appPath + "resources/others/docs/instruction-rotations.docx");
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
	}
	
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
     * Метод отвечает за формирование и скачку 330 отчёта сервис левела
     * @param request
     * @param from
     * @param to
     * @param stock
     * @param code
     * @return
     * @throws ParseException
     */
    @TimedExecution
	@GetMapping("/330/{from}&{to}&{stock}&{code}")
    public Map<String, Object> get330AndParam(HttpServletRequest request, HttpServletResponse servletResponse,
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable String stock,
            @PathVariable String code) throws ParseException {
         String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport330\", \"Data\": "
               + "{\"DateFrom\": \""+from+"\", "
               + "\"DateTo\": \""+to+"\", "
               + "\"WarehouseId\": ["+stock+"], "
               + "\"GoodsId\": ["+code+"]}}}";
         Map<String, Object> response = new HashMap<>();
         List<MarketDataFor330Responce> dataList330 = new ArrayList<MarketDataFor330Responce>();
         List<ReportRow> reportRows = new ArrayList<ReportRow>();
         try {
            mainRestController.checkJWT(mainRestController.marketUrl);
         } catch (Exception e) {
            System.err.println("Ошибка получения jwt токена");
         }
         JSONParser parser = new JSONParser();
         JSONObject jsonMainObject = (JSONObject) parser.parse(str);
         String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
         JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
         String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
         JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

         JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
         JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());

         String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
         String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
         Object[] warehouseId = warehouseIdArray.toArray();
         Object[] goodsId = goodsIdArray.toArray();

         MarketDataFor330Request for330Request = new MarketDataFor330Request(dateForm, dateTo, warehouseId, goodsId);
         MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetReport330", mainRestController.serviceNumber, for330Request);
         MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

         String marketOrder2 = null;
		try {
			marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace(); // тут просто выводим стектрейс. Продумать обработку с выводом на фронт! 
		}
         System.out.println(gson.toJson(requestDto));

         if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
            //в этом случае проверяем бд
            System.err.println("Связь с маркетом потеряна");
            response.put("status", "503");
            response.put("payload responce", marketOrder2);
            response.put("message", "Связь с маркетом потеряна");
            return response;

         }else{//если есть связь с маркетом
            JSONObject jsonResponceMainObject = (JSONObject) parser.parse(marketOrder2);
            JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
            for (Object obj : jsonResponceTable) {
                 dataList330.add(new MarketDataFor330Responce(obj.toString())); // парсин json засунул в конструктор
              }

         }

         if(dataList330.isEmpty()) {
        	 response.put("status", 100);
        	 response.put("message", "Данные по 330 отчёту из маркета не найдены");
        	 return response;
         }

//       for (MarketDataFor330Responce object : responces) {
//          System.out.println(object);
//       }

         // Получаем номера заказов
         List<String> uniqueOrderBuyGroupIds = dataList330.stream()
                  .map(MarketDataFor330Responce::getOrderBuyGroupId) // Получаем значения
                  .filter(id -> id != null) // Убираем null значения
                  .map(String::valueOf) // Преобразуем Long в String
                  .distinct() // Убираем дубликаты
                  .collect(Collectors.toList()); // Преобразуем обратно в список

         //получаем заказы по списку
         Map<String, Order> orders = orderService.getOrdersByListMarketNumber(uniqueOrderBuyGroupIds);



         //получаем даты заказов ОРЛ которые нам понадобятся
         List<Date> datesOrderORL = orders.values().stream()
                  .map(Order::getDateOrderOrl) // Получаем значения getDateOrderOrl
                  .filter(Objects::nonNull)   // Исключаем null
                  .map(date -> new java.sql.Date(date.getTime() - 86400000)) // Уменьшаем на 1 день
                  .distinct()                 // Убираем дубликаты
                  .collect(Collectors.toList()); // Сохраняем в список
         //получаем мапу с заказами орл
         /*
          * ключ - дата, значение - мапа с кодом товара и значением (как в методе orderProductService.getOrderProductMapHasDate(dateTarget))
          */
         
         Map<String, Map<Integer, OrderProduct>> mapOrderProduct = orderProductService.getOrderProductMapHasDateList(datesOrderORL);

         System.out.println("В мапе объектов : " + mapOrderProduct.size());
         mapOrderProduct.forEach((k,v)-> System.out.println("Для даты :" + k + " -> " + v.size() + " значений"));


         //подгатавливаем строку (собираем все нужные столбцы)
         for (MarketDataFor330Responce data330 : dataList330) {
            ReportRow reportRow = new ReportRow();
            reportRow.setProductName(data330.getGoodsName());
            reportRow.setProductCode(data330.getGoodsId());

            String period = Date.valueOf(from).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + Date.valueOf(to).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            reportRow.setPeriodOrderDelivery(period);
            reportRow.setOrderedUnitsORL(null); //сколько заказано ОРЛ

            if(data330.getOrderBuyGroupId() == null) { // ПОТОМ ОБРАБОТАТЬ КОГДА НЕТУ КОДА ИЗ МАРКЕТА В 330 ОТЧЁТЕ
               continue;
            }

            Order order = orders.get(data330.getOrderBuyGroupId().toString());
            if(order == null) {
               System.err.println("Заказа с номером " + data330.getOrderBuyGroupId() + " не найдено!");
               try {
            	   order = getMarketOrder(request, data330.getOrderBuyGroupId().toString()); // тянем простой ордер из маркета
				} catch (MarketConnectionException e) {
					if(e.getStatus() == 503) { // связь с маркетом потеряна
						response.put("status", 100);
	                    response.put("message", e.getMessage());
	                    return response;
					}else { // значит что общая ошибка
						reportRow.setComment(e.getMessage());
						reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
			            reportRow.setDateStart(Date.valueOf(from));
			            reportRow.setDateFinish(Date.valueOf(to));
			            reportRow.setCounterpartyName(data330.getContractorNameShort());
			            reportRow.setAcceptedUnits(data330.getQuantity().intValue());
			            reportRow.setStock(data330.getWarehouseId().toString());
			            reportRow.setDateUnload(data330.getDate3());
			            reportRow.setPrecentOrderFulfillment(0.0);
			            reportRow.setDiscrepancyQuantity(0);
						reportRows.add(reportRow);
						continue; // пропускаем остальные данные этого товара
					}
				}

	        }

            Map<Long, Double> productHasOrder = order.getOrderLinesMap();
//          System.out.println("---> Хочу взять: " + data330.getGoodsId() + " из заказа " + data330.getOrderBuyGroupId());
            if(!productHasOrder.containsKey(data330.getGoodsId())) {
               System.err.println("Отсутствует товар " + data330.getGoodsId() + " ("+ data330.getGoodsName()+ ") в заказе " + data330.getOrderBuyGroupId());
            }
            Long longGoodIdHas330 = data330.getGoodsId();
            Integer orderProductHasOrderManager;
            if(!productHasOrder.containsKey(longGoodIdHas330)) {
               Order orderFromMarket;
               if(order.getIdOrder() == null) { // если id == null это значит что ордер уже вытянут из маркета
                  orderFromMarket = order;
               }else {
                  try {
                     orderFromMarket = getMarketOrder(request, data330.getOrderBuyGroupId().toString());
                  } catch (MarketConnectionException e) {
                     response.put("status", 100);
                     response.put("message", e.getMessage());
                     return response;
                  }
               }

               if (orderFromMarket == null) {
                  System.err.println("orderFromMarket == null. Возможно, связь с макетом была потеряна.");
                  response.put("message", "Возможно, связь с маркетом потеряна. Попробуйте создать отчёт ещё раз.");
                  return response;
               }
               
               if(!orderFromMarket.getOrderLinesMap().containsKey(longGoodIdHas330)) { // если и в заказе из маркета нет и в заказе из SL нет - записываем коммент
                  reportRow.setComment("Товара нет в базе данных SpeedLogist и Маркета.");
                  orderProductHasOrderManager = 0;
               }else {
                  orderProductHasOrderManager = orderFromMarket.getOrderLinesMap().get(longGoodIdHas330).intValue();
                  System.out.println("Товара " +data330.getGoodsName() + " ("+ data330.getGoodsId() + ") не было в заказе, который хранится в базе данных SpeedLogist. Однако был в заказе базы данных Маркета" );
                  reportRow.setComment("Товара не было в заказе, который хранится в базе данных SpeedLogist. Однако был в заказе базы данных Маркета");
               }
               
            }else {
               orderProductHasOrderManager = productHasOrder.get(longGoodIdHas330).intValue();
            }
            reportRow.setOrderedUnitsManager(orderProductHasOrderManager); // сколько заказано менеджером
            
            OrderProduct orderORL;
            Integer intOrderORL;
            if(order.getDateOrderOrl() != null) {
            	reportRow.setDateOrderORL(order.getDateOrderOrl());
               Date dateTarget = Date.valueOf(order.getDateOrderOrl().toLocalDate().minusDays(1)); 
               Map<Integer, OrderProduct> mapOrderProductTarget = mapOrderProduct.get(dateTarget.toString());

//             mapOrderProduct.forEach((k,v)-> System.out.println("Для даты :" + k + " -> " + k.toString().equals(dateTarget.toString()) + " значений"));
               orderORL = mapOrderProductTarget.get(data330.getGoodsId().intValue()) != null ? mapOrderProductTarget.get(data330.getGoodsId().intValue()) : null;
               if(dateTarget.toLocalDate().isBefore(LocalDate.parse("2024-12-08")) && orderORL!= null) {//тут искллючения на прошлые даты, когда не было двух складов
                  intOrderORL = orderORL.getQuantity();
               }else {
                  if(orderORL!= null) {
                     switch (data330.getWarehouseId().toString()) {
                     case "1700":
                        intOrderORL = orderORL.getQuantity1700();
                        break;
                     case "1800":
                        intOrderORL = orderORL.getQuantity1800();
                        break;
                     default:
                        intOrderORL = orderORL.getQuantity();
                        break;
                     }
                  }else {
                     intOrderORL = 0;
                  }
               }        
               reportRow.setOrderedUnitsORL(intOrderORL);// сколько заказано ОРЛ
            }else {
               if(order.getIdOrder() != null) {
                  reportRow.setComment("В заказе не стоит дата расчёта ОРЛ");
               }else {
                  reportRow.setComment("Заказ не найден в базе данных SpeedLogist. Расчёт заказа ОРЛ невозможен");
               }
               
            }
            
            reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
            reportRow.setDateStart(Date.valueOf(from));
            reportRow.setDateFinish(Date.valueOf(to));
            reportRow.setCounterpartyName(data330.getContractorNameShort());
            reportRow.setAcceptedUnits(data330.getQuantity().intValue());
            reportRow.setStock(data330.getWarehouseId().toString());
            reportRow.setDateUnload(data330.getDate3());
            
            
            //тут блок расчёта процентов для каждой строки и всяких разниц
            /*
             * Рассчитываем % Выполнения заказа
             */
//          Double precentOrderFulfillment = (reportRow.getOrderedUnitsManager().doubleValue()/reportRow.getAcceptedUnits().doubleValue()*100);
            Double precentOrderFulfillment;
            if(reportRow.getOrderedUnitsManager().doubleValue() == 0) {
               precentOrderFulfillment = 9999.0;
            }else {
               precentOrderFulfillment = (reportRow.getAcceptedUnits().doubleValue()/reportRow.getOrderedUnitsManager().doubleValue()*100);
            }
             
            reportRow.setPrecentOrderFulfillment(roundВouble(precentOrderFulfillment, 2));       
               
            /*
             * рассчитываем Расхождение кол-во
             */
            reportRow.setDiscrepancyQuantity(reportRow.getOrderedUnitsManager()-reportRow.getAcceptedUnits());
            
            reportRows.add(reportRow);
         }
         
         //записываем строки в ексель
         String appPath = request.getServletContext().getRealPath("");
          String folderPath = appPath + "resources/others/report330.xlsx";
          
          try {
            poiExcel.generateExcelReport(reportRows, folderPath);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         servletResponse.setHeader("content-disposition", "attachment;filename="+"report330.xlsx");
         servletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

         try (FileInputStream in = new FileInputStream(folderPath); OutputStream out = servletResponse.getOutputStream();){

            byte buffer[] = new byte[1024];
            int len = 0;
            //  Прочитать содержимое входного потока в буфер в цикле
            while ((len = in.read(buffer)) > 0) {
               out.write(buffer, 0, len);
            }

         } catch (Exception e) {
            e.printStackTrace();
         }

         return null;
      }
    
    /**
     * Метод отвечает за формирование и скачку 330 отчёта сервис левела
     * <br>Разница в том, что запросы в маркет по заказам идут по списку, а не по одному
     *  Версия 1.2 
     * @param request
     * @param servletResponse
     * @param from
     * @param to
     * @param stock
     * @param code
     * @return
     * @throws ParseException
     * 
     */
    @GetMapping("/330V2/{from}&{to}&{stock}&{code}")
    public Map<String, Object> get330AndParamV2(HttpServletRequest request, HttpServletResponse servletResponse,
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable String stock,
            @PathVariable String code) throws ParseException {
         String str = "{\"CRC\": \"\", \"Packet\": {\"MethodName\": \"SpeedLogist.GetReport330\", \"Data\": "
               + "{\"DateFrom\": \""+from+"\", "
               + "\"DateTo\": \""+to+"\", "
               + "\"WarehouseId\": ["+stock+"], "
               + "\"GoodsId\": ["+code+"]}}}";
         Map<String, Object> response = new HashMap<>();
         List<MarketDataFor330Responce> dataList330 = new ArrayList<MarketDataFor330Responce>();
         List<ReportRow> reportRows = new ArrayList<ReportRow>();
         try {
            mainRestController.checkJWT(mainRestController.marketUrl);
         } catch (Exception e) {
            System.err.println("Ошибка получения jwt токена");
         }
         JSONParser parser = new JSONParser();
         JSONObject jsonMainObject = (JSONObject) parser.parse(str);
         String marketPacketDtoStr = jsonMainObject.get("Packet") != null ? jsonMainObject.get("Packet").toString() : null;
         JSONObject jsonMainObject2 = (JSONObject) parser.parse(marketPacketDtoStr);
         String marketDataFor398RequestStr = jsonMainObject2.get("Data") != null ? jsonMainObject2.get("Data").toString() : null;
         JSONObject jsonMainObjectTarget = (JSONObject) parser.parse(marketDataFor398RequestStr);

         JSONArray warehouseIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("WarehouseId").toString());
         JSONArray goodsIdArray = (JSONArray) parser.parse(jsonMainObjectTarget.get("GoodsId").toString());

         String dateForm = jsonMainObjectTarget.get("DateFrom") == null ? null : jsonMainObjectTarget.get("DateFrom").toString();
         String dateTo = jsonMainObjectTarget.get("DateTo") == null ? null : jsonMainObjectTarget.get("DateTo").toString();
         Object[] warehouseId = warehouseIdArray.toArray();
         Object[] goodsId = goodsIdArray.toArray();

         MarketDataFor330Request for330Request = new MarketDataFor330Request(dateForm, dateTo, warehouseId, goodsId);
         MarketPacketDto marketPacketDto = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetReport330", mainRestController.serviceNumber, for330Request);
         MarketRequestDto requestDto = new MarketRequestDto("", marketPacketDto);

         String market330JSON = null;
		try {
			market330JSON = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto));
		} catch (Exception e) {
			e.printStackTrace(); // тут просто выводим стектрейс. Продумать обработку с выводом на фронт! 
		}
//         System.out.println(gson.toJson(requestDto));

         if(market330JSON.equals("503")) { // означает что связь с маркетом потеряна
            //в этом случае проверяем бд
            System.err.println("Связь с маркетом потеряна");
            response.put("status", "503");
            response.put("payload responce", market330JSON);
            response.put("message", "Связь с маркетом потеряна");
            return response;

         }else{//если есть связь с маркетом
            JSONObject jsonResponceMainObject = (JSONObject) parser.parse(market330JSON);
            JSONArray jsonResponceTable = (JSONArray) parser.parse(jsonResponceMainObject.get("Table").toString());
            for (Object obj : jsonResponceTable) {
                 dataList330.add(new MarketDataFor330Responce(obj.toString())); // парсин json засунул в конструктор
              }

         }

         if(dataList330.isEmpty()) {
        	 response.put("status", 100);
        	 response.put("message", "Данные по 330 отчёту из маркета не найдены");
        	 return response;
         }

//       for (MarketDataFor330Responce object : responces) {
//          System.out.println(object);
//       }

         // Получаем номера заказов
         List<String> uniqueOrderBuyGroupIds = dataList330.stream()
                  .map(MarketDataFor330Responce::getOrderBuyGroupId) // Получаем значения
                  .filter(id -> id != null) // Убираем null значения
                  .map(String::valueOf) // Преобразуем Long в String
                  .distinct() // Убираем дубликаты
                  .collect(Collectors.toList()); // Преобразуем обратно в список

         //получаем заказы по списку
         Map<String, Order> ordersFromMyDB = orderService.getOrdersByListMarketNumber(uniqueOrderBuyGroupIds);
         
         //тут делаем массовый запрос в маркет
         String uniqueOrderFromMarket = String.join(",", uniqueOrderBuyGroupIds);
         
         //спрашиваем у маркета целый пулл заказов
         Map<String, Order> ordersFromMarket = null;
		try {
			ordersFromMarket = marketAPI.getMarketOrders(uniqueOrderFromMarket);
		} catch (Exception e) {
			e.printStackTrace(); // тут просто выводим стектрейс. Продумать обработку с выводом на фронт! 
		}
         
//         ordersFromMarket.forEach((k,v) -> System.out.println(k + " - " + v));

         //получаем даты заказов ОРЛ которые нам понадобятся
         List<Date> datesOrderORL = ordersFromMyDB.values().stream()
                  .map(Order::getDateOrderOrl) // Получаем значения getDateOrderOrl
                  .filter(Objects::nonNull)   // Исключаем null
                  .map(date -> new java.sql.Date(date.getTime() - 86400000)) // Уменьшаем на 1 день
                  .distinct()                 // Убираем дубликаты
                  .collect(Collectors.toList()); // Сохраняем в список
         //получаем мапу с заказами орл
         /*
          * ключ - дата, значение - мапа с кодом товара и значением (как в методе orderProductService.getOrderProductMapHasDate(dateTarget))
          */
         
         Map<String, Map<Integer, OrderProduct>> mapOrderProduct = orderProductService.getOrderProductMapHasDateList(datesOrderORL);

         System.out.println("В мапе объектов : " + mapOrderProduct.size());
         mapOrderProduct.forEach((k,v)-> System.out.println("Для даты :" + k + " -> " + v.size() + " значений"));


         //подгатавливаем строку (собираем все нужные столбцы)
         for (MarketDataFor330Responce data330 : dataList330) {
            ReportRow reportRow = new ReportRow();
            reportRow.setProductName(data330.getGoodsName());
            reportRow.setProductCode(data330.getGoodsId());

            String period = Date.valueOf(from).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + Date.valueOf(to).toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            reportRow.setPeriodOrderDelivery(period);
            reportRow.setOrderedUnitsORL(null); //сколько заказано ОРЛ

            if(data330.getOrderBuyGroupId() == null) { // ПОТОМ ОБРАБОТАТЬ КОГДА НЕТУ КОДА ИЗ МАРКЕТА В 330 ОТЧЁТЕ
               continue;
            }

            Order order = ordersFromMyDB.get(data330.getOrderBuyGroupId().toString());
            if(order == null) {
               System.err.println("Заказа с номером " + data330.getOrderBuyGroupId() + "в базе данных не найдено!");
               order = ordersFromMarket.get(data330.getOrderBuyGroupId().toString());
               if(order == null) {
					reportRow.setComment("Заказ отсутствует в БД маркета и в БД Speedlogist");
					reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
		            reportRow.setDateStart(Date.valueOf(from));
		            reportRow.setDateFinish(Date.valueOf(to));
		            reportRow.setCounterpartyName(data330.getContractorNameShort());
		            reportRow.setAcceptedUnits(data330.getQuantity().intValue());
		            reportRow.setStock(data330.getWarehouseId().toString());
		            reportRow.setDateUnload(data330.getDate3());
		            reportRow.setPrecentOrderFulfillment(0.0);
		            reportRow.setDiscrepancyQuantity(0);
					reportRows.add(reportRow);
					continue; // пропускаем остальные данные этого товара
               }
               
//               try {
//            	   order = getMarketOrder(request, data330.getOrderBuyGroupId().toString()); // тянем простой ордер из маркета ТУТ ЗАПРОС В МАРКЕТ!!!!
//				} catch (MarketConnectionException e) {
//					if(e.getStatus() == 503) { // связь с маркетом потеряна
//						response.put("status", 100);
//	                    response.put("message", e.getMessage());
//	                    return response;
//					}else { // значит что общая ошибка
//						reportRow.setComment(e.getMessage());
//						reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
//			            reportRow.setDateStart(Date.valueOf(from));
//			            reportRow.setDateFinish(Date.valueOf(to));
//			            reportRow.setCounterpartyName(data330.getContractorNameShort());
//			            reportRow.setAcceptedUnits(data330.getQuantity().intValue());
//			            reportRow.setStock(data330.getWarehouseId().toString());
//			            reportRow.setDateUnload(data330.getDate3());
//			            reportRow.setPrecentOrderFulfillment(0.0);
//			            reportRow.setDiscrepancyQuantity(0);
//						reportRows.add(reportRow);
//						continue; // пропускаем остальные данные этого товара
//					}
//				}

	        }

            Map<Long, Double> productHasOrder = order.getOrderLinesMap();
//          System.out.println("---> Хочу взять: " + data330.getGoodsId() + " из заказа " + data330.getOrderBuyGroupId());
            if(!productHasOrder.containsKey(data330.getGoodsId())) {
               System.err.println("Отсутствует товар " + data330.getGoodsId() + " ("+ data330.getGoodsName()+ ") в заказе " + data330.getOrderBuyGroupId());
            }
            Long longGoodIdHas330 = data330.getGoodsId();
            Integer orderProductHasOrderManager;
            if(!productHasOrder.containsKey(longGoodIdHas330)) {
               Order orderFromMarket;
               if(order.getIdOrder() == null) { // если id == null это значит что ордер уже вытянут из маркета
                  orderFromMarket = order;
               }else {
            	   orderFromMarket = ordersFromMarket.get(data330.getOrderBuyGroupId().toString());
//                  try {
//                     orderFromMarket = getMarketOrder(request, data330.getOrderBuyGroupId().toString());
//                  } catch (MarketConnectionException e) {
//                     response.put("status", 100);
//                     response.put("message", e.getMessage());
//                     return response;
//                  }
               }

               if (orderFromMarket == null) {
                  System.err.println("orderFromMarket == null. Возможно, связь с макетом была потеряна.");
                  response.put("message", "Возможно, связь с маркетом потеряна. Попробуйте создать отчёт ещё раз.");
                  return response;
               }
               
               if(!orderFromMarket.getOrderLinesMap().containsKey(longGoodIdHas330)) { // если и в заказе из маркета нет и в заказе из SL нет - записываем коммент
                  reportRow.setComment("Товара нет в базе данных SpeedLogist и Маркета.");
                  orderProductHasOrderManager = 0;
               }else {
                  orderProductHasOrderManager = orderFromMarket.getOrderLinesMap().get(longGoodIdHas330).intValue();
                  System.out.println("Товара " +data330.getGoodsName() + " ("+ data330.getGoodsId() + ") не было в заказе, который хранится в базе данных SpeedLogist. Однако был в заказе базы данных Маркета" );
                  reportRow.setComment("Товара не было в заказе, который хранится в базе данных SpeedLogist. Однако был в заказе базы данных Маркета");
               }
               
            }else {
               orderProductHasOrderManager = productHasOrder.get(longGoodIdHas330).intValue();
            }
            reportRow.setOrderedUnitsManager(orderProductHasOrderManager); // сколько заказано менеджером
            
            OrderProduct orderORL;
            Integer intOrderORL;
            if(order.getDateOrderOrl() != null) {
            	reportRow.setDateOrderORL(order.getDateOrderOrl());
               Date dateTarget = Date.valueOf(order.getDateOrderOrl().toLocalDate().minusDays(1)); 
               Map<Integer, OrderProduct> mapOrderProductTarget = mapOrderProduct.get(dateTarget.toString());

//             mapOrderProduct.forEach((k,v)-> System.out.println("Для даты :" + k + " -> " + k.toString().equals(dateTarget.toString()) + " значений"));
               orderORL = mapOrderProductTarget.get(data330.getGoodsId().intValue()) != null ? mapOrderProductTarget.get(data330.getGoodsId().intValue()) : null;
               if(dateTarget.toLocalDate().isBefore(LocalDate.parse("2024-12-08")) && orderORL!= null) {//тут искллючения на прошлые даты, когда не было двух складов
                  intOrderORL = orderORL.getQuantity();
               }else {
                  if(orderORL!= null) {
                     switch (data330.getWarehouseId().toString()) {
                     case "1700":
                        intOrderORL = orderORL.getQuantity1700();
                        break;
                     case "1800":
                        intOrderORL = orderORL.getQuantity1800();
                        break;
                     default:
                        intOrderORL = orderORL.getQuantity();
                        break;
                     }
                  }else {
                     intOrderORL = 0;
                  }
               }        
               reportRow.setOrderedUnitsORL(intOrderORL);// сколько заказано ОРЛ
            }else {
               if(order.getIdOrder() != null) {
                  reportRow.setComment("В заказе не стоит дата расчёта ОРЛ");
               }else {
                  reportRow.setComment("Заказ не найден в базе данных SpeedLogist. Расчёт заказа ОРЛ невозможен");
               }
               
            }
            
            reportRow.setMarketNumber(data330.getOrderBuyGroupId().toString());
            reportRow.setDateStart(Date.valueOf(from));
            reportRow.setDateFinish(Date.valueOf(to));
            reportRow.setCounterpartyName(data330.getContractorNameShort());
            reportRow.setAcceptedUnits(data330.getQuantity().intValue());
            reportRow.setStock(data330.getWarehouseId().toString());
            reportRow.setDateUnload(data330.getDate3());
            
            
            //тут блок расчёта процентов для каждой строки и всяких разниц
            /*
             * Рассчитываем % Выполнения заказа
             */
//          Double precentOrderFulfillment = (reportRow.getOrderedUnitsManager().doubleValue()/reportRow.getAcceptedUnits().doubleValue()*100);
            Double precentOrderFulfillment;
            if(reportRow.getOrderedUnitsManager().doubleValue() == 0) {
               precentOrderFulfillment = 9999.0;
            }else {
               precentOrderFulfillment = (reportRow.getAcceptedUnits().doubleValue()/reportRow.getOrderedUnitsManager().doubleValue()*100);
            }
             
            reportRow.setPrecentOrderFulfillment(roundВouble(precentOrderFulfillment, 2));       
               
            /*
             * рассчитываем Расхождение кол-во
             */
            reportRow.setDiscrepancyQuantity(reportRow.getOrderedUnitsManager()-reportRow.getAcceptedUnits());
            
            reportRows.add(reportRow);
         }
         
         //записываем строки в ексель
         String appPath = request.getServletContext().getRealPath("");
          String folderPath = appPath + "resources/others/report330.xlsx";
          
          try {
            poiExcel.generateExcelReportV1_2(reportRows, folderPath);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

         servletResponse.setHeader("content-disposition", "attachment;filename="+"report330.xlsx");
         servletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

         try (FileInputStream in = new FileInputStream(folderPath); OutputStream out = servletResponse.getOutputStream();){

            byte buffer[] = new byte[1024];
            int len = 0;
            //  Прочитать содержимое входного потока в буфер в цикле
            while ((len = in.read(buffer)) > 0) {
               out.write(buffer, 0, len);
            }

         } catch (Exception e) {
            e.printStackTrace();
         }

         return null;
      }
    
    
	private Order getMarketOrder(HttpServletRequest request, String idMarket) throws MarketConnectionException {		
		try {			
			mainRestController.checkJWT(mainRestController.marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Object> response = new HashMap<String, Object>();
		MarketDataForRequestDto dataDto3 = new MarketDataForRequestDto(idMarket);
		MarketPacketDto packetDto3 = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.GetOrderBuyInfo", mainRestController.serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2 = null;
		try {
			marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto3));
		} catch (Exception e) {
			e.printStackTrace(); // тут просто выводим стектрейс. Продумать обработку с выводом на фронт! 
		}
		
//		System.out.println(marketOrder2);
		
		if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
			System.err.println("Связь с маркетом потеряна");	
			throw new MarketConnectionException("Связь с маркетом потеряна", 503);
		}else{//если есть связь с маркетом
			//проверяем на наличие сообщений об ошибке со стороны маркета
			if(marketOrder2.contains("Error")) {
				MarketErrorDto errorMarket = gson.fromJson(marketOrder2, MarketErrorDto.class);
				System.err.println("Error: " + marketOrder2);
				throw new MarketConnectionException("Error: " + errorMarket, 500);
//				return null;
			}
			
			//тут избавляемся от мусора в json
			String str2 = marketOrder2.split("\\[", 2)[1];
			String str3 = str2.substring(0, str2.length()-2);
			
			//создаём свой парсер и парсим json в объекты, с которыми будем работать.
			CustomJSONParser customJSONParser = new CustomJSONParser();
			
			//создаём OrderBuyGroup
			OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
						
			//создаём Order, записываем в бд и возвращаем или сам ордер или ошибку (тот же ордер, только с отрицательным id)
			Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
			return order;
		}
	}
    
    /**
	 * Метод отвечает за скачивание документа инструкции для графика поставок для ТО
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/delivery-schedule-to/downdoad/instruction-trading-objects")
	public void downdoadIncotermsInsuranceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"instruction-trading-objects.docx");
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/instruction-trading-objects.docx");
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
	}

	/**
	 * метод отвечает за скачку иснтрукции из слотов (в частности по объеденению заказов)
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/slot/downdoad/instruction-join")
	public void downdoadInstructionJoin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
		//File file = new File(appPath + "resources/others/Speedlogist.apk");
		response.setHeader("content-disposition", "attachment;filename="+"instruction-join.docx");
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Прочтите файл, который нужно загрузить, и сохраните его во входном потоке файла
			in = new FileInputStream(appPath + "resources/others/docs/instruction-join.docx");
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
	}

	@RequestMapping("/orl/download/zip398")
	public void downloadInstructionArchive(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    String appPath = request.getServletContext().getRealPath("");
	    String targetDirectoryPath = appPath + "resources/others/398/";
	    String archivePath = targetDirectoryPath + "398.zip";

	    File targetDirectory = new File(targetDirectoryPath);
	    File archiveFile = new File(archivePath);

	    // Если архив не существует, создаем его
	    if (!archiveFile.exists()) {
	        try (FileOutputStream fos = new FileOutputStream(archiveFile);
	             ZipOutputStream zos = new ZipOutputStream(fos)) {

	            // Получаем все файлы из целевой директории
	            File[] filesToInclude = targetDirectory.listFiles();
	            if (filesToInclude != null) {
	                for (File file : filesToInclude) {
	                    if (file.isFile() && !file.getName().equals("398.zip")) { // Исключаем сам архив
	                        try (FileInputStream fis = new FileInputStream(file)) {
	                            // Добавляем файл в архив
	                            ZipEntry zipEntry = new ZipEntry(file.getName());
	                            zos.putNextEntry(zipEntry);

	                            byte[] buffer = new byte[1024];
	                            int len;
	                            while ((len = fis.read(buffer)) > 0) {
	                                zos.write(buffer, 0, len);
	                            }
	                            zos.closeEntry();
	                        }
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new IOException("Error creating the archive file");
	        }
	    }

	    // Устанавливаем заголовки для скачивания
	    response.setHeader("content-disposition", "attachment;filename=398.zip");
	    response.setContentType("application/zip");

	    // Передаем архив в поток
	    try (FileInputStream in = new FileInputStream(archiveFile);
	         OutputStream out = response.getOutputStream()) {

	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = in.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new IOException("Error downloading the archive file");
	    }
	}

	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByLoginV2(name);
		return user;
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
	
	// округляем числа до 2-х знаков после запятой
		private static double roundВouble(double value, int places) {
			double scale = Math.pow(10, places);
			return Math.round(value * scale) / scale;
		}

}
