package com.tilab.ca.sda.sda_controller.controller;

import com.tilab.ca.sda.sda_controller.model.GlobalConfs;
import com.tilab.ca.sda.sda_controller.model.SdaConfs;
import com.tilab.ca.sda.sda_controller.services.ConfigurationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class ConfigController {
    
    @Autowired
    private ConfigurationService configurationService;
    
    @RequestMapping("/sdaConfig")
    public SdaConfs getSdaConfig(){
        return configurationService.getSdaStarupScriptConfs();
    }
    
    @RequestMapping(value = "/sdaConfig",method = RequestMethod.PUT)
    public void updateSdaConfig(@RequestBody SdaConfs sdaConfs) throws Exception{
        configurationService.updateSdaStartScriptConfs(sdaConfs);
    }
    
    @RequestMapping("/globalConfig")
    public GlobalConfs getGlobalConfs(){
        return configurationService.getGlobalConfs();
    }
    
    @RequestMapping(value = "/globalConfig",method = RequestMethod.PUT)
    public void updateGlobalConfs(@RequestBody GlobalConfs gs) throws Exception{
         configurationService.updateGlobalConfs(gs);
    }
}
