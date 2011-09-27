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

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.editorparts.ModuleEditor;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.view.internal.localization.Messages;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 10.06.2010
 */
public class CallNewChildrenNodeEditor extends AbstractCallNodeEditor {

    private static final String ID = "org.csstudio.config.ioconfig.commands.callNewChildrenEditor";//$NON-NLS-1$

    /**
     * {@inheritDoc}
     * @throws PartInitException
     * @throws PersistenceException
     */
    @Override
    protected void openNodeEditor(@Nonnull final AbstractNodeDBO<AbstractNodeDBO<?,?>,AbstractNodeDBO<?,?>> parentNode,
                                  @Nonnull final IWorkbenchPage page) throws PartInitException,
                                  PersistenceException {
        final AbstractNodeDBO<?, ?> createChild = parentNode.createChild();
        final String id = NodeEditorHandler.getEditorIdFor(createChild);

        if ((AbstractNodeDBO<?, ?>) createChild != null && id != null) {
            final String nodeType = ((AbstractNodeDBO<?, ?>) createChild).getNodeType().getName();
            if (id.equals(ModuleEditor.ID)) {
                ((AbstractNodeDBO<?, ?>) createChild).setName(" "); //$NON-NLS-1$
                ((AbstractNodeDBO<?, ?>) createChild).setSortIndexNonHibernate(parentNode.getfirstFreeStationAddress());
                final NodeEditorInput input = new NodeEditorInput(createChild,true);
                page.openEditor(input, id);
            } else {
                final String title = String.format(Messages.NodeEditor_Title, nodeType);
                final String msg = String.format(Messages.NodeEditor_Msg, nodeType);
                final InputDialog idialog = new InputDialog(null, title, msg, nodeType, null);
                idialog.setBlockOnOpen(true);
                if (idialog.open() == Window.OK) {
                    // TODO: (hrickens) set the right max station Address
                    ((AbstractNodeDBO<?, ?>) createChild).setSortIndexNonHibernate(parentNode.getfirstFreeStationAddress());
                    if(idialog.getValue()!=null&&!idialog.getValue().isEmpty()) {
                        ((AbstractNodeDBO<?, ?>) createChild).setName(idialog.getValue());
                    } else {
                        ((AbstractNodeDBO<?, ?>) createChild).setName(nodeType);
                    }
                    final NodeEditorInput input = new NodeEditorInput(createChild,true);
                    page.openEditor(input, id);
                } else {
                    parentNode.removeChild(createChild);
                }
            }
        }
    }

    /**
     * @return
     */
    @Nonnull
    public static String getEditorID() {
        return ID;
    }
}
