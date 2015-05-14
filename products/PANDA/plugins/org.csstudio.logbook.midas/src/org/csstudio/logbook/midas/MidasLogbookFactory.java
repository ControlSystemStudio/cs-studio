package org.csstudio.logbook.midas;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;

@SuppressWarnings("nls")
public class MidasLogbookFactory implements ILogbookFactory {

    @Override
    public String[] getLogbooks() throws Exception {
        return new String[] { "labor" };
    }

    @Override
    public String getDefaultLogbook() {
        return "labor";
    }

    @Override
    public ILogbook connect(String logbook, String user, String password)
            throws Exception {
        return new MidasLogbook(logbook, user, password);
    }

}
