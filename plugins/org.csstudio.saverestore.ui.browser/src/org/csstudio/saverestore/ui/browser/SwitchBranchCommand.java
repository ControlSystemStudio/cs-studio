package org.csstudio.saverestore.ui.browser;

import org.csstudio.saverestore.ui.browser.logic.Selector;
import org.csstudio.saverestore.ui.util.ComboInputDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>SwitchBranchCommand</code> provides a dialog, where user can select any existing branch and issues a
 * request to switch to this branch.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SwitchBranchCommand extends AbstractHandler implements IHandler {

    public static final String ID = "org.csstudio.saverestore.ui.gitbrowser.command.switchbranch";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        //EFX support for dialogs is bad. They cannot be made modal, if not closed they remain open
        //even after CSS is closed, they use some default ugly icon etc.
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            Selector selector = ((BrowserView)part).getSelector();
            ComboInputDialog dialog = new ComboInputDialog(HandlerUtil.getActiveShell(event),
                    "Select Branch", "Select the branch you wish to work on",
                    selector.selectedBranchProperty().get(),
                    selector.branchesProperty().get().toArray(new String[0]), null);
            dialog.open();
            String value = dialog.getValue();
            if (value != null) {
                selector.selectedBranchProperty().set(value);
            }
        }



        return null;
    }

}
