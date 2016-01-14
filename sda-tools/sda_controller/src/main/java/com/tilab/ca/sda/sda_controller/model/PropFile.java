/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.sda_controller.model;

import java.util.List;

/**
 *
 * @author dino
 */
public class PropFile {
    
    private String name;
    private List<Section> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
               
}
