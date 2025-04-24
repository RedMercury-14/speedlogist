package by.base.main.util;

import by.base.main.coders.MessageDecoder;
import by.base.main.coders.MessageEncoder;
import by.base.main.model.Message;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Component
@ServerEndpoint(value = "/api/carrier/tenders/get-bid", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class},
        configurator = SpringConfigurator.class)
public class CarrierTenderWebSocket {

    private Session session = null;
    public static List<Session> sessionList = new LinkedList<>();
//	public static List<Message> internationalMessegeList = new ArrayList<Message>(); //лист с сообщениями (предложениями) от перевозчиков (международников)


    @OnOpen
    public void onOpen(Session session) throws IOException {
//		System.out.println("SlotTsdWebSocket подключение пользователя: " + session.getUserPrincipal().getName());
        this.session = session;
        if(sessionList.isEmpty()) {
            sessionList.add(session);
            sendSimpleMessage(session, "120", "Session opened");
        }else if(!sessionList.contains(session)){
            sessionList.add(session);
            sendSimpleMessage(session, "120", "Session opened");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp + " TenderWebSocket: всего сессий: " + sessionList.size());
    }

    @OnClose
    public void onClose(Session session) {
        System.err.println("TenderWebSocket: onClose");
        sessionList.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        onClose(session);
        throwable.printStackTrace();
    }


    @OnMessage
    public void onMessage(Session session, Message message) throws ParseException, IOException, EncodeException {

//        Message message = new Message();
//        Message.setContent("Сообщение получено");
        session.getBasicRemote().sendObject(message);

    }

    public void sendMessage (Message message) {

        sessionList.forEach(s -> {
            try {
                if(s!= null) {
                    s.getBasicRemote().sendObject(message);
                }
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });

    }

    public void setMessageByTimer(Message message) {
        sessionList.forEach(s->{
            try {
                s.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendSimpleMessage(Session session, String status, String message) {
        Message errorMessage = new Message();
//        errorMessage.setStatus(status);
//        errorMessage.setContent(message);
        try {
            session.getBasicRemote().sendObject(errorMessage);
        } catch (IOException | EncodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendPingToAll() {
        for (Session session : sessionList) {
            if (session.isOpen()) {
                try {
                    ByteBuffer payload = ByteBuffer.wrap("ping".getBytes());
                    session.getBasicRemote().sendPing(payload);
                    System.out.println("Ping -> " + session.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnMessage
    public void onPong(PongMessage pongMessage, Session session) {
        ByteBuffer data = pongMessage.getApplicationData();
        System.out.println("PONG получен от: " + session.getId() + ", данные: " + new String(data.array()));
    }

}
