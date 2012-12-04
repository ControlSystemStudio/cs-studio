/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**The dialog for rules input editing.
 * @author Xihui Chen
 *
 */
public class RulesInputDialog extends HelpTrayDialog {
	
	private Action addAction;
	private Action editAction;
	private Action copyAction;
	private Action removeAction;
	private Action moveUpAction;
	private Action moveDownAction;
	
	private ListViewer rulesViewer;
	
	
	private List<RuleData> ruleDataList;
	private String title;
	private AbstractWidgetModel widgetModel;	

	
	public RulesInputDialog(Shell parentShell, RulesInput scriptsInput, AbstractWidgetModel widgetModel, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.ruleDataList = scriptsInput.getCopy().getRuleDataList();
		title = dialogTitle;
		this.widgetModel = widgetModel;
	}
	
	
	@Override
	protected void okPressed() {	
		for(RuleData ruleData : ruleDataList){
			boolean hasTrigger = false;
			for(PVTuple pvTuple : ruleData.getPVList()){
				hasTrigger |= pvTuple.trigger;
			}
			if(!hasTrigger){
				MessageDialog.openWarning(getShell(), "Warning", 
						NLS.bind("At least one trigger PV must be selected for the rule:\n{0}", 
								ruleData.getName().toString()));
				return;
			}
		}
		super.okPressed();
	}
	
	@Override
	protected String getHelpResourcePath() {
		return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Rules.html"; //$NON-NLS-1$; //$NON-NLS-2$
	}
	
	
	/**
	 * @return the ruleData List
	 */
	public final List<RuleData> getRuleDataList() {
		return ruleDataList;
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
		mainComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 200;
		mainComposite.setLayoutData(gridData);
		
		
		createLabel(mainComposite, "Rules");
		
		Composite toolBarComposite = new Composite(mainComposite, SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		toolBarComposite.setLayout(gridLayout);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		toolBarComposite.setLayoutData(gridData);
		
		ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
		GridData grid = new GridData();
		grid.horizontalAlignment = GridData.FILL;
		grid.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(grid);
		createActions();
		toolbarManager.add(addAction);
		toolbarManager.add(editAction);
		toolbarManager.add(copyAction);
		toolbarManager.add(removeAction);
		toolbarManager.add(moveUpAction);
		toolbarManager.add(moveDownAction);
		
		toolbarManager.update(true);
		
		rulesViewer = createRulsListViewer(toolBarComposite);
		rulesViewer.setInput(ruleDataList);
		rulesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				refreshToolbarOnSelection();				
			}
		});				
		
		return parent_Composite;
	}
	
	
	
	private void setRulesViewerSelection(RuleData ruleData){
		rulesViewer.refresh();
		if(ruleData == null){
			rulesViewer.setSelection(StructuredSelection.EMPTY);
		}
		else {
			rulesViewer.setSelection(new StructuredSelection(ruleData));
		}
		refreshToolbarOnSelection();
	}
	
	private void refreshToolbarOnSelection(){
		boolean enabled = !rulesViewer.getSelection().isEmpty();
		removeAction.setEnabled(enabled);
		editAction.setEnabled(enabled);	
		copyAction.setEnabled(enabled);
		moveUpAction.setEnabled(enabled);
		moveDownAction.setEnabled(enabled);
	}
	
	
	/**
	 * Creates and configures a {@link TableViewer}.
	 * 
	 * @param parent
	 *            The parent for the table
	 * @return The {@link TableViewer}
	 */
	private ListViewer createRulsListViewer(final Composite parent) {
		final ListViewer viewer = new ListViewer(parent, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		viewer.setContentProvider(new BaseWorkbenchContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(final Object element) {
				return (((List<RuleData>)element).toArray());
			}
		});
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				invokeRuleDataDialog();
			}
		});
		viewer.getList().setLayoutData(
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
				RuleDataEditDialog dialog = new RuleDataEditDialog(getShell(), new RuleData(widgetModel));
			
				if(dialog.open() == OK){
					ruleDataList.add(dialog.getOutput());
					rulesViewer.refresh();
				}
			}
		};
		addAction.setToolTipText("Add a Rule");
		addAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/add.gif")); //$NON-NLS-1$
		
		editAction = new Action("Edit") {
			@Override
			public void run() {
				invokeRuleDataDialog();
			}
		};
		editAction.setToolTipText("Edit Selected Rule");
		editAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/edit.gif")); //$NON-NLS-1$
		editAction.setEnabled(false);
		
		copyAction = new Action("Copy") {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) rulesViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof RuleData) {
					RuleData ruleData = ((RuleData) selection
							.getFirstElement()).getCopy();
					ruleDataList.add(ruleData);					
					setRulesViewerSelection(ruleData);				
				}
			}
		};
		copyAction.setToolTipText("Copy Selected Rule");
		copyAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/copy.gif")); //$NON-NLS-1$
		copyAction.setEnabled(false);
		
		removeAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) rulesViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof RuleData) {
					ruleDataList.remove((RuleData)selection.getFirstElement());
					setRulesViewerSelection(null);
					this.setEnabled(false);
				}
			}
		};
		removeAction
				.setToolTipText("Remove Selected Rule");
		removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/delete.gif")); //$NON-NLS-1$
		removeAction.setEnabled(false);

		moveUpAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) rulesViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof RuleData) {
					RuleData ruleData = (RuleData) selection
							.getFirstElement();
					int i = ruleDataList.indexOf(ruleData);
					if(i>0){
						ruleDataList.remove(ruleData);
						ruleDataList.add(i-1, ruleData);
						setRulesViewerSelection(ruleData);
					}	
				}
			}
		};
		moveUpAction.setText("Move Rule Up");
		moveUpAction.setToolTipText("Move Selected Rule up");
		moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_prev.gif")); //$NON-NLS-1$
		moveUpAction.setEnabled(false);

		moveDownAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) rulesViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof RuleData) {
					RuleData ruleData = (RuleData) selection
							.getFirstElement();
					int i = ruleDataList.indexOf(ruleData);
					if(i<ruleDataList.size()-1){
						ruleDataList.remove(ruleData);
						ruleDataList.add(i+1, ruleData);
						setRulesViewerSelection(ruleData);
					}			
				}
			}
		};
		moveDownAction.setText("Move Rule Down");
		moveDownAction.setToolTipText("Move Selected Rule Down");
		moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_next.gif")); //$NON-NLS-1$
		moveDownAction.setEnabled(false);
	}


	/**
	 * 
	 */
	private void invokeRuleDataDialog() {
		RuleData selection = 
			(RuleData)((IStructuredSelection)rulesViewer.getSelection()).getFirstElement();
		if(selection == null)
			return;
		RuleDataEditDialog dialog = 
			new RuleDataEditDialog(rulesViewer.getControl().getShell(), selection);
		if(dialog.open() == OK){
			RuleData result = dialog.getOutput();
			int index = ruleDataList.indexOf(selection);
			ruleDataList.remove(index);
			ruleDataList.add(index, result);
			rulesViewer.refresh();
		}		
	}
}
