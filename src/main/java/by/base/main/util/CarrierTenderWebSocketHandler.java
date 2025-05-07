package by.base.main.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class CarrierTenderWebSocketHandler implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CarrierTenderWebSocket(), "/tender-message")
                .setAllowedOrigins("*"); // можно задать нужные origin
    }
}