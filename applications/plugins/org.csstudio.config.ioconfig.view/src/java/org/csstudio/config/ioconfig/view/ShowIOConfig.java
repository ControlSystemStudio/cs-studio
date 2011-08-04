/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: ShowIOConfig.java,v 1.2 2009/09/15 06:38:21 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view;

import javax.annotation.Nonnull;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.WorkbenchException;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 20.07.2007
 */
public class ShowIOConfig implements IWorkbenchWindowActionDelegate {
    
    /** A workbench window handle. */
    private IWorkbenchWindow _window;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // Nothig do dispose
    }
    
    /** {@inheritDoc} */
    @Override
    public final void init(@Nonnull final IWorkbenchWindow window) {
        _window = window;
    }
    
    /** {@inheritDoc} */
    @Override
    public final void run(@Nonnull final IAction action) {
        try {
            IOConfigActivatorUI.getDefault().getWorkbench().showPerspective("org.csstudio.config.ioconfig.view.perspective", _window);
        } catch (final WorkbenchException e) {
            e.printStackTrace();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void selectionChanged(@Nonnull final IAction action,
                                 @Nonnull final ISelection selection) {
        // nothing to do at selection change
    }
}
