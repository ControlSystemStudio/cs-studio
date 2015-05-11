package org.csstudio.diag.pvmanager.probe;

import java.io.Writer;
import org.epics.vtype.io.CSVIO;

public class SimpleDataTextExport implements DataExportFormat {

    private CSVIO io = new CSVIO();

    @Override
    public void export(Object value, Writer writer) {
        io.export(value, writer);
    }

    @Override
    public boolean canExport(Object data) {
        return io.canExport(data);
    }

}
