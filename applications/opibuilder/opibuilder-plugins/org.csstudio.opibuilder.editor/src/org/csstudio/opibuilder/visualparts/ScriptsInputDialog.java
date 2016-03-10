/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.scriptUtil.FileUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**The dialog for scripts input editing.
 * @author Xihui Chen
 *
 */
public class ScriptsInputDialog extends HelpTrayDialog {

    private Action addAction;
    private Action editAction;
    private Action removeAction;
    private Action moveUpAction;
    private Action moveDownAction;
    private Action convertToEmbedAction;


    private TableViewer scriptsViewer;
    private PVTupleTableEditor pvsEditor;
    private Button skipFirstExecutionButton;
    private Button checkConnectivityButton;
    private Button stopExecuteOnErrorButton;

    private List<ScriptData> scriptDataList;
    private String title;

    private IPath startPath;

    private AbstractWidgetModel widgetModel;

    public ScriptsInputDialog(Shell parentShell, ScriptsInput scriptsInput,
            String dialogTitle, AbstractWidgetModel widgetModel) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.scriptDataList = scriptsInput.getCopy().getScriptList();
        title = dialogTitle;
        this.widgetModel = widgetModel;
        this.startPath =
                widgetModel.getRootDisplayModel().getOpiFilePath().removeLastSegments(1);
    }


    @Override
    protected void okPressed() {
        pvsEditor.forceFocus();
        for(ScriptData scriptData : scriptDataList){
            boolean hasTrigger = false;
            for(PVTuple pvTuple : scriptData.getPVList()){
                hasTrigger |= pvTuple.trigger;
            }
            if(!hasTrigger){
                MessageDialog.openWarning(getShell(), "Warning",
                        NLS.bind("At least one trigger PV must be selected for the script:\n{0}",
                                scriptData.getPath().toString()));
                return;
            }
        }
        super.okPressed();
    }

    @Override
    protected String getHelpResourcePath() {
        return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Script.html"; //$NON-NLS-1$; //$NON-NLS-2$
    }

    /**
     * @return the scriptDataList
     */
    public final List<ScriptData> getScriptDataList() {
        return scriptDataList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /**
     * Creates 'wrapping' label with the given text.
     *
     * @param parent
     *            The parent for the label
     * @param text
     *            The text for the label
     */
    private void createLabel(final Composite parent, final String text) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(text);
        label.setLayoutData(new GridData(SWT.FILL, 0, false, false));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite parent_Composite = (Composite) super.createDialogArea(parent);

        // Parent composite has GridLayout with 1 columns.
        // Create embedded composite w/ 2 columns
        final Composite mainComposite = new Composite(parent_Composite, SWT.None);
        mainComposite.setLayout(new GridLayout(2, false));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 250;
        mainComposite.setLayoutData(gridData);

        // Left Panel: List of scripts
        final Composite leftComposite = new Composite(mainComposite, SWT.NONE);
        leftComposite.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 350;
        leftComposite.setLayoutData(gd);
        createLabel(leftComposite, "Scripts");

        Composite toolBarComposite = new Composite(leftComposite, SWT.BORDER);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        toolBarComposite.setLayout(gridLayout);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        toolBarComposite.setLayoutData(gd);

        ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
        GridData grid = new GridData();
        grid.horizontalAlignment = GridData.FILL;
        grid.verticalAlignment = GridData.BEGINNING;
        toolBar.setLayoutData(grid);
        createActions();
        toolbarManager.add(addAction);
        toolbarManager.add(editAction);
        toolbarManager.add(removeAction);
        toolbarManager.add(moveUpAction);
        toolbarManager.add(moveDownAction);
        toolbarManager.add(convertToEmbedAction);

        toolbarManager.update(true);

        scriptsViewer = createScriptsTableViewer(toolBarComposite);
        scriptsViewer.setInput(scriptDataList);

        // Right panel: Input PVs for selected script
        final Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        gridLayout = new GridLayout(1, false);
        rightComposite.setLayout(gridLayout);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumWidth = 250; // Account for the StringTableEditor's minimum size
        rightComposite.setLayoutData(gd);
        this.createLabel(rightComposite, "");

        TabFolder tabFolder = new TabFolder(rightComposite, SWT.None);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabItem pvTab = new TabItem(tabFolder, SWT.NONE);
        pvTab.setText("Input PVs");
        TabItem optionTab = new TabItem(tabFolder, SWT.NONE);
        optionTab.setText("Options");


        pvsEditor = new PVTupleTableEditor(tabFolder, new ArrayList<PVTuple>(), SWT.NONE);
        pvsEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        pvsEditor.setEnabled(false);

        pvTab.setControl(pvsEditor);

        final Composite optionTabComposite = new Composite(tabFolder, SWT.None);
        optionTabComposite.setLayout(new GridLayout(1, false));
        optionTab.setControl(optionTabComposite);
        skipFirstExecutionButton = new Button(optionTabComposite, SWT.CHECK|SWT.WRAP);
        skipFirstExecutionButton.setText("Skip executions triggered by PVs' first value.");
        skipFirstExecutionButton.setToolTipText(
            "Skip the script executions triggered by PVs' first connections during OPI startup.\n" +
            "This is useful if you want to trigger a script from user inputs only.");
        skipFirstExecutionButton.setSelection(false);
        skipFirstExecutionButton.setEnabled(false);
        skipFirstExecutionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) scriptsViewer.getSelection();
                if(!selection.isEmpty()){
                    ((ScriptData)selection.getFirstElement()).setSkipPVsFirstConnection(
                            skipFirstExecutionButton.getSelection());
                }
            }
        });
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        Point preferredSize = skipFirstExecutionButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        gd.widthHint = preferredSize.x;
        gd.minimumHeight = preferredSize.y;
        skipFirstExecutionButton.setLayoutData(gd);

        checkConnectivityButton = new Button(optionTabComposite, SWT.CHECK|SWT.WRAP);
        checkConnectivityButton.setSelection(false);
        checkConnectivityButton.setText(
                "Execute anyway even if some PVs are disconnected.");
        checkConnectivityButton.setToolTipText(
                "This is only useful if you want to handle PVs' disconnection in script.\nOtherwise, please keep it unchecked.");
        checkConnectivityButton.setEnabled(false);
        checkConnectivityButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) scriptsViewer.getSelection();
                if(!selection.isEmpty()){
                    ((ScriptData)selection.getFirstElement()).setCheckConnectivity(
                            !checkConnectivityButton.getSelection());
                }
                if(checkConnectivityButton.getSelection()){
                    MessageDialog.openWarning(getShell(), "Warning",
                            "If this option is checked, " +
                            "the script itself is responsible for checking PV's connectivity before using that PV in the script.\n" +
                            "Otherwise, you will probably get an error message with java.lang.NullPointerException. \n" +
                            "PV's connectivity can be checked via this method: pvArray[#].isConnected()");
                }
            }
        });
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        preferredSize = checkConnectivityButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        gd.widthHint = preferredSize.x;
        gd.minimumHeight = preferredSize.y;
        checkConnectivityButton.setLayoutData(gd);

        stopExecuteOnErrorButton = new Button(optionTabComposite, SWT.CHECK|SWT.WRAP);
        stopExecuteOnErrorButton.setSelection(false);
        stopExecuteOnErrorButton.setText(
                "Do not execute the script if error was detected.");
        stopExecuteOnErrorButton.setToolTipText(
                "If this option is selected, the script will not be executed \n" +
                "on next trigger if error was detected in the script.");
        stopExecuteOnErrorButton.setEnabled(false);
        stopExecuteOnErrorButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) scriptsViewer.getSelection();
                if(!selection.isEmpty()){
                    ((ScriptData)selection.getFirstElement()).setStopExecuteOnError(
                            stopExecuteOnErrorButton.getSelection());
                }
            }
        });
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        preferredSize = stopExecuteOnErrorButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        gd.widthHint = preferredSize.x;
        gd.minimumHeight = preferredSize.y;
        stopExecuteOnErrorButton.setLayoutData(gd);

        if(scriptDataList.size() > 0){
            setScriptsViewerSelection(scriptDataList.get(0));
            checkConnectivityButton.setSelection(
                    !scriptDataList.get(0).isCheckConnectivity());
            skipFirstExecutionButton.setSelection(
                    scriptDataList.get(0).isSkipPVsFirstConnection());
            stopExecuteOnErrorButton.setSelection(
                    scriptDataList.get(0).isStopExecuteOnError());

        }
        return parent_Composite;
    }

    /**
     * Refreshes the enabled-state of the actions.
     */
    private void refreshGUIOnSelection() {

        IStructuredSelection selection = (IStructuredSelection) scriptsViewer
                .getSelection();
        if (!selection.isEmpty()
                && selection.getFirstElement() instanceof ScriptData) {
            removeAction.setEnabled(true);
            moveUpAction.setEnabled(true);
            moveDownAction.setEnabled(true);
            convertToEmbedAction.setEnabled(
                    !((ScriptData)selection.getFirstElement()).isEmbedded());

            editAction.setEnabled(true);
            pvsEditor.updateInput(((ScriptData) selection
                    .getFirstElement()).getPVList());
            pvsEditor.setEnabled(true);
            checkConnectivityButton.setSelection(!((ScriptData) selection
                    .getFirstElement()).isCheckConnectivity());
            checkConnectivityButton.setEnabled(true);
            skipFirstExecutionButton.setSelection(((ScriptData) selection
                    .getFirstElement()).isSkipPVsFirstConnection());
            skipFirstExecutionButton.setEnabled(true);
            stopExecuteOnErrorButton.setSelection(((ScriptData) selection
                    .getFirstElement()).isStopExecuteOnError());
            stopExecuteOnErrorButton.setEnabled(true);

        } else {
            removeAction.setEnabled(false);
            moveUpAction.setEnabled(false);
            moveDownAction.setEnabled(false);
            convertToEmbedAction.setEnabled(false);
            pvsEditor.setEnabled(false);
            editAction.setEnabled(false);
            checkConnectivityButton.setEnabled(false);
            skipFirstExecutionButton.setEnabled(false);
            stopExecuteOnErrorButton.setEnabled(false);
        }
    }


    private void setScriptsViewerSelection(ScriptData scriptData){
        scriptsViewer.refresh();
        if(scriptData == null)
            scriptsViewer.setSelection(StructuredSelection.EMPTY);
        else {
            scriptsViewer.setSelection(new StructuredSelection(scriptData));
        }
    }


    /**
     * Creates and configures a {@link TableViewer}.
     *
     * @param parent
     *            The parent for the table
     * @return The {@link TableViewer}
     */
    private TableViewer createScriptsTableViewer(final Composite parent) {
        TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
        viewer.setContentProvider(new BaseWorkbenchContentProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public Object[] getElements(final Object element) {
                return (((List<ScriptData>)element).toArray());
            }
        });
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                refreshGUIOnSelection();
            }
        });
        viewer.getTable().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));
        return viewer;
    }




    /**
     * Creates the actions.
     */
    private void createActions() {
        addAction = new Action("Add") {
            @Override
            public void run() {
                ScriptChoiceDialog scriptChoiceDialog = new ScriptChoiceDialog(
                        getShell());
                if(scriptChoiceDialog.open() == Window.CANCEL)
                    return;
                ScriptData scriptData = null;
                if (scriptChoiceDialog.isEmbedded()) {
                    EmbeddedScriptEditDialog scriptEditDialog =
                            new EmbeddedScriptEditDialog(getShell(), null);
                    if(scriptEditDialog.open() == Window.OK)
                        scriptData = scriptEditDialog.getResult();
                }else {
                    IPath path;
                    RelativePathSelectionDialog rsd = new RelativePathSelectionDialog(
                            Display.getCurrent().getActiveShell(), startPath,
                            "Select a script file", new String[] {
                                    ScriptService.JS, ScriptService.PY });
                    rsd.setSelectedResource(new Path("./")); //$NON-NLS-1$
                    if (rsd.open() == Window.OK) {
                        if (rsd.getSelectedResource() != null) {
                            path = rsd.getSelectedResource();
                            scriptData = new ScriptData(path);
                        }
                    }
                }
                if(scriptData != null){
                    scriptDataList.add(scriptData);
                    setScriptsViewerSelection(scriptData);
                }
            }
        };
        addAction.setToolTipText("Add a script");
        addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/add.gif")); //$NON-NLS-1$

        editAction = new Action("Edit") {
            @Override
            public void run() {
                IPath path;
                IStructuredSelection selection = (IStructuredSelection) scriptsViewer.getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ScriptData) {
                    ScriptData sd = (ScriptData) selection.getFirstElement();
                    if(sd.isEmbedded()){
                        EmbeddedScriptEditDialog scriptEditDialog =
                                new EmbeddedScriptEditDialog(getShell(), sd);
                        if(scriptEditDialog.open() == Window.OK){
                            ScriptData newSd = scriptEditDialog.getResult();
                            sd.setScriptName(newSd.getScriptName());
                            sd.setScriptType(newSd.getScriptType());
                            sd.setScriptText(newSd.getScriptText());
                            setScriptsViewerSelection(sd);
                        }
                    }else{
                        RelativePathSelectionDialog rsd = new RelativePathSelectionDialog(
                                getShell(), startPath, "Select a script file",
                        new String[]{ScriptService.JS, ScriptService.PY});
                        rsd.setSelectedResource(((ScriptData)selection.getFirstElement()).getPath());
                        if (rsd.open() == Window.OK) {
                            if (rsd.getSelectedResource() != null) {
                                path = rsd.getSelectedResource();
                                sd.setPath(path);
                                setScriptsViewerSelection(sd);
                            }
                        }
                    }


                }

            }
        };
        editAction.setToolTipText("Edit/Change script path");
        editAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/edit.gif")); //$NON-NLS-1$
        editAction.setEnabled(false);
        removeAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) scriptsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ScriptData) {
                    scriptDataList.remove((ScriptData)selection.getFirstElement());
                    setScriptsViewerSelection(null);
                    this.setEnabled(false);
                }
            }
        };
        removeAction.setText("Remove Script");
        removeAction
                .setToolTipText("Remove the selected script from the list");
        removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/delete.gif")); //$NON-NLS-1$
        removeAction.setEnabled(false);

        moveUpAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) scriptsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ScriptData) {
                    ScriptData scriptData = (ScriptData) selection
                            .getFirstElement();
                    int i = scriptDataList.indexOf(scriptData);
                    if(i>0){
                        scriptDataList.remove(scriptData);
                        scriptDataList.add(i-1, scriptData);
                        setScriptsViewerSelection(scriptData);
                    }
                }
            }
        };
        moveUpAction.setText("Move Script Up");
        moveUpAction.setToolTipText("Move selected script up");
        moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_prev.gif")); //$NON-NLS-1$
        moveUpAction.setEnabled(false);

        moveDownAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) scriptsViewer
                        .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ScriptData) {
                    ScriptData scriptData = (ScriptData) selection
                            .getFirstElement();
                    int i = scriptDataList.indexOf(scriptData);
                    if(i<scriptDataList.size()-1){
                        scriptDataList.remove(scriptData);
                        scriptDataList.add(i+1, scriptData);
                        setScriptsViewerSelection(scriptData);
                    }
                }
            }
        };
        moveDownAction.setText("Move Script Down");
        moveDownAction.setToolTipText("Move selected script down");
        moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/search_next.gif")); //$NON-NLS-1$
        moveDownAction.setEnabled(false);

        convertToEmbedAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) scriptsViewer.getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof ScriptData) {
                    ScriptData sd = (ScriptData) selection.getFirstElement();
                    if(!sd.isEmbedded()){
                        IPath absoluteScriptPath = sd.getPath();
                        if(!absoluteScriptPath.isAbsolute()){
                            absoluteScriptPath = ResourceUtil.buildAbsolutePath(
                                    widgetModel, absoluteScriptPath);
                            if(!ResourceUtil.isExsitingFile(absoluteScriptPath, true)){
                                //search from OPI search path
                                absoluteScriptPath = ResourceUtil.getFileOnSearchPath(sd.getPath(), true);
                            }
                        }

                        try {
                            String text = FileUtil.readTextFile(absoluteScriptPath.toString());
                            String ext = absoluteScriptPath.getFileExtension().trim().toLowerCase();
                            if(ext.equals(ScriptService.JS))
                                sd.setScriptType(ScriptType.JAVASCRIPT);
                            else if(ext.equals(ScriptService.PY))
                                sd.setScriptType(ScriptType.PYTHON);
                            else{
                                MessageDialog.openError(getShell(),
                                    "Failed", "The script type is not recognized.");
                                return;
                            }
                            sd.setEmbedded(true);
                            sd.setScriptText(text);
                            sd.setScriptName(
                                    absoluteScriptPath.removeFileExtension().lastSegment());
                            setScriptsViewerSelection(sd);
                        } catch (Exception e) {
                            MessageDialog.openError(getShell(),
                                    "Failed", "Failed to read script file");
                        }

                    }


                }
            }
        };
        convertToEmbedAction.setText("Convert to Embedded Script");
        convertToEmbedAction.setToolTipText("Convert to Embedded Script");
        convertToEmbedAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
                        "icons/convertToEmbedded.png")); //$NON-NLS-1$
        convertToEmbedAction.setEnabled(false);
    }

}
