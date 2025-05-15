package by.base.main.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class CarrierTenderWebSocketHandler implements WebSocketConfigurer {

    private final CustomHandshakeHandler handshakeHandler;

    public CarrierTenderWebSocketHandler(CustomHandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CarrierTenderWebSocket(), "/tender-message")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*"); // можно задать нужные origin
    }
}