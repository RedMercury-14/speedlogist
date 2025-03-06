package by.base.main.controller.ajax;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.base.main.aspect.TimedExecution;
import by.base.main.dto.AuthRequest;
import by.base.main.dto.AuthResponse;
import by.base.main.security.JwtTokenProvider;
import by.base.main.service.ScheduleService;

@RestController
@RequestMapping(path = "fusion", produces = "application/json")
public class FusionController {
	
	private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Autowired
    ScheduleService scheduleService; 
    
    public FusionController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

	@GetMapping("/echo")
    public Map<String, Object> getTastList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "200");
		responseMap.put("message", "echo");
		responseMap.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
		return responseMap;
    }
	
	@PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
		
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails.getUsername());

        return new AuthResponse(token);
    }
	
	@GetMapping("/schedule/getListTOAll")
	public Map<String, Object> getListDeliveryScheduleTO(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", "200");
		response.put("body", scheduleService.getSchedulesListTOAll());
		return response;		
	}
	
	@GetMapping("/help")
	public Map<String, Object> getHelp(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("status", "200");
		response.put("/schedule/getListTOAll", "Метод возвращает все графики поставок и временные и удалённые и т.д. (status: 0 - удалён; 10 - создан, ожидает подтверждения; 20 - в работе)");
		return response;		
	}
	
	
}
