package com.mikuac.shiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * <p>ShiroTestApplication class.</p>
 *
 * @author zero
 * @version $Id: $Id
 * @since 1.3.7
 */
@SpringBootApplication
public class ShiroTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroTestApplication.class, args);
    }


}

