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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.editorparts.ChannelEditor;
import org.csstudio.config.ioconfig.editorparts.ChannelStructureEditor;
import org.csstudio.config.ioconfig.editorparts.FacilityEditor;
import org.csstudio.config.ioconfig.editorparts.IocEditor;
import org.csstudio.config.ioconfig.editorparts.MasterEditor;
import org.csstudio.config.ioconfig.editorparts.ModuleEditor;
import org.csstudio.config.ioconfig.editorparts.SlaveEditor;
import org.csstudio.config.ioconfig.editorparts.SubnetEditor;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.view.MainView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 01.04.2010
 */
public class CallEditor extends AbstractHandler {

    public static final String ID = "org.csstudio.config.ioconfig.commands.callEditor";

    /**
     * (@inheritDoc)
     */
    @Override
    @CheckForNull
    public Object execute(@Nonnull final ExecutionEvent event) throws ExecutionException {

        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();

        AbstractNodeDBO obj = getCallerNode(page);

        NodeEditorInput input = new NodeEditorInput(obj);
        try {
            if (obj instanceof FacilityDBO) {
                page.openEditor(input, FacilityEditor.ID);
            } else if (obj instanceof IocDBO) {
                page.openEditor(input, IocEditor.ID);
            } else if (obj instanceof ProfibusSubnetDBO) {
                page.openEditor(input, SubnetEditor.ID);
            } else if (obj instanceof MasterDBO) {
                page.openEditor(input, MasterEditor.ID);
            } else if (obj instanceof SlaveDBO) {
                page.openEditor(input, SlaveEditor.ID);
            } else if (obj instanceof ModuleDBO) {
                page.openEditor(input, ModuleEditor.ID);
            } else if (obj instanceof ChannelDBO) {
                page.openEditor(input, ChannelEditor.ID);
            } else if (obj instanceof ChannelStructureDBO) {
                page.openEditor(input, ChannelStructureEditor.ID);
            }
        } catch (PartInitException e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    /**
     * @return
     */
    @CheckForNull
    private AbstractNodeDBO getCallerNode(final IWorkbenchPage page) {
        MainView view = (MainView) page.findView(MainView.ID);
        // Get the selection
        ISelection selection = view.getSite().getSelectionProvider().getSelection();
        if ( (selection != null) && (selection instanceof IStructuredSelection)) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            // If we had a selection lets open the editor
            if ( (obj != null) && (obj instanceof AbstractNodeDBO)) {
                return (AbstractNodeDBO) obj;
            }
        }
        return null;
    }
}
