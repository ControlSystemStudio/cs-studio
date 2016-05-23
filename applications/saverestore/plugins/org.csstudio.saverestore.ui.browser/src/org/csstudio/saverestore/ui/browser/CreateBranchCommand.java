/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.ui.fx.util.FXTextInputDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>CreateBranchCommand</code> opens a dialog where user can type in the name of a new branch and request to create
 * it.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class CreateBranchCommand extends AbstractHandler {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (wrapper != null && wrapper.getProvider().areBranchesSupported()) {
                List<Branch> branches = ((BrowserView) part).getSelector().branchesProperty().get();
                final List<String> names = new ArrayList<>(branches.size());
                branches.forEach(e -> names.add(e.getShortName()));
                FXTextInputDialog
                    .get(HandlerUtil.getActiveShell(event), "Create New Branch", "Enter the name of the new branch", "",
                        e -> names.contains(e) ? "Branch '" + e + "' already exists."
                            : e.isEmpty() ? "Branch name cannot be empty." : null)
                    .ifPresent(((BrowserView) part).getActionManager()::createNewBranch);
            }
        }
        return null;
    }
}
