package com.tilab.ca.sda.tw_user_profile_extractor.hibernate.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tw_profiles_raw")
public class TwRawProfile extends TwProfile{
    
    public static final String TRAINING_TYPE="TRAINING";
    public static final String TEST_TYPE="TEST";
    
    @Column(name="data_type")
    private String dataType;
    
    
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
