package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.ui.util.ComboInputDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>SelectDataProviderCommand</code> displays a dialog, where user can choose which data provider will be
 * used by the browser.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SelectDataProviderCommand extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            List<DataProviderWrapper> dataProviders = Engine.getInstance().getDataProviders();
            List<String> names = new ArrayList<>(dataProviders.size());
            dataProviders.forEach(d -> names.add(d.getPresentationName()));
            DataProviderWrapper selected = Engine.getInstance().getSelectedDataProvider();
            ComboInputDialog dialog = new ComboInputDialog(HandlerUtil.getActiveShell(event),
                    "Select Data Provider", "Select the data provider you wish to user",
                    selected == null ? null : selected.getPresentationName(),
                            names.toArray(new String[names.size()]), null);
            dialog.open();
            String value = dialog.getValue();
            if (value != null) {
                for (DataProviderWrapper dpw : dataProviders) {
                    if (dpw.getPresentationName().equals(value)) {
                        Engine.getInstance().setSelectedDataProvider(dpw);
                        break;
                    }
                }
            }
        }
        return null;
    }

}
