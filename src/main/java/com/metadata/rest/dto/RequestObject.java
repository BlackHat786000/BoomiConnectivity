package com.metadata.rest.dto;

public class RequestObject {

    private String iPackName;

    private String processName;

    private String processType;

    private String processDescription;

    public String getIPackName(){
        return iPackName;
    }

    public void setIPackName(String ipackName){
        this.iPackName = ipackName;
    }

    public String getProcessName(){
        return processName;
    }

    public void setProcessName(String processName){
        this.processName = processName;
    }

    public String getProcessType(){
        return processType;
    }

    public void setProcessType(String processType){
        this.processType = processType;
    }

    public String getProcessDescription() {
        return processDescription;
    }

    public void setProcessDescription(String processDescription) {
        this.processDescription = processDescription;
    }

}
