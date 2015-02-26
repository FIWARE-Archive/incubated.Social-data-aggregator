
package com.tilab.ca.sda.utils.mocks;

import java.util.Properties;


public class TstImpl implements TstInterface{

    private int val1;
    private int val2;
    
    public TstImpl(Properties props){
        val1=Integer.parseInt(props.getProperty("val1"));
        val2=Integer.parseInt(props.getProperty("val2"));
    }

    @Override
    public int val1() {
        return val1;
    }

    @Override
    public int val2() {
        return val2;
    }
    
    
}
