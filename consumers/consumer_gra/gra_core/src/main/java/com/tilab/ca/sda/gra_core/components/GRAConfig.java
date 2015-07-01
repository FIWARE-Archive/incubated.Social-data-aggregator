package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.ml.FeaturesExtraction;
import com.tilab.ca.sda.gra_core.ml.MlModel;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

public class GRAConfig implements Serializable {

    private MlModel coloursModel;
    private MlModel descrModel;
    private NamesGenderMap namesGenderMap;
    private FeaturesExtraction fe;
    private String trainingPathStr;
    private int numBits;
    private int numColors;

    public GRAConfig coloursClassifierModel(MlModel mlModel) throws Exception {
        this.coloursModel = mlModel;
        return this;
    }

    public GRAConfig descrClassifierModel(MlModel mlModel) throws Exception {
        this.descrModel = mlModel;
        return this;
    }

    public GRAConfig featureExtractor(FeaturesExtraction fe) throws Exception {
        this.fe = fe;
        return this;
    }

    public GRAConfig trainingPath(String trainingPathStr) throws Exception {
        this.trainingPathStr = trainingPathStr;
        return this;
    }

    public GRAConfig namesGenderMap(NamesGenderMap namesGenderMap) throws Exception {
        this.namesGenderMap = namesGenderMap;
        return this;
    }

    public GRAConfig numColorBitsMapping(int nbits) {
        this.numBits = nbits;
        return this;
    }

    public GRAConfig numColorsMapping(int ncols) {
        this.numColors = ncols;
        return this;
    }

    public MlModel getColoursModel() {
        return coloursModel;
    }

    public MlModel getDescrModel() {
        return descrModel;
    }

    public NamesGenderMap getNamesGenderMap() {
        return namesGenderMap;
    }

    public FeaturesExtraction getFe() {
        return fe;
    }

    public String getTrainingPathStr() {
        return trainingPathStr;
    }

    public int getNumBits() {
        return numBits;
    }

    public int getNumColors() {
        return numColors;
    }

    public boolean areMandatoryFieldsFilled() {
        return StringUtils.isNotBlank(trainingPathStr) && coloursModel != null
                && descrModel != null && fe != null && namesGenderMap != null
                && numBits > 0 && numColors > 0;
    }

}
