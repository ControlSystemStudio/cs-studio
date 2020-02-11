package org.csstudio.phoebus.integration.databrowser;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Action to open the databrowser with a list of pv's
 * @author kunal
 *
 */
@SuppressWarnings("nls")
public class OpenPhoebusDatabrowser extends AbstractHandler implements IHandler {

    static final String PHOEBUS_APP = "databrowser";

    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final Shell shell = HandlerUtil.getActiveShell(event);
        try {
            // Retrieve the selection and the current page
            final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
            final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);

            // Were PVs provided (opened from context menu?)
            if (pvs == null || pvs.length <= 0) {
                PhoebusLauncherService.launchApplication(PHOEBUS_APP);
            } else {

                String query = Arrays.asList(pvs).stream().map(pv -> pv.getName()).collect(Collectors.joining("&"));
                query = query + "&" + "app=" + PHOEBUS_APP;
                URI uri = new URI("pv", "", null, query, null);
                PhoebusLauncherService.launchResource(uri.toString());
            }
        } catch (Exception ex) {
            ExceptionDetailsErrorDialog.openError(shell, "Failed to open databrowser", ex);
        }
        return null;
    }

}
