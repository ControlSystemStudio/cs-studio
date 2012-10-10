package org.csstudio.ams.jmsutilities;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class AmsJmsCommandLineArgs {
    @Parameter(names = "-uri", description = "URI of JMS server (one or more)", required = true)
    public List<String> uris = new ArrayList<String>();
    
    @Parameter(names = "-topic", description = "JMS Topic", required = true)
    public List<String> topics;
}
