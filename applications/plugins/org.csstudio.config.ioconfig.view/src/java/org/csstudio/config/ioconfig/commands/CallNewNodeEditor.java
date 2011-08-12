/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.commands;

import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.editorparts.FacilityEditor;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.tools.UserName;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Wird diese Klasse noch gebraucht!?:
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 01.04.2010
 */
public class CallNewNodeEditor extends AbstractHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(CallNewNodeEditor.class);
    
    private FacilityDBO _fac;
    
    /**
     * (@inheritDoc)
     */
    @Override
    @CheckForNull
    public Object execute(@Nonnull final ExecutionEvent event) throws ExecutionException {
        // Get the view
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        final IWorkbenchPage page = window.getActivePage();
        FacilityDBO node = null;
        // Get the selection
        // If we had a selection lets open the editor
        if (newNode("")) {
            final NodeEditorInput input = new NodeEditorInput(getNode(), true);
            try {
                page.openEditor(input, FacilityEditor.ID);
            } catch (final PartInitException e) {
                LOG.error("Can't open Facility Editor", e);//$NON-NLS-1$
            }
            node = getNode();
        }
        _fac = null;
        return node;
    }
    
    @Nonnull
    private FacilityDBO getNode() {
        if(_fac == null) {
            _fac = new FacilityDBO();
        }
        return _fac;
    }
    
    private boolean newNode(@Nullable final String nameOffer) {
        
        final String nodeType = getNode().getClass().getSimpleName();
        final String title = String.format(Messages.NodeEditor_Title, nodeType);
        final String msg = String.format(Messages.NodeEditor_Msg, nodeType);
        final InputDialog id = new InputDialog(Display.getDefault().getActiveShell(), title,
                                               msg, nameOffer, null);
        id.setBlockOnOpen(true);
        if (id.open() == Window.OK) {
            getNode().setName(id.getValue());
            getNode().setSortIndex(0);
            final String name = UserName.getUserName();
            getNode().setCreationData(name, new Date());
            //            getNode().setVersion(-2);
            return true;
        }
        return false;
    }
}
