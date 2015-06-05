
package com.tilab.ca.sda.gra_core.components;

import com.tilab.ca.sda.gra_core.GenderTypes;


public interface NamesGenderMap {
    /**
     * @param name the name of the person
     * @return the corresponding gender for that name or GenderTypes.UNKNOWN if the
     *         mapping is not present
     */
    public GenderTypes getGender(String name);
   
    /**
     * @param name the name of the person
     * @return the corresponding gender for the name 
     *         that is contained in the string using a longest prefix match criteria
     *         (grannydaniela match both daniel=m and daniela=f but daniela is a best match)
     *         or GenderTypes.UNKNOWN if the mapping is not present
     */
    public GenderTypes getGenderLongestPrefixName(String name);
}
