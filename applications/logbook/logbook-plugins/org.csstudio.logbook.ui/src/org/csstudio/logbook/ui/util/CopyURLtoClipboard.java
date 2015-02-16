package org.csstudio.logbook.ui.util;
import java.net.URL;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author Kunal Shroff
 *
 */
public class CopyURLtoClipboard extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final URL[] urls = AdapterUtil.convert(selection, URL.class);
        
	if (urls == null || urls.length == 0) {
	    MessageDialog.openError(null, "Empty URL",
		    "URL is empty! Nothing will be copied.");
	    return null;
	}
        
	// Copy as text to clipboard
	final Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
		.getDisplay());
	// Use the first URL from the array of selected URLs
	clipboard.setContents(new String[] { urls[0].toString() },
		new Transfer[] { TextTransfer.getInstance() });
        
	return null;
    }

}
