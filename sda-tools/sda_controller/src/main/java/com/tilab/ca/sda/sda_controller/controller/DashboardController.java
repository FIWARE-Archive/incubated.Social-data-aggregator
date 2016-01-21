/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tilab.ca.sda.sda_controller.controller;

import com.tilab.ca.sda.sda_controller.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 *
 * @author dino
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @RequestMapping(value="/startSda",method = RequestMethod.GET)
    public void startSda(@RequestParam("startWithEnv") boolean startWithEnv) throws Exception{
        dashboardService.startSda(startWithEnv);
    }
    
    @RequestMapping(value="/stopSda",method = RequestMethod.GET)
    public void stopSda() throws Exception{
        dashboardService.stopSda();
    }
    
    @RequestMapping("/sdaStartupLogContent")
    public ResponseBodyEmitter getSdaStartupLogContent() throws Exception{
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        dashboardService.getSdaStartupLogContent(emitter);
        return emitter;
    }
}
