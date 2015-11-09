package org.csstudio.saverestore.ui.browser;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>CreateBranchCommand</code> opens a dialog where user can type in the name of a new branch and request
 * to create it.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class CreateBranchCommand extends AbstractHandler implements IHandler {

    public static final String ID = "org.csstudio.saverestore.ui.gitbrowser.command.newbranch";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            final List<String> branches = ((BrowserView) part).getSelector().branchesProperty().get();
            InputDialog dialog = new InputDialog(HandlerUtil.getActiveShell(event), "Create New Branch",
                    "Enter the name of the new branch", "", new IInputValidator() {
                        @Override
                        public String isValid(String newText) {
                            if (branches.contains(newText)) {
                                return "Branch '" + newText + "' already exists.";
                            } else if (branches.isEmpty()) {
                                return "Branch name cannot be empty.";
                            } else {
                                return null;
                            }
                        }
                    });
            dialog.open();
            String value = dialog.getValue();
            if (value != null) {
                ((BrowserView) part).getActionManager().createNewBranch(value);
            }
        }
        return null;
    }

}
