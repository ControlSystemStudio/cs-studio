package org.csstudio.saverestore.ui.browser;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.ui.fx.util.FXComboInputDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>SelectDataProviderCommand</code> displays a dialog, where user can choose which data provider will be used by
 * the browser.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SelectDataProviderCommand extends AbstractHandler {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            FXComboInputDialog.pick(HandlerUtil.getActiveShell(event), "Select Data Provider",
                "Select the data provider you wish to use", SaveRestoreService.getInstance().getSelectedDataProvider(),
                SaveRestoreService.getInstance().getDataProviders())
                .ifPresent(SaveRestoreService.getInstance()::setSelectedDataProvider);
        }
        return null;
    }
}
