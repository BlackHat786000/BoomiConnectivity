package com.metadata.rest.dto;

import java.util.List;

public class ResponseObject {

    private ProcessDetails processDetails;

    private List<CrtDetails> crtDetails;

    private List<String> parameterDetails;

    public ProcessDetails getProcessDetails() {
        return processDetails;
    }

    public void setProcessDetails(ProcessDetails processDetails) {
        this.processDetails = processDetails;
    }

    public List<CrtDetails> getCrtDetails() {
        return crtDetails;
    }

    public void setCrtDetails(List<CrtDetails> crtDetails) {
        this.crtDetails = crtDetails;
    }

    public List<String> getParameterDetails() {
        return parameterDetails;
    }

    public void setParameterDetails(List<String> parameterDetails) {
        this.parameterDetails = parameterDetails;
    }
}
