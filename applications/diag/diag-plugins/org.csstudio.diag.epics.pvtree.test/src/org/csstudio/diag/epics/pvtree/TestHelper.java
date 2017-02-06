package org.csstudio.diag.epics.pvtree;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.RefCountMap.ReferencedEntry;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;

public class TestHelper
{
    public static void setupLogging()
    {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tH:%1$tM:%1$tS %2$s %4$s: %5$s%6$s%n");

        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.FINE);

        logger = Logger.getLogger("org.csstudio.vtype.pv");
        logger.setLevel(Level.WARNING);
        logger = Logger.getLogger("com.cosylab.epics.caj");
        logger.setLevel(Level.WARNING);
    }

    public static void setupPVFactory()
    {
        final String addr_list = "127.0.0.1 webopi.sns.gov:5066 160.91.228.17";
        System.setProperty("com.cosylab.epics.caj.CAJContext.use_pure_java", "true");
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", addr_list);
        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", addr_list);

        PVPool.addPVFactory(new JCA_PVFactory());
    }

    public static void checkShutdown()
    {
        final Collection<ReferencedEntry<PV>> pvs = PVPool.getPVReferences();
        if (pvs.isEmpty())
            System.out.println("Done.");
        else
            for (ReferencedEntry<PV> pv : pvs)
                System.out.println("Failed to dispose " + pv);
        System.exit(0);
    }
}
