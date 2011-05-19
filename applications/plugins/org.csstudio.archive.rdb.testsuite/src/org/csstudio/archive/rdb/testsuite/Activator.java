package org.csstudio.archive.rdb.testsuite;

import java.util.logging.Logger;

public class Activator
{
    final public static String ID = "org.csstudio.archive.rdb.testsuite"; //$NON-NLS-1$

    final private static Logger logger = Logger.getLogger(ID);

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }
}
