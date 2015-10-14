package org.csstudio.diag.pvmanager.javafx.probe;

import java.io.Writer;
import org.diirt.vtype.io.CSVIO;

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
