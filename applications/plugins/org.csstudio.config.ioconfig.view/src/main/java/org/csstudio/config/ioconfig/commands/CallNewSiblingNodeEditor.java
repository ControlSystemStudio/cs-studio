/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.commands;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.editorparts.IocEditor;
import org.csstudio.config.ioconfig.editorparts.MasterEditor;
import org.csstudio.config.ioconfig.editorparts.ModuleEditor;
import org.csstudio.config.ioconfig.editorparts.SlaveEditor;
import org.csstudio.config.ioconfig.editorparts.SubnetEditor;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 10.06.2010
 */
public class CallNewSiblingNodeEditor extends AbstractCallNodeEditor {

    private static final String ID = "org.csstudio.config.ioconfig.commands.callNewSiblingEditor";
    /**
     * @return
     */
    @Nonnull
    public static String getEditorID() {
        return ID;
    }

    /**
     * {@inheritDoc}
     * @throws PartInitException
     */
    @Override
    protected void abcde(final Node siblingNode,@Nonnull final IWorkbenchPage page) throws PartInitException {
        Node node = null;
        String id = null;
        String nodeType = "";
        if (siblingNode instanceof Ioc) {
            id = IocEditor.ID;
            node = new Ioc(((Ioc)siblingNode).getFacility());
            nodeType = "Ioc";
        } else if (siblingNode instanceof ProfibusSubnet) {
            id = SubnetEditor.ID;
            node = new ProfibusSubnet(((ProfibusSubnet)siblingNode).getIoc());
            nodeType = "Subnet";
        } else if (siblingNode instanceof Master) {
            id = MasterEditor.ID;
            node = new Master(((Master)siblingNode).getProfibusSubnet());
            nodeType = "Master";
        } else if (siblingNode instanceof Slave) {
            id = SlaveEditor.ID;
            node = new Slave(((Slave)siblingNode).getProfibusDPMaster());
            nodeType = "Slave";
        } else if (siblingNode instanceof Module) {
            id = ModuleEditor.ID;
            node = new Module(((Module)siblingNode).getSlave());
            nodeType = "Module";
        }
        if((node != null) && (id != null)) {
            InputDialog idialog = new InputDialog(null, "Create new " + nodeType,
                                                  "Enter the name of the " + nodeType, siblingNode.getName(), null);
            idialog.setBlockOnOpen(true);
            if (idialog.open() == Dialog.OK) {
                node.setSortIndexNonHibernate(siblingNode.getSortIndex()+1);
                if((idialog.getValue()!=null)&&!idialog.getValue().isEmpty()) {
                    node.setName(idialog.getValue());
                } else {
                    node.setName(nodeType);
                }
                NodeEditorInput input = new NodeEditorInput(node,true);
                page.openEditor(input, id);
            } else {
                siblingNode.removeChild(node);
            }
        }
    }

}
