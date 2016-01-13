package com.tilab.ca.sda.sda_controller.model;

import java.util.List;


public class SdaConfs {
    
    private String file;
    
    private List<Prop> props;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<Prop> getProps() {
        return props;
    }

    public void setProps(List<Prop> props) {
        this.props = props;
    }
}
