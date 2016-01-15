package com.tilab.ca.sda.sda_controller.controller;

import com.tilab.ca.sda.sda_controller.model.GlobalConfs;
import com.tilab.ca.sda.sda_controller.model.Module;
import com.tilab.ca.sda.sda_controller.model.PatchRequest;
import com.tilab.ca.sda.sda_controller.model.SdaConfs;
import com.tilab.ca.sda.sda_controller.services.ConfigurationService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class ConfigController {
    
    @Autowired
    private ConfigurationService configurationService;
    
    @RequestMapping("/modules")
    public List<Module> getModules(){
        return configurationService.getModules();
    }
    
    @RequestMapping(value = "/modules/{moduleId}",method = RequestMethod.PUT)
    public void updateModuleConfig(@PathVariable("moduleId") int moduleId,@RequestBody Module module) throws Exception{
        configurationService.updateModule(module);
    }
    
    @RequestMapping(value = "/modules/{moduleId}",method = RequestMethod.PATCH)
    public void updateModuleEnable(@PathVariable("moduleId") int moduleId,@RequestBody PatchRequest request) throws Exception{
        
        if(request.getFieldName().equals("enabled"))
            configurationService.updateModuleEnabled(moduleId,Boolean.parseBoolean(request.getValue()));
    }
    
    
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
