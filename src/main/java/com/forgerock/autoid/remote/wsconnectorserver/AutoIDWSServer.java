package com.forgerock.autoid.remote.wsconnectorserver;
import java.io.IOException;
import java.util.logging.Logger;
import javax.websocket.CloseReason;

import javax.websocket.OnClose;

import javax.websocket.OnMessage;

import javax.websocket.OnOpen;

import javax.websocket.Session;

import javax.websocket.CloseReason.CloseCodes;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/autoidserver")
public class AutoIDWSServer {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected ... " + session.getId());
        logger.info("Connected ... " + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println(" Got the message ... " + session.getId()+"::"+message);
        switch (message) {
            case "quit":
                try {
                    session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Game ended"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        return message;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(String.format("Session %s closed because of %s", session.getId(), closeReason));
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}
