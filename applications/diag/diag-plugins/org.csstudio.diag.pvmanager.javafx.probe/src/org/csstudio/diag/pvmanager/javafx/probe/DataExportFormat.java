package org.csstudio.diag.pvmanager.javafx.probe;

import java.io.Writer;

public interface DataExportFormat {

    void export(Object data, Writer writer);
    boolean canExport(Object data);

}
