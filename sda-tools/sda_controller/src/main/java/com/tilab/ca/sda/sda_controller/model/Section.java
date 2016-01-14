package com.tilab.ca.sda.sda_controller.model;

import java.util.List;

/**
 *
 * @author dino
 */
public class Section {
    
    private String name;
    
    private String label;
    
    private String description;
    
    private List<Prop> props;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Prop> getProps() {
        return props;
    }

    public void setProps(List<Prop> props) {
        this.props = props;
    }
    
    
    
    
}
