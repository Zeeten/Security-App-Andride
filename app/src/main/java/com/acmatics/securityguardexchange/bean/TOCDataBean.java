package com.acmatics.securityguardexchange.bean;

import nl.siegmann.epublib.domain.Resource;

/**
 * Created by sahil on 12/8/2015.
 */
public class TOCDataBean {
    private String title;
    private Resource resource;
    private Boolean isCategory;

    public Boolean getIsCategory() {
        return isCategory;
    }

    public void setIsCategory(Boolean isCategory) {
        this.isCategory = isCategory;
    }

    public TOCDataBean() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public Resource getResource() {
        return resource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
