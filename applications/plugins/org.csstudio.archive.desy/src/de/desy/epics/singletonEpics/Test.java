/*
 * @@COPYRIGHT@@
 */

package de.desy.epics.singletonEpics;

/**

 */
public class Test {

    public static void main(String[] args)
    {
        //
        // set EPICS_CA specific environment variables
        //
        // since the sigleton will be running on our Tomcat server we've to hard code this
        //
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.236 131.169.115.237");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list","false");
        
        
        EpicsSingleton casing = EpicsSingleton.getInstance();
        System.out.println(casing.getValue("RZ:K:KL:P1:B50_T_Raum_ai_h"));
        //System.out.println(casing.setValue("record1", "123"));
        //System.out.println(casing.setValue("record1", "911"));
        
        // array test
        //System.out.println(casing.getValue("wave"));
    }
}
