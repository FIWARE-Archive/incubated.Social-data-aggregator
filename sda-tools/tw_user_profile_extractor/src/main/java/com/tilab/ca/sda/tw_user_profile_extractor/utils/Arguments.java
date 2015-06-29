package com.tilab.ca.sda.tw_user_profile_extractor.utils;


public class Arguments {
    
    private String inputDataPath=null;
    private String outputPath=null;
    private boolean trainingData=false;
    
    public String getInputDataPath() {
        return inputDataPath;
    }

    public void setInputDataPath(String inputDataPath) {
        this.inputDataPath = inputDataPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputFile) {
        this.outputPath = outputFile;
    }

    public boolean areTrainingData() {
        return trainingData;
    }

    public void setAreTrainingData(boolean areTrainingData) {
        this.trainingData = areTrainingData;
    }
    
    
}
