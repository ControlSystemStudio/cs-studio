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
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.editorparts.IocEditor;
import org.csstudio.config.ioconfig.editorparts.MasterEditor;
import org.csstudio.config.ioconfig.editorparts.ModuleEditor;
import org.csstudio.config.ioconfig.editorparts.SlaveEditor;
import org.csstudio.config.ioconfig.editorparts.SubnetEditor;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
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

    private static final String ID = "org.csstudio.config.ioconfig.commands.callNewChildrenEditor";

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
     * @throws PersistenceException 
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    @Override
    protected void openNodeEditor(@Nonnull final AbstractNodeDBO parentNode,
                                  @Nonnull final IWorkbenchPage page) throws PartInitException, 
                                                                             PersistenceException {
//        AbstractNodeDBO node = null;
//        String id = null;
//
//        if (parentNode instanceof FacilityDBO) {
//            id = IocEditor.ID;
//            node = new IocDBO((FacilityDBO)parentNode);
//        }else if (parentNode instanceof IocDBO) {
//            id = SubnetEditor.ID;
//            node = new ProfibusSubnetDBO((IocDBO) parentNode);
//        } else if (parentNode instanceof ProfibusSubnetDBO) {
//            id = MasterEditor.ID;
//            node = new MasterDBO((ProfibusSubnetDBO) parentNode);
//        } else if (parentNode instanceof MasterDBO) {
//            id = SlaveEditor.ID;
//            node = new SlaveDBO((MasterDBO) parentNode);
//        } else if (parentNode instanceof SlaveDBO) {
//            id = ModuleEditor.ID;
//            node = new ModuleDBO((SlaveDBO) parentNode);
//        }
        
        AbstractNodeDBO child = parentNode.createChild();
        String id = NodeEditorHandler.getEditorIdFor(child);
        	
        if((child != null) && (id != null)) {
            String nodeType = child.getNodeType().getName();
        	if(id.equals(ModuleEditor.ID)){
        		child.setName("");
        		child.setSortIndexNonHibernate(parentNode.getfirstFreeStationAddress(128));
        		NodeEditorInput input = new NodeEditorInput(child,true);
        		page.openEditor(input, id);
        	} else {
	        	InputDialog idialog = new InputDialog(null, "Create new " + nodeType,
	                                                  "Enter the name of the " + nodeType, nodeType, null);
	            idialog.setBlockOnOpen(true);
	            if (idialog.open() == Window.OK) {
	                // TODO: (hrickens) set the right max station Address
	                child.setSortIndexNonHibernate(parentNode.getfirstFreeStationAddress(128));
	                if((idialog.getValue()!=null)&&!idialog.getValue().isEmpty()) {
	                    child.setName(idialog.getValue());
	                } else {
	                    child.setName(nodeType);
	                }
	                NodeEditorInput input = new NodeEditorInput(child,true);
	                page.openEditor(input, id);
	            } else {
	                parentNode.removeChild(child);
	            }
        	}
        }
    }
    // CHECKSTYLE ON: CyclomaticComplexity

}
