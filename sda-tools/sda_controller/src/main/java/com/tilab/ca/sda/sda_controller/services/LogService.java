package com.tilab.ca.sda.sda_controller.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    
    public void tailF(String logFileName) throws FileNotFoundException, IOException, InterruptedException {
         RandomAccessFile logFile = new RandomAccessFile(logFileName,"r");
         Path logFilePath = Paths.get(logFileName);
         
         WatchService watcher = logFilePath.getFileSystem().newWatchService();
         logFilePath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
         WatchKey key;
         int filePointer=0;
         for (;;) {
              key = watcher.take();
              key.pollEvents().forEach((event)->{
                  WatchEvent.Kind<?> kind = event.kind();
                  if (kind != StandardWatchEventKinds.OVERFLOW){
                      
                  } 
              });           
         }
    }
}
//https://docs.oracle.com/javase/tutorial/essential/io/notification.html
//https://github.com/mingfang/Tail/blob/master/src/main/java/com/rebelsoft/FileTailer.java
//https://github.com/satyagraha/logviewer/blob/master/logviewer-common/src/main/java/org/logviewer/core/LogManager.java