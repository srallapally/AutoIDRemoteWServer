package com.forgerock.autoid.remote.wsconnectorserver;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class AutoIDWorkServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(AutoIDWorkHandler.class);
        //webSocketServletFactory.getPolicy().setIdleTimeout(3000);
    }
}
