package org.csstudio.utility.pvmanager.widgets;

import java.io.StringWriter;
import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.epics.vtype.VType;
import org.epics.vtype.io.CSVIO;

/**
 * Command to copy the selected value to clipboard.
 *
 * @author carcassi
 */
public class CopyValueToClipboardHandler extends AbstractAdaptedHandler<VType> {

    private CSVIO export = new CSVIO();

    public CopyValueToClipboardHandler() {
        super(VType.class);
    }

    @Override
    protected void execute(List<VType> data, ExecutionEvent event) {
        if (!data.isEmpty()) {
            VType value = data.get(0);
            if (export.canExport(value)) {
                StringWriter writer = new StringWriter();
                export.export(value, writer);
                String text = writer.toString();
                final Clipboard clipboard = new Clipboard(
                        PlatformUI.getWorkbench().getDisplay());
                    clipboard.setContents(new String[] { text },
                        new Transfer[] { TextTransfer.getInstance() });
            } else {
                throw new RuntimeException("Copy operation for value " + value + " is not supported.");
            }
        }
    }

}
