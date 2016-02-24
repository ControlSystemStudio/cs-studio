package org.csstudio.perspectives;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Plugin extends AbstractUIPlugin {

    public static final String ID = "org.csstudio.perspectives";

    public static final String ASCII_ENCODING = "ascii";
    public static final String XMI_EXTENSION = ".xmi";
    public static final String FILE_PREFIX = "file://";
    public static final String PERSPECTIVE_SUFFIX = "_e4persp";

    private static final Logger logger = Logger.getLogger(ID);

    public static Logger getLogger() {
        return logger;
    }

}
