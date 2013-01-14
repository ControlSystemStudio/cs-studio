package org.csstudio.config.ioconfig.commands;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.editorinputs.NodeEditorInput;
import org.csstudio.config.ioconfig.editorparts.FacilityEditor;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Caller for a Facility Editor with a new Facility.
 * 
 * @author hrickens
 * @author $Author: $
 * @since 01.10.2010
 */
public class CallNewFacilityEditor extends AbstractHandler {
    
    public static final String ID = "org.csstudio.config.ioconfig.commands.callNewFacilityEditor";//$NON-NLS-1$
    private static final Logger LOG = LoggerFactory.getLogger(CallNewFacilityEditor.class);
    
    @Override
    @CheckForNull
    public Object execute(@Nonnull final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        final IWorkbenchPage page = window.getActivePage();
        
        final FacilityDBO facilityDBO = new FacilityDBO();
        facilityDBO.setName("new Facility"); //$NON-NLS-1$
        facilityDBO.setSortIndex(0);
        final NodeEditorInput input = new NodeEditorInput(facilityDBO, true);
        try {
            page.openEditor(input, FacilityEditor.ID);
        } catch (final PartInitException e) {
            LOG.error("Can't open Facility Editor Error:", e);//$NON-NLS-1$
        }
        return null;
    }
    
}
