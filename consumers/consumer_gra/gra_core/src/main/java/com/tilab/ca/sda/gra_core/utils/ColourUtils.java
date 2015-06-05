package com.tilab.ca.sda.gra_core.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public class ColourUtils {
    
    /**
     * @param colorHexStr hex string represent a color
     * @return rgb representation of the hex colour
     */
    public static Color hex2Rgb(String colorHexStr){
        colorHexStr=colorHexStr.replace("#","");
        return new Color(
            Integer.valueOf(colorHexStr.substring( 1, 3 ), 16 ),
            Integer.valueOf(colorHexStr.substring( 3, 5 ), 16 ),
            Integer.valueOf(colorHexStr.substring( 5, 7 ), 16 )
        );
    }
    
    /**
     * @param numBits number of bits for all channels (9 bits means
     * 3 bits for each colour channel)
     * @return  a map containing each generated colour with an index
     */
    public static Map<Color,Integer> generatePaletteRGB(int numBits){
       int maxChannelValue=(int)(Math.pow(2f,(double)(numBits/3)));
       Map<Color,Integer> paletteMap=new HashMap<>();
       int index=1;
       for(int red=0;red<maxChannelValue;red++)
          for(int green=0;green<maxChannelValue;green++)
             for(int blue=0;blue<maxChannelValue;blue++)
                 paletteMap.put(new Color(red,green,blue),index++);
       
       return paletteMap;
    }
    
    /**
     * converts x bit RGB color values to their closest equivalent in y bit depths.
     * @param source RGB color in input
     * @param numBitsIn number of bits (in total) of RGB tuple in input
     * @param numBitsOut number of bits (in total) of RGB tuple in output
     * @return RGB color containing the different channels with bit depth changed
               Example:
               rgb input (192, 222, 237)  =>  rgb output (5, 6, 6)
    
     */
    public static Color changeRGBbitDepth(Color source,int numBitsIn,int numBitsOut){
        int maxValOut=(int)Math.pow(2f,(double)(numBitsOut/3))-1;
        int maxValIn=(int)Math.pow(2f,(double)(numBitsIn/3))-1;
        
        return new Color(source.getRed()*maxValOut/maxValIn,
                         source.getGreen()*maxValOut/maxValIn,
                         source.getBlue()*maxValOut/maxValIn);
    }
}
