package by.base.main.util;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        // Извлекаем имя пользователя из параметров запроса
        URI uri = request.getURI();
        String query = uri.getQuery(); // user=username123

        if (query != null && query.startsWith("user=")) {
            String username = query.substring("user=".length());
            return () -> username; // создаем анонимный Principal с этим именем
        }

        return null; // или можно кинуть исключение, если имя обязательно
    }
}
