package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;

/**
 * TODO remove class since it is not longer needed.
 * @author shroffk
 *
 */
@Deprecated
public class LocalUtilityPvManagerBridge {

    private PVWriter<Object> selectionWriter;

    public LocalUtilityPvManagerBridge(String pvName) {
        selectionWriter = PVManager.write(channel(pvName)).async();
        write("");
    }

    private static Executor executor = Executors.newSingleThreadExecutor();

    public void write(final Object obj) {
        if (selectionWriter != null) {
            selectionWriter.write(obj);
        }
    }


    public void close() {
        if (selectionWriter != null) {
            selectionWriter.close();
            selectionWriter = null;
        }
    }

}
