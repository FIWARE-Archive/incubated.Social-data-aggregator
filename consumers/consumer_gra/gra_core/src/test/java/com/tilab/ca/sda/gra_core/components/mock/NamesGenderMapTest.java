package com.tilab.ca.sda.gra_core.components.mock;

import com.tilab.ca.sda.gra_core.GenderTypes;
import com.tilab.ca.sda.gra_core.components.NamesGenderMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class NamesGenderMapTest implements NamesGenderMap{

    private Map<String,GenderTypes> testMap=new HashMap<>();

    public NamesGenderMapTest() {
        testMap.put("john", GenderTypes.MALE);
        testMap.put("matt", GenderTypes.MALE);
        testMap.put("deborah", GenderTypes.FEMALE);
        testMap.put("news", GenderTypes.PAGE);
    }

    @Override
    public GenderTypes getGender(String name) {
        return testMap.getOrDefault(name,GenderTypes.UNKNOWN);
    }

    @Override
    public GenderTypes getGenderLongestPrefixName(String name) {
        Optional<String> optName=testMap.keySet().stream().filter((currName)->name.startsWith(currName) || name.endsWith(currName))
                                        .sorted((c1,c2) -> Integer.compare(c2.length(), c1.length()))
                                        .findFirst();
        return optName.isPresent()?testMap.get(optName.get()):GenderTypes.UNKNOWN;
    }
    
}
