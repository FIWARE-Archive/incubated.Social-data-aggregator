package com.tilab.ca.sda.gra_consumer_dao.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.SQLInsert;

@Entity
@Table(name="tw_user_gender")
@SQLInsert(sql="INSERT IGNORE INTO tw_user_gender(screen_name,gender,uid) VALUES (?,?,?)")
public class TwGenderProfile implements Serializable{
    
    private static final String FIELDS_SEPARATOR=",";
    
    @Id
    @Column(name="uid")
    private long uid;
    
    @Column(name="screen_name")
    private String screenName;
    
    private char gender;
    
    public TwGenderProfile(){}

    public TwGenderProfile(long uid, String screenName, char gender) {
        this.uid = uid;
        this.screenName = screenName;
        this.gender = gender;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
    
    @Override
    public String toString(){
        return uid+FIELDS_SEPARATOR+screenName+FIELDS_SEPARATOR+gender;
    }

}
