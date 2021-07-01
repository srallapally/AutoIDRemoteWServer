package com.forgerock.autoid.remote.utils;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class Repository {
    private static Repository instance;
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    private final HashMap<String, Work> work = new HashMap<>();
    private final List<Agent> agents = new ArrayList<Agent>();
    private final HashMap<String,AgentSession> sessions = new HashMap<>();

    private Repository() {
        init();
    }

    private void init(){
        agents.add(new Agent("100","IIQ","IIQConnector"));
        agents.add(new Agent("200","OIM","OIMConnector"));
        work.put("123",new Work("1234","IIQ","Approval"));
        work.put("345",new Work("345","IIQ","Certification"));
        work.put("456",new Work("456","OIM","Roles"));
    }

    public boolean isValidAgent(String agentId) {
        ListIterator<Agent> iterator = agents.listIterator();
        while(iterator.hasNext()){
            if(agentId == iterator.next().getAgentId()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Work> getWork(String target){
        ArrayList<Work> ret = new ArrayList<>();
        for(Work w : work.values()){
            if(w.getTarget().equalsIgnoreCase(target)) {
                ret.add(w);
            }
        }
        return ret;
    }

    public Boolean disconnectClient(Session session){
        Iterator<Map.Entry<String, AgentSession>> itr = sessions.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<String, AgentSession> entry = itr.next();
            if(entry.getValue().getSession() == session) {
                sessions.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }
    public AgentSession getAgentBySessionId(String sessionId){
        return sessions.get(sessionId);
    }
    public Boolean connectClient(AgentSession agentSession){
        if(null == sessions.get(agentSession.getSessionId())){
            System.out.println("Created a new session for "+agentSession.getSessionId());
            sessions.put(agentSession.getSessionId(),agentSession);
            return true;
        }
        return false;
    }
    public Boolean validateClient(AgentSession agentSession){
        if(null == sessions.get(agentSession.getSessionId())){
            return false;
        }
        return true;
    }
    public Boolean updateWork(String clientId,String workId, String results){
        for (Map.Entry<String, Work> set : work.entrySet()) {
            if(set.getKey().equalsIgnoreCase(workId)) {
                String oldKey = set.getKey();
                Work w1 = new Work();
                w1.setWorkId(set.getKey());
                w1.setAgentId(clientId);
                w1.setTarget(set.getValue().getTarget());
                w1.setResults(results);
                w1.setComplete(true);
                w1.setError(false);
                work.remove(oldKey);
                work.put(oldKey,w1);
                return true;
            }
        }
        return false;
    }

    public HashMap getAllSessions(){
        return this.sessions;
    }
}
