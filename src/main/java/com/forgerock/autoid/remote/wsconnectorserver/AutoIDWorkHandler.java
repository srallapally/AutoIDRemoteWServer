package com.forgerock.autoid.remote.wsconnectorserver;

import com.forgerock.autoid.remote.utils.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoIDWorkHandler extends WebSocketAdapter {
    private static final Logger LOG = Log.getLogger(AutoIDWorkHandler.class);
    private Session session;
    private String sessionId;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
     @Override
    public void onWebSocketConnect(Session session){
         System.out.println("WebSocket Connect: {}" + session);
         super.onWebSocketConnect(session);
         this.session = session;
         sessionId = UUID.randomUUID().toString();
         final AgentSession agentSession = new AgentSession(sessionId,null,session);
         Repository.getInstance().connectClient(agentSession);
         LOG.debug("WebSocket Connect: {}",session);
         Message ack = new Message();
         ack.op = Message.OP_ACK;
         ack.agentId = null;
         ack.sessionId = sessionId;
         Gson gson = new Gson();
         getSession().getRemote().sendStringByFuture(gson.toJson(ack, Message.class));
         //getRemote().sendStringByFuture("You are now connected to " + this.getClass().getName()+ "with session "+sessionId);
         executorService.scheduleAtFixedRate(() -> {
                     Iterator itr = Repository.getInstance().getAllSessions().entrySet().iterator();
                     while (itr.hasNext()) {
                         Map.Entry<String, AgentSession> entry = (Map.Entry<String, AgentSession>) itr.next();
                         String data = "Pong";
                         ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
                         try {
                             entry.getValue().getSession().getRemote().sendPong(payload);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 },
                 5, 5, TimeUnit.MINUTES);
     }
     public void onWebSocketClose(int statusCode, String reason){
         LOG.info("Close called ::");
         Repository.getInstance().disconnectClient(session);
         this.session = null;
         LOG.info("Close connection "+statusCode+", "+reason);
         super.onWebSocketClose(statusCode,reason);
         LOG.info("WebSocket Close: {} - {}",statusCode,reason);
     }

     public void onWebSocketText(String message) {
         System.out.println("onWebSocketText: Message::"+message);
         super.onWebSocketText(message);
         if (isConnected() && session != null && session.isOpen()) {
             //LOG.info("Echoing back text message [{}]",message);
             try {
                 receiveText(message);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     }

    void receiveText(String text) throws Exception {
        AgentSession a = null;
        try {
            Gson gson = new Gson();
            Message message = gson.fromJson(text, Message.class);
            a =  Repository.getInstance().getAgentBySessionId(message.sessionId);
            if(null == a) {
                getSession().getRemote().sendStringByFuture("Invalid session");
            } else {
                a.setAgentId(message.agentId);
                System.out.println("The OP is "+ message.op);
                switch (message.op) {
                    case Message.OP_LOGIN:
                        Repository.getInstance().connectClient(a);
                        break;
                    case Message.OP_PING:
                        Message pingResponse = new Message();
                        pingResponse.op = Message.OP_PING;
                        pingResponse.agentId = message.agentId;
                        pingResponse.sessionId = message.sessionId;
                        getSession().getRemote().sendStringByFuture(gson.toJson(pingResponse, Message.class));
                        break;
                    case Message.OP_APPROVAL:
                        //ArrayList<Work> items = Repository.getInstance().getWork(message.agentId);
                        //getSession().getRemote().sendString(gson.toJson(items));
                        Message m = new Message();
                        m.op = Message.OP_APPROVAL;
                        m.sessionId = a.getSessionId();
                        m.agentId = a.getAgentId();
                        getSession().getRemote().sendStringByFuture(gson.toJson(m));
                        break;
                    case Message.OP_BROADCAST:
                        doBroadcast();
                        break;
                    default:
                        getSession().getRemote().sendString("Invalid Operation");
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void doBroadcast() throws IOException {
        Iterator itr = Repository.getInstance().getAllSessions().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, AgentSession> entry = (Map.Entry<String, AgentSession>) itr.next();
            String data = "Pong";
            ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
            entry.getValue().getSession().getRemote().sendPong(payload);
        }
    }

}
