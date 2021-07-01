package com.forgerock.autoid.remote.utils;

public class Work {
    private String workId;
    private String target;
    private String type;
    private String agentId;
    private Boolean complete;
    private Boolean error;
    private String results;

    public Work(){}
    public Work(String workId, String target, String type){
        this.workId = workId;
        this.target = target;
        this.type = type;
    }
    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
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

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public void setType (String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
