package com.tilab.ca.sda.sda_controller;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

//@Component
public class ControllerInitializer implements ApplicationListener<ContextRefreshedEvent>{

    private boolean isBootStrapping = true;
     
    @Override
    public void onApplicationEvent(ContextRefreshedEvent e) {
        if (!isBootStrapping) {
            return;
        }

        isBootStrapping = false;
        
        
    }
    
    
}
