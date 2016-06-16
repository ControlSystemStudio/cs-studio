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

import org.csstudio.saverestore.ui.Selector;
import org.csstudio.ui.fx.util.FXComboInputDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 * <code>SwitchBranchCommand</code> provides a dialog, where user can select any existing branch and issues a request to
 * switch to this branch.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SwitchBranchCommand extends AbstractHandler {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof BrowserView) {
            Selector selector = ((BrowserView) part).getSelector();
            FXComboInputDialog
                .pick(HandlerUtil.getActiveShell(event), "Select Branch", "Select the branch you wish to work on",
                    selector.selectedBranchProperty().get(), selector.branchesProperty().get())
                .ifPresent(selector.selectedBranchProperty()::set);
        }
        return null;
    }
}
