package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**The dialog for scripts input editing.
 * @author Xihui Chen
 *
 */
public class ScriptsInputDialog extends Dialog {
	
	private Action addAction;
	private Action editAction;
	private Action removeAction;
	private Action moveUpAction;
	private Action moveDownAction;
	
	private TableViewer scriptsViewer;
	private PVTupleTableEditor pvsEditor;
	
	
	private List<ScriptData> scriptDataList;
	private String title;	

	private IPath startPath;
	
	public ScriptsInputDialog(Shell parentShell, ScriptsInput scriptsInput, IPath startPath, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.scriptDataList = scriptsInput.getCopy().getScriptList();
		title = dialogTitle;
		this.startPath = startPath;
	}
	
	
	@Override
	protected void okPressed() {	
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
		gridData.heightHint = 200;
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
		this.createLabel(rightComposite, "Input PVs for the selected script");
		
		pvsEditor = new PVTupleTableEditor(rightComposite, new ArrayList<PVTuple>());
		pvsEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pvsEditor.setEnabled(false);
		
		if(scriptDataList.size() > 0)
			setScriptsViewerSelection(scriptDataList.get(0));
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
			editAction.setEnabled(true);
			pvsEditor.updateInput(((ScriptData) selection
					.getFirstElement()).getPVList());
			pvsEditor.setEnabled(true);
		} else {
			removeAction.setEnabled(false);
			moveUpAction.setEnabled(false);
			moveDownAction.setEnabled(false);
			pvsEditor.setEnabled(false);
			editAction.setEnabled(false);
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
				IPath path;				
				RelativePathSelectionDialog rsd = new RelativePathSelectionDialog(
						Display.getCurrent().getActiveShell(), startPath, "Select a java script file", new String[]{"js"});
				if (rsd.open() == Window.OK) {
					if (rsd.getSelectedResource() != null) {
						path = rsd.getSelectedResource();
						ScriptData scriptData = new ScriptData(path);
						scriptDataList.add(scriptData);
						setScriptsViewerSelection(scriptData);
					}
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
					RelativePathSelectionDialog rsd = new RelativePathSelectionDialog(
					Display.getCurrent().getActiveShell(), startPath, "Select a java script file", new String[]{"js"});
					rsd.setSelectedResource(((ScriptData)selection.getFirstElement()).getPath());
					if (rsd.open() == Window.OK) {
						if (rsd.getSelectedResource() != null) {
							path = rsd.getSelectedResource();
							scriptDataList.get(scriptDataList.indexOf(
									(ScriptData)selection.getFirstElement())).setPath(path);
							setScriptsViewerSelection((ScriptData)selection.getFirstElement());
						}
					}					
				}
				
			}
		};
		editAction.setToolTipText("Change the script path");
		editAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/folder.gif")); //$NON-NLS-1$
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
	}
}
