package by.base.main.util;

import by.base.main.model.Message;
import by.base.main.model.Role;
import by.base.main.model.User;
import by.base.main.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CarrierTenderWebSocket extends TextWebSocketHandler {

    private static final Map<String, ArrayList<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    private static final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private static ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private UserService userService;
    
    /**
     * отправляет всем кроме перевозов
     * @param message
     */
    public void broadcastWithoutCarriers(Message message) {
        Set<String> userLogins = userSessions.keySet();
        for (String userLogin : userLogins) {
            User user = userService.getUserByLogin(userLogin);
            Set<Role> roles = user.getRoles().stream().filter(r -> r.getAuthority().equals("ROLE_CARRIER")).collect(Collectors.toSet());
            if(roles.isEmpty()) {
                sendToUser(userLogin, message);
            }
        }
    }

    /**
     * Метод, который используется при начале WebSocket сессии
     * @author Ira
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = getUsername(session);
        if (username != null) {
            if (!userSessions.containsKey(username)) {
                ArrayList<WebSocketSession> sessions = new ArrayList<>();
                sessions.add(session);
                userSessions.put(username, sessions);
            } else {
                ArrayList<WebSocketSession> sessions = userSessions.get(username);
                sessions.add(session);
                userSessions.put(username, sessions);
            }
        }
        sessions.add(session);
//        System.out.println("All sessions: " + sessions.size());
//        System.out.println("User sessions: " + userSessions.size());
    }

    /**
     * Метод, который используется при окончании WebSocket сессии
     * @author Ira
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Map.Entry<String, ArrayList<WebSocketSession>> entry : userSessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                entry.getValue().remove(session);
            }
        }
//        userSessions.values().removeIf(s -> s.getId().equals(session.getId()));
//        sessions.remove(session);
//        System.out.println("All sessions: " + sessions.size());
//        System.out.println("User sessions: " + userSessions.size());
    }

    /**
     * Метод дял отправки сообщения одному пользователю по логину
     * @author Ira
     */
    public void sendToUser(String username, Message message) {
        List<WebSocketSession> sessions = userSessions.get(username.toLowerCase());
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session != null && session.isOpen()) {
                    try {
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        String json = mapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(json));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Метод для отправки сообщений всем сессиям
     * @author Ira
     */
    public void broadcast(Message message) {
        try {
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String json = mapper.writeValueAsString(message);
            TextMessage msg = new TextMessage(json);
            for(Map.Entry<String, ArrayList<WebSocketSession>> entry : userSessions.entrySet()) {
                for (WebSocketSession session : entry.getValue()) {
                    if (session.isOpen()) {
                        session.sendMessage(msg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для отправки сообщения всем, но с исключением
     * @author Ira
     */
    public void broadcastWithException(String exceptionLogin, Message message) {
        try {
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String json = mapper.writeValueAsString(message);
            TextMessage msg = new TextMessage(json);

            for (Map.Entry<String, ArrayList<WebSocketSession>> entry : userSessions.entrySet()) {
                if (!entry.getKey().equals(exceptionLogin.toLowerCase())) {
                    message.setToUser(entry.getKey());
                    for (WebSocketSession session : entry.getValue()) {
                        if (session.isOpen()) {
                            session.sendMessage(msg);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения логина из сессии
     * @author Ira
     */
    private String getUsername(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery(); // user=user123
            if (query != null && query.startsWith("user=")) {
                return query.substring("user=".length());
            }
        }
        return null;
    }
}
