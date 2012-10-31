package org.csstudio.diag.pvfields;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;

public class TestSetup
{
    final public static String CHANNEL_NAME = "demo:tank";
    
    public static void setup() throws Exception
    {
        PVManager.setDefaultDataSource(new JCADataSource());
    }
}
