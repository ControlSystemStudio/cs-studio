package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.properties.support.ScriptData;
import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.csstudio.platform.ui.dialogs.ResourceSelectionDialog;
import org.csstudio.platform.ui.swt.stringtable.StringTableEditor;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
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

public class ScriptsInputDialog extends Dialog {
	
	private Action addAction;
	private Action removeAction;
	private Action moveUpAction;
	private Action moveDownAction;
	
	private TableViewer scriptsViewer;
	private StringTableEditor pvsEditor;
	
	
	private List<ScriptData> scriptDataList;
	private String title;	

	public ScriptsInputDialog(Shell parentShell, ScriptsInput scriptsInput, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.scriptDataList = scriptsInput.getCopy().getScriptList();
		title = dialogTitle;
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
	 * Creates a label with the given text.
	 * 
	 * @param parent
	 *            The parent for the label
	 * @param text
	 *            The text for the label
	 */
	private void createLabel(final Composite parent, final String text) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
				false, 2, 1));
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		
		final Composite mainComposite = new Composite(parent_Composite, SWT.None);			
		mainComposite.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 200;
		mainComposite.setLayoutData(gridData);
		final Composite leftComposite = new Composite(mainComposite, SWT.None);
		leftComposite.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		leftComposite.setLayoutData(gd);
		createLabel(leftComposite, "The Scripts:");
		
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
		gd.widthHint = 200;
		toolBarComposite.setLayoutData(gd);
		
		ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
		GridData grid = new GridData();
		grid.horizontalAlignment = GridData.FILL;
		grid.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(grid);
		createActions();
		toolbarManager.add(addAction);
		toolbarManager.add(removeAction);
		toolbarManager.add(moveUpAction);
		toolbarManager.add(moveDownAction);
		toolbarManager.update(true);
		
		scriptsViewer = createScriptsTableViewer(toolBarComposite);
		scriptsViewer.setInput(scriptDataList);
		
		Composite rightComposite = new Composite(mainComposite, SWT.NONE);
		rightComposite.setLayout(new GridLayout(1, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 300;
		rightComposite.setLayoutData(gd);
		this.createLabel(rightComposite, "The PVs for the selected script, \n " +
				"which will trigger the execution of the script.");
		
		pvsEditor = new StringTableEditor(rightComposite, new ArrayList<String>());
		pvsEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pvsEditor.setEnabled(false);
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
			pvsEditor.updateInput(((ScriptData) selection
					.getFirstElement()).getPVList());
			pvsEditor.setEnabled(true);
		} else {
			removeAction.setEnabled(false);
			moveUpAction.setEnabled(false);
			moveDownAction.setEnabled(false);
			pvsEditor.setEnabled(false);
		}
	}
	
	
	private void refreshScriptsViewer(ScriptData scriptData){
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
				ResourceSelectionDialog rsd = new ResourceSelectionDialog(
						Display.getCurrent().getActiveShell(), "Select a java script file", new String[]{"js"});
				if (rsd.open() == Window.OK) {
					if (rsd.getSelectedResource() != null) {
						path = rsd.getSelectedResource();
						ScriptData scriptData = new ScriptData(path);
						scriptDataList.add(scriptData);
						refreshScriptsViewer(scriptData);
					}
				}
			}
		};
		addAction.setToolTipText("Add a script");
		addAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/add.gif"));
		
		removeAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) scriptsViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof ScriptData) {
					scriptDataList.remove((ScriptData)selection.getFirstElement());
					refreshScriptsViewer(null);
					this.setEnabled(false);
				}
			}
		};
		removeAction.setText("Remove Script");
		removeAction
				.setToolTipText("Removes the selected Action from the list");
		removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/delete.gif"));
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
						refreshScriptsViewer(scriptData);
					}	
				}
			}
		};
		moveUpAction.setText("Move Up Action");
		moveUpAction.setToolTipText("Move up the selected Action");
		moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_prev.gif"));
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
						refreshScriptsViewer(scriptData);
					}			
				}
			}
		};
		moveDownAction.setText("Move Down Action");
		moveDownAction.setToolTipText("Move down the selected Action");
		moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_next.gif"));
		moveDownAction.setEnabled(false);
	}
	
	

	

}
