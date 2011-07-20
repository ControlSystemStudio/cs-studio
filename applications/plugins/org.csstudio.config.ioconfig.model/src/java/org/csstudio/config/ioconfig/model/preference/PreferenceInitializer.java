package org.csstudio.config.ioconfig.model.preference;

import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 04.05.2011
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope()
                .getNode(IOConfigActivator.getDefault().getPluginId());

        prefs.put(PreferenceConstants.DDB_FACILITIES, "Geb. 54,Geb. 55,XMTS,FLASH");
        prefs.put(PreferenceConstants.DDB_LOGBOOK,
                "MKS-2-DOC,MKS-DOC,FLASH-DOC,XFEL-DOC,KRYO-DOC,AMTF-DOC,CMTB-DOC,"
                        + "MKS-2,MKS,KRYO,CTA,CMTB,MKK");
        prefs.put(PreferenceConstants.DDB_LOGBOOK_MEANING,
                "DOCU,NONE,FATAL,ERROR,WARN,INFO,IDEA,TODO,DONE,FIXED");
        prefs.put(PreferenceConstants.DDB_USER_NAME, "KRYKMAN");
        prefs.put(PreferenceConstants.DDB_PASSWORD, "KRYKMAN");
        prefs.put(PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS,
                "oracle.jdbc.driver.OracleDriver");
        prefs.put(PreferenceConstants.HIBERNATE_CONNECTION_URL, "jdbc:oracle:thin:@(DESCRIPTION ="
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))"
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))"
                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))"
                + "(LOAD_BALANCE = yes)" + "(CONNECT_DATA = (SERVER = DEDICATED)"
                + "(SERVICE_NAME = desy_db.desy.de)"
                + "(FAILOVER_MODE = (TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))");
        prefs.put(PreferenceConstants.DIALECT, "org.hibernate.dialect.Oracle10gDialect");
        prefs.put(PreferenceConstants.SHOW_SQL, "false");
        prefs.putInt(PreferenceConstants.DDB_TIMEOUT, 99);
    }
}
