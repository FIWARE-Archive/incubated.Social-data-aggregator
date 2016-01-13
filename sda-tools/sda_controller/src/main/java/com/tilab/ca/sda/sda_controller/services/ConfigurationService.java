package com.tilab.ca.sda.sda_controller.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tilab.ca.sda.sda_controller.model.GlobalConfs;
import com.tilab.ca.sda.sda_controller.model.Prop;
import com.tilab.ca.sda.sda_controller.model.SdaConfs;
import com.tilab.ca.sda.sda_controller.utils.JsonUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;


@Service
public class ConfigurationService {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
    
    @Autowired
    private ApplicationContext context;
    
    private SdaConfs sdaStarupScriptConfs;
    
    private GlobalConfs globalConfs;
    
    @Value("${globalconfs.file.path:confs/globalConfs.json}")
    private String globalConfsFilePath;
    
    @Value("${sda.startup.script.confs.file.path:confs/sdaConf.json}")
    private String sdaStartupScriptConfsFilePath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String EQ_SEPARATOR="=";
    private static final String NEWLINE_SEPARATOR=System.getProperty("line.separator");
    
    @PostConstruct
    public void init(){
        try {
            sdaStarupScriptConfs = objectMapper.readValue(getResource(sdaStartupScriptConfsFilePath).getInputStream(), SdaConfs.class);        
            //load global confs
            globalConfs = objectMapper.readValue(getResource(globalConfsFilePath).getInputStream(), GlobalConfs.class);
        } catch (IOException ex) {
            log.error("Failed to load configurations json files!",ex);
        }
    }
    
    private Resource getResource(String resourceName){
        Resource resource = context.getResource("file:"+resourceName);
            if(!resource.exists()){
                log.info("resource "+resourceName+" not found on relative path. Getting the default one on classpath");
                resource = context.getResource("classpath:"+resourceName);
            }
            return resource;
    }

    public SdaConfs getSdaStarupScriptConfs() {
        return sdaStarupScriptConfs;
    }
    
    public GlobalConfs getGlobalConfs(){
        return globalConfs;
    }
    
    public void updateGlobalConfs(GlobalConfs gs) throws Exception {
        saveConfs(gs, globalConfsFilePath);
        this.globalConfs = gs;
    }
    
    public void updateSdaStartScriptConfs(SdaConfs confs) throws Exception{     
        saveFile(serializeProps(confs.getProps(), EQ_SEPARATOR,NEWLINE_SEPARATOR), this.globalConfs.getSdaHome().getValue()+File.separator+confs.getFile());
        saveConfs(confs, sdaStartupScriptConfsFilePath);
        this.sdaStarupScriptConfs = confs;
    }
    
    private String serializeProps(List<Prop> props,String propSeparator,String lineSeparator){
        return 
               StringUtils.collectionToDelimitedString(props.stream().map(prop -> prop.getName()+propSeparator+prop.getValue())
                       .collect(Collectors.toList()),lineSeparator);
    }
    
    private void saveConfs(Object conf,String filePath) throws Exception{
        String jSerConf = JsonUtils.serialize(conf);
        saveFile(jSerConf,filePath);
    }
    
    public static void saveFile(String fileContent, String savePath) throws Exception {
        if(savePath.split(File.separator).length>1){
            String path = savePath.substring(0, savePath.lastIndexOf(File.separator));
            new File(path).mkdirs();
        }
        File file2Save = new File(savePath);
        try (FileWriter fileWriter = new FileWriter(file2Save)) {
            fileWriter.write(fileContent);
            fileWriter.flush();
        }
    }
    
//    public void saveSdaConfs(SdaConfs sdaConfs){
//        String fileName = this.sdaConfs.getFile();
//        sdaConfs.setFile(fileName);
//        this.sdaConfs = sdaConfs;
//    }

}
