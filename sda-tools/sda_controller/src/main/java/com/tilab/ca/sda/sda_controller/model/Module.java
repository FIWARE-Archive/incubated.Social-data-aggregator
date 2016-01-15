/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.sda_controller.model;

import java.util.Map;

/**
 *
 * @author dino
 */
public class Module {
    
    private int id;
    
    private String name;
    
    private String label;
    
    private String type;
    
    private String imageUrl;
    
    private String description;
    
    private String confsPath;
    
    private boolean enabled;
       
    private Map<String,PropFile> confs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfsPath() {
        return confsPath;
    }

    public void setConfsPath(String confsPath) {
        this.confsPath = confsPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, PropFile> getConfs() {
        return confs;
    }

    public void setConfs(Map<String, PropFile> confs) {
        this.confs = confs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }          
}
