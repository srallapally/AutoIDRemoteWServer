package com.forgerock.autoid.remote.utils;

public class Agent {
    private String agentId;
    private String target;
    private String agentName;

    public Agent(String agentId, String target, String agentName) {
        this.agentId = agentId;
        this.target = target;
        this.agentName = agentName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
