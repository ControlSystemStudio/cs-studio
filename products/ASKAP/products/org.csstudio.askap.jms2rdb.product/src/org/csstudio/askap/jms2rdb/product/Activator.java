package org.csstudio.askap.jms2rdb.product;

import java.util.logging.Logger;

public class Activator {

	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.askap.jms2rdb";

    final private static Logger logger = Logger.getLogger(ID);

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return logger;
	}

}
