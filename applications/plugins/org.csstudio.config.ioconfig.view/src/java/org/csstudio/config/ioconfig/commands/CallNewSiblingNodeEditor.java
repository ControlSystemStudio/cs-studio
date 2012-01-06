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
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
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
public class CallNewSiblingNodeEditor extends AbstractCallNodeEditor {

    private static final String ID = "org.csstudio.config.ioconfig.commands.callNewSiblingEditor";//$NON-NLS-1$

    /**
     * @param siblingNode
     * @param page
     * @param node
     * @param id
     * @param nodeType
     * @param idialog
     * @throws PersistenceException
     * @throws PartInitException
     */
    private void performOpen(@Nonnull final AbstractNodeSharedImpl<?,?> siblingNode,
                             @Nonnull final IWorkbenchPage page,
                             @Nonnull final AbstractNodeSharedImpl<?,?> node,
                             @Nonnull final String id,
                             @Nonnull final String name) throws PersistenceException, PartInitException {
        node.setSortIndexNonHibernate(siblingNode.getSortIndex() + 1);
        node.setName(name);
        final NodeEditorInput input = new NodeEditorInput(node, true);
        page.openEditor(input, id);
    }

    /**
     * {@inheritDoc}
     * @throws PartInitException
     * @throws PersistenceException
     */
    @Override
    protected void openNodeEditor(@Nonnull final AbstractNodeSharedImpl<AbstractNodeSharedImpl<?, ?>, AbstractNodeSharedImpl<?, ?>> siblingNode,
                                  @Nonnull final IWorkbenchPage page) throws PartInitException,
                                  PersistenceException {
        final AbstractNodeSharedImpl<?,?> node = (AbstractNodeSharedImpl<?, ?>) siblingNode.getParent().createChild();
        final String id = NodeEditorHandler.getEditorIdFor(node);

        if( node != null && id != null) {
            if(id.equals(ModuleEditor.ID)) {
                performOpen(siblingNode, page, node, id, " ");
            } else {
                final String nodeType = node.getNodeType().getName();
                final String title = String.format(Messages.NodeEditor_Title, nodeType);
                final String msg = String.format(Messages.NodeEditor_Msg, nodeType);
                final InputDialog idialog =
                    new InputDialog(null, title, msg, siblingNode.getName(), null);
                idialog.setBlockOnOpen(true);
                if(idialog.open() == Window.OK) {
                    String name;
                    if( idialog.getValue() != null && !idialog.getValue().isEmpty()) {
                        name = idialog.getValue();
                    } else {
                        name = nodeType;
                    }
                    performOpen(siblingNode, page, node, id, name);
                } else {
                    siblingNode.removeChild(node);
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
