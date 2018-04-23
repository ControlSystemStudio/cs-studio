/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.CompactModeAction;
import org.csstudio.opibuilder.actions.FullScreenAction;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.WorkbenchWindowService;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * The editor for running of OPI.
 *
 * @author Xihui Chen
 *
 */
//DO NOT REMOVE. RAP NEEDS THIS!
public class OPIRunner extends EditorPart implements IOPIRuntime{

    public static final String ID = "org.csstudio.opibuilder.OPIRunner"; //$NON-NLS-1$

    private OPIRuntimeDelegate opiRuntimeDelegate;

    public OPIRunner() {
        opiRuntimeDelegate = new OPIRuntimeDelegate(this);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void dispose() {
        if (opiRuntimeDelegate != null) {
            opiRuntimeDelegate.dispose();
            opiRuntimeDelegate = null;
        }
        super.dispose();
    }

    @Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException {
        setSite(site);

        setInput(!(input instanceof IRunnerInput) && !(input instanceof NoResourceEditorInput) ?
                        new NoResourceEditorInput(input) : input);
        opiRuntimeDelegate.init(site, input instanceof NoResourceEditorInput ?
                ((NoResourceEditorInput)input).getOriginEditorInput() : input);
    }

    @Override
    public void setOPIInput(IEditorInput input) throws PartInitException {
        init(getEditorSite(), input);
    }


    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(final Composite parent) {
        opiRuntimeDelegate.createGUI(parent);
        //if this is the first OPI in this window, resize the window to match the OPI size.
        //Make it in compact or fullscreen mode if either is configured.
        if(OPIBuilderPlugin.isRAP())
            return;
        Display.getCurrent().asyncExec(new Runnable() {

            @Override
            public void run() {
                IEditorReference[] editorReferences = getSite().getWorkbenchWindow().getActivePage().getEditorReferences();
                if (editorReferences.length > 0 && editorReferences[0].getEditor(true) == OPIRunner.this
                        && getSite().getWorkbenchWindow().getActivePage().getViewReferences().length == 0) {
                    if (WorkbenchWindowService.isInFullScreenMode()) {
                        FullScreenAction fullScreenAction = WorkbenchWindowService.getInstance()
                                .getFullScreenAction(getSite().getWorkbenchWindow());
                        if (!fullScreenAction.isInFullScreen())
                            fullScreenAction.run();
                    } else {
                        if (WorkbenchWindowService.isInCompactMode()) {
                            CompactModeAction compactAction = WorkbenchWindowService.getInstance()
                                    .getCompactModeAction(getSite().getWorkbenchWindow());
                            if (!compactAction.isInCompactMode())
                                compactAction.run();
                        }
                    }

                }
            }
        });

    }

    @Override
    public void setFocus() {}

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (opiRuntimeDelegate != null) {
            T obj = opiRuntimeDelegate.getAdapter(adapter);
            if(obj != null)
                return obj;
        }
        return super.getAdapter(adapter);

    }

    @Override
    public void setWorkbenchPartName(String name) {
        setPartName(name);
        setTitleToolTip(getEditorInput().getToolTipText());
    }

    public OPIRuntimeDelegate getOPIRuntimeDelegate() {
        return opiRuntimeDelegate;
    }

    @Override
    public IEditorInput getOPIInput() {
        return getOPIRuntimeDelegate().getEditorInput();
    }

    @Override
    public DisplayModel getDisplayModel() {
        return getOPIRuntimeDelegate().getDisplayModel();
    }
}
