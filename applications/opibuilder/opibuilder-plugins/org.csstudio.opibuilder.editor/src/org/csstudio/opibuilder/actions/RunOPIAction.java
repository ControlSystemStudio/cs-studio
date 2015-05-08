/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * The Action to Run an OPI.
 * @author Xihui Chen
 *
 */
public class RunOPIAction extends Action implements IWorkbenchWindowActionDelegate{

    public static String ID = "org.csstudio.opibuilder.editor.run"; //$NON-NLS-1$
    public static String ACITON_DEFINITION_ID = "org.csstudio.opibuilder.runopi"; //$NON-NLS-1$

  public RunOPIAction() {
     super("Run OPI", CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
             OPIBuilderPlugin.PLUGIN_ID, "icons/run.gif"));     //$NON-NLS-1$
     setId(ID);
     setActionDefinitionId(ACITON_DEFINITION_ID);
  }

  @Override
    public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart activeEditor = page.getActiveEditor();
        if(activeEditor instanceof OPIEditor){
            if(PreferencesHelper.isAutoSaveBeforeRunning() && activeEditor.isDirty())
                activeEditor.doSave(null);

            //It seems that the synch with editor is not necessary
            DisplayModel displayModel = ((OPIEditor)activeEditor).getDisplayModel();

            IEditorInput input = activeEditor.getEditorInput();


            IPath path = null;
                path = ResourceUtil.getPathInEditor(input);
                RunModeService.getInstance().runOPI(path, TargetWindow.RUN_WINDOW,
                        new Rectangle(displayModel.getLocation(), displayModel.getSize()));

        }

    }

      @Override
      public boolean isEnabled() {
          return true;
      }

      @Override
      public void setEnabled(boolean enabled) {
          super.setEnabled(true);
      }

    public void dispose() {

    }

    public void init(IWorkbenchWindow window) {

    }

    public void run(IAction action) {
        run();
    }

    public void selectionChanged(IAction action, ISelection selection) {

    }
}
