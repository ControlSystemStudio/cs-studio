package org.csstudio.saverestore.ui.browser;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.ui.util.FXComboInputDialog;
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
            FXComboInputDialog<DataProviderWrapper> dialog = new FXComboInputDialog<>(
                  HandlerUtil.getActiveShell(event), "Select Data Provider",
                  "Select the data provider you wish to use:", Engine.getInstance().getSelectedDataProvider(),
                  Engine.getInstance().getDataProviders());
            dialog.openAndWait().ifPresent(Engine.getInstance()::setSelectedDataProvider);
        }
        return null;
    }

}
