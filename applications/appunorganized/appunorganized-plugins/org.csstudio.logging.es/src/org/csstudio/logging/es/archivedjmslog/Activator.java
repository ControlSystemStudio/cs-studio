package org.csstudio.logging.es.archivedjmslog;

import java.util.logging.Logger;

public class Activator
{
    final public static String ID = "org.csstudio.logging.es.archivedjmslog"; //$NON-NLS-1$

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }

    static void checkParameter(Object parameter, String name)
    {
        if (null == parameter)
        {
            throw new IllegalArgumentException(name + " is required."); //$NON-NLS-1$
        }
    }

    static void checkParameterString(String parameter, String name)
    {
        if ((null == parameter) || parameter.isEmpty())
        {
            throw new IllegalArgumentException(name + " is required."); //$NON-NLS-1$
        }
    }
}
