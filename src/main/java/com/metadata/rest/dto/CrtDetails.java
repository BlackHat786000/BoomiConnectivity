package com.metadata.rest.dto;

public class CrtDetails {

    private String crtName;
    
    private String crtHeaders;

    public String getCrtHeaders() {
		return crtHeaders;
	}

	public void setCrtHeaders(String crtHeaders) {
		this.crtHeaders = crtHeaders;
	}

	public String getCrtName() {
		if (crtName.startsWith("_")) {
			return crtName.substring(1);
		}
        return crtName;
    }

    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }
}
