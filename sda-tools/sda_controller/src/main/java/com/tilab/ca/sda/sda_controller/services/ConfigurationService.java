package com.tilab.ca.sda.sda_controller.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tilab.ca.sda.sda_controller.model.GlobalConfs;
import com.tilab.ca.sda.sda_controller.model.Module;
import com.tilab.ca.sda.sda_controller.model.Prop;
import com.tilab.ca.sda.sda_controller.model.SdaConfs;
import com.tilab.ca.sda.sda_controller.model.Section;
import com.tilab.ca.sda.sda_controller.utils.JsonUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    
    private List<Module> modules;
    
    @Value("${globalconfs.file.path:confs/globalConfs.json}")
    private String globalConfsFilePath;
    
    @Value("${globalconfs.file.path:confs/modules.json}")
    private String modulesFilePath;
    
    @Value("${sda.startup.script.confs.file.path:confs/sdaConf.json}")
    private String sdaStartupScriptConfsFilePath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String MODULES_FILE_PATH="/scripts/modules";
    
    private static final String MODULE_COMMENT_CHAR="#";
    
    private static final String EQ_SEPARATOR="=";
    private static final String NEWLINE_SEPARATOR=System.getProperty("line.separator");
    
    @PostConstruct
    public void init(){
        try {
            sdaStarupScriptConfs = objectMapper.readValue(getResource(sdaStartupScriptConfsFilePath).getInputStream(), SdaConfs.class);        
            //load global confs
            globalConfs = objectMapper.readValue(getResource(globalConfsFilePath).getInputStream(), GlobalConfs.class);
            //load modules
            modules = objectMapper.readValue(getResource(modulesFilePath).getInputStream(), new TypeReference<List<Module>>(){});
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
    
    public List<Module> getModules(){
        return modules;
    }
    
    public void updateGlobalConfs(GlobalConfs gs) throws Exception {
        saveConfs(gs, globalConfsFilePath);
        this.globalConfs = gs;
    }
    
    public void updateSdaStartScriptConfs(SdaConfs confs) throws Exception{     
        saveFile(serializeSections(confs.getSections(), EQ_SEPARATOR,NEWLINE_SEPARATOR), this.globalConfs.getSdaHome().getValue()+File.separator+confs.getFile());
        saveConfs(confs, sdaStartupScriptConfsFilePath);
        this.sdaStarupScriptConfs = confs;
    }
    
    //TODO add a method that allows to load already valorized props from sda conf files 
    public void loadConfsFromFile(){
    }
    
    private String serializeSections(List<Section> sections,String propSeparator,String lineSeparator){
        return StringUtils.collectionToDelimitedString(sections.stream().map(section -> {
           
            String sectStr = MODULE_COMMENT_CHAR+section.getName()+lineSeparator+MODULE_COMMENT_CHAR+section.getDescription();
            return sectStr+lineSeparator+serializeProps(section.getProps(), propSeparator, lineSeparator);
        
        }).collect(Collectors.toList()),lineSeparator);
    }
    
    
    private String serializeProps(List<Prop> props,String propSeparator,String lineSeparator){
        return 
               StringUtils.collectionToDelimitedString(props.stream().map(prop -> {
                            if(prop.getValue()!=null && !prop.getValue().isEmpty())
                                return prop.getName()+propSeparator+prop.getValue();
                            return MODULE_COMMENT_CHAR+prop.getName();
                       })
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
    
    public void updateModule(Module module) throws Exception {
        List<Module> mTempList = this.modules.stream().map(m -> m.getId()==module.getId()?module:m)
                                            .collect(Collectors.toList());
        
        module.getConfs().forEach((key,propFile) -> {       
            try {
                saveFile(serializeSections(propFile.getSections(), EQ_SEPARATOR,NEWLINE_SEPARATOR),
                        this.globalConfs.getSdaHome().getValue()+module.getConfsPath()+File.separator+propFile.getName());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        
        saveConfs(mTempList, modulesFilePath);
        this.modules = mTempList;
    }

    public void updateModuleEnabled(int moduleId, boolean enabled) throws Exception {
        Module module = modules.stream().filter(m -> moduleId == m.getId()).findAny()
                                        .orElseThrow(() -> new IllegalArgumentException("provided id not found"));
        String scriptModuleFilePath = globalConfs.getSdaHome().getValue()+MODULES_FILE_PATH;
        try(Stream<String> lines = Files.lines(Paths.get(scriptModuleFilePath))
                                   .map(row -> updateModuleFileRow(row, module.getName(), enabled))){
            saveFile(StringUtils.collectionToDelimitedString(lines.collect(Collectors.toList()), NEWLINE_SEPARATOR)+NEWLINE_SEPARATOR,
                    scriptModuleFilePath);
            module.setEnabled(enabled);
            saveConfs(modules, modulesFilePath);
        }
    }
    
    private String updateModuleFileRow(String row,String moduleName,boolean enabled){
        String mNameFile = row.split(" ")[0];
        if(moduleName.equals(mNameFile) || moduleName.equals(MODULE_COMMENT_CHAR+mNameFile)){
            if(enabled && row.startsWith(MODULE_COMMENT_CHAR))
                return row.replace(MODULE_COMMENT_CHAR, "");
            if(!enabled && !row.startsWith(MODULE_COMMENT_CHAR))
                return MODULE_COMMENT_CHAR+row;
        }
        
        return row;
    }

}
