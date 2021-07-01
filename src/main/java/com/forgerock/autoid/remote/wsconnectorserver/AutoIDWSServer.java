package com.forgerock.autoid.remote.wsconnectorserver;

import com.forgerock.autoid.remote.utils.AgentSession;
import com.forgerock.autoid.remote.utils.Message;
import com.forgerock.autoid.remote.utils.Repository;
import com.google.gson.Gson;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AutoIDWSServer {
    private Server server;
    public void setup() {
        final int port = 5040;
        final int sslPort = 8443;

/*
        SslContextFactory contextFactory = new SslContextFactory();
        contextFactory.setKeyStorePath("./keystore");
        contextFactory.setKeyStorePassword("Avaya123");
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(contextFactory, org.eclipse.jetty.http.HttpVersion.HTTP_1_1.toString());

        HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(sslPort);
        config.setOutputBufferSize(32786);
        config.setRequestHeaderSize(8192);
        config.setResponseHeaderSize(8192);
        HttpConfiguration sslConfiguration = new HttpConfiguration(config);
        sslConfiguration.addCustomizer(new SecureRequestCustomizer());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(sslConfiguration);
*/
        //ServerConnector connector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler wsContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        wsContext.setContextPath("/");

        server.setHandler(wsContext);
        wsContext.addServlet(AutoIDWorkServlet.class,"/autoidworkitems");
    }

    public void start() throws Exception {
        server.start();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        //executorService.scheduleAtFixedRate(() -> {
        //    System.out.println("Am I alive?");
        //    System.out.println("There are " + Repository.getInstance().getAllSessions().keySet().size() + "agents connected");
        //
//
        //},1, 1, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(() -> {
                    Iterator itr = Repository.getInstance().getAllSessions().entrySet().iterator();
                    while (itr.hasNext()) {
                        Map.Entry<String, AgentSession> entry = (Map.Entry<String, AgentSession>) itr.next();
                        Message ack = new Message();
                        ack.op = Message.OP_APPROVAL;
                        ack.agentId = entry.getValue().getAgentId();
                        ack.sessionId = entry.getValue().getSessionId();
                        Gson gson = new Gson();
                        //String data = "Pong";
                        //ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
                        entry.getValue().getSession().getRemote().sendStringByFuture(gson.toJson(ack,Message.class));
                    }
                },
                1, 1, TimeUnit.MINUTES);
        server.dump(System.err);
        server.join();
    }

    public static void main(String args[]) throws Exception {
        AutoIDWSServer theServer = new AutoIDWSServer();
        theServer.setup();
        theServer.start();
    }

}
