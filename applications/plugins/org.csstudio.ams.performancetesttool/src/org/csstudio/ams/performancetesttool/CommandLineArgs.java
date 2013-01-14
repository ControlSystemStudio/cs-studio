package org.csstudio.ams.performancetesttool;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CommandLineArgs {
    @Parameter(names = { "-help", "-h", "--help" }, description = "Display help")
    public boolean showUsage = false;
    
    @Parameter(names = "-uri", description = "URI of JMS server (one or more)", required = true)
    public List<String> uris = new ArrayList<String>();
    
    @Parameter(names = "-template", description = "Message template file", required = true)
    public String templateFile;
    
    @Parameter(names = "-count", description = "Number of messages to send")
    public int count = 1;
    
    @Parameter(names = "-nonpersistant", description = "Send non-persistant messages")
    public boolean nonpersistant = false;
    
    @Parameter(names = "-component", description = "Which component to test (decision, minder, distributor, or jmsconnector). If not specified, tests the whole AMS.")
    public String component = null;
    
    @Parameter(names = "-rate", description = "Limit the send rate of messages to less than this number of messages per second. (0..1000, 0 = unlimited)")
    public int rate = 0;
    
    @Parameter(names = "-receive", description = "Name of topic to receive messages from. Only valid for -component jmsconnector or no test of complete AMS.")
    public List<String> receiveFromTopics = new ArrayList<String>();
}
