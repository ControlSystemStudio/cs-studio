package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.eventhandling.BehaviorDescriptor;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

/**
 * Dialog that allows for choosing the main process variable and widget behavior
 * as well as the widget type (optional).
 * 
 * @author Sven Wende, Kai Meyer
 */
final class WidgetCreationDialog extends Dialog {

	private List<IProcessVariableAddress> processVariables;
	private TreeViewer widgetTypeViewerLeft;
	private ComboViewer processVariablesCombo;
	private Text processVariableText;
	private String selectedWidgetType;
	private String selectedProcessVariable;
	private ListViewer behaviorViewer;
	private String selectedBehaviourId;
	private String fixWidgetType;
	private Label processVariableLabel;
	private Label widgetTypeLabel;
	private Label behaviorLabel;
	private TreeViewer widgetTypeViewerRight;

	public WidgetCreationDialog(final Shell parentShell, List<IProcessVariableAddress> processVariables, String fixWidgetType) {
		super(parentShell);
		this.processVariables = processVariables != null ? processVariables : new ArrayList<IProcessVariableAddress>();
		this.fixWidgetType = fixWidgetType;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Configure Widget");
		shell.setSize(525, fixWidgetType != null ? 280 : 600);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite dialogArea = new Composite(parent, SWT.NONE);
		dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		dialogArea.setFont(parent.getFont());
		dialogArea.setLayout(new FormLayout());

		createProcessVariableLabel(dialogArea);

		if (processVariables.size() > 1) {
			createProcessVariableCombo(dialogArea);
		} else {
			createProcessVariableTextbox(dialogArea);
		}

		createBehaviorLabel(dialogArea);
		createBehaviourViewer(dialogArea);

		if (fixWidgetType != null) {
			updateBehaviours(fixWidgetType);
		} else {
			createWidgetTypeLabel(dialogArea);
			createWidgetTypeViewers(dialogArea);
		}

		return dialogArea;
	}

	private void createWidgetTypeLabel(Composite dialogArea) {
		widgetTypeLabel = new Label(dialogArea, SWT.NONE);
		widgetTypeLabel.setText("Widget type:");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(widgetTypeViewerLeft != null ? widgetTypeViewerLeft.getControl() : behaviorViewer.getControl(), 10);
		fd.right = new FormAttachment(100, -10);
		widgetTypeLabel.setLayoutData(fd);
	}

	private void createBehaviorLabel(Composite dialogArea) {
		behaviorLabel = new Label(dialogArea, SWT.NONE);
		behaviorLabel.setText("Behavior:");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(processVariableText != null ? processVariableText : processVariablesCombo.getControl(), 10);
		fd.right = new FormAttachment(100, -10);
		behaviorLabel.setLayoutData(fd);
	}

	private void createProcessVariableLabel(Composite dialogArea) {
		processVariableLabel = new Label(dialogArea, SWT.NONE);
		processVariableLabel.setText("Process variable:");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(0, 10);
		fd.right = new FormAttachment(100, -10);
		processVariableLabel.setLayoutData(fd);
	}

	private void createBehaviourViewer(Composite composite) {
		behaviorViewer = new ListViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(behaviorLabel, 5);
		fd.right = new FormAttachment(100, -10);

		if (fixWidgetType == null) {
			fd.height = 85;
		} else {
			fd.bottom = new FormAttachment(100, -10);
		}
		behaviorViewer.getControl().setLayoutData(fd);

		behaviorViewer.setContentProvider(new ArrayContentProvider());
		behaviorViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				BehaviorDescriptor d = (BehaviorDescriptor) element;
				return d.getDescription();
			}
		});

	}

	private void updateBehaviours(String widgetType) {
		List<BehaviorDescriptor> result = new ArrayList<BehaviorDescriptor>();
		result.add(new BehaviorDescriptor("none", "*", "None", Collections.EMPTY_SET, null));
		result.addAll(SdsPlugin.getDefault().getBehaviourService().getBehaviors(widgetType));
		behaviorViewer.setInput(result);
		behaviorViewer.getControl().getParent().layout();
		behaviorViewer.getControl().redraw();
	}

	private void createProcessVariableTextbox(Composite parent) {
		processVariableText = new Text(parent, SWT.None);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(processVariableLabel, 5);
		fd.right = new FormAttachment(100, -10);
		fd.height = 20;
		processVariableText.setLayoutData(fd);
		processVariableText.setText(processVariables.size() > 0 ? processVariables.get(0).getFullName() : "");
	}

	private void createProcessVariableCombo(Composite parent) {
		CCombo combo = new CCombo(parent, SWT.None);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(processVariableLabel, 5);
		fd.right = new FormAttachment(100, -10);
		fd.height = 30;
		combo.setLayoutData(fd);

		processVariablesCombo = new ComboViewer(combo);
		processVariablesCombo.setContentProvider(ArrayContentProvider.getInstance());
		processVariablesCombo.setLabelProvider(new LabelProvider());
		processVariablesCombo.setInput(processVariables.toArray());
		processVariablesCombo.setSelection(new StructuredSelection(processVariables.get(0)));
	}

	private final class WidgetTypeSelectionListener implements ISelectionChangedListener {
		private TreeViewer secondWidgetTypeViewer;

		public WidgetTypeSelectionListener(TreeViewer secondWidgetTypeViewer) {
			this.secondWidgetTypeViewer = secondWidgetTypeViewer;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection() != null) {
				if (!event.getSelection().isEmpty()) {
					WidgetTypeInput widgetType = (WidgetTypeInput) ((IStructuredSelection) event.getSelection()).getFirstElement();
					if (widgetType != null) {
						updateBehaviours(widgetType.getTypeId());
					}
					secondWidgetTypeViewer.setSelection(new StructuredSelection());
				}
			}
		}
	}

	private void createWidgetTypeViewers(final Composite parent) {
		Set<String> allWidgetTypes = WidgetModelFactoryService.getInstance().getUsedWidgetTypes();
		WidgetTypeInput[] widgetTypes = new WidgetTypeInput[allWidgetTypes.size()];
		int i = 0;
		for (String typeId : allWidgetTypes) {
			WidgetTypeInput widgetType = new WidgetTypeInput();
			widgetType.setName(WidgetModelFactoryService.getInstance().getName(typeId));
			widgetType.setTypeId(typeId);
			String contributingPluginId = WidgetModelFactoryService.getInstance().getContributingPluginId((String) typeId);
			String iconPath = WidgetModelFactoryService.getInstance().getIcon((String) typeId);
			widgetType.setIcon(CustomMediaFactory.getInstance().getImageFromPlugin(contributingPluginId, iconPath));
			widgetTypes[i++] = widgetType;
		}

		Arrays.sort(widgetTypes);

		int n = widgetTypes.length;
		int cut = ((n - (n % 2)) / 2) + n % 2;

		widgetTypeViewerLeft = new TreeViewer(parent, SWT.SINGLE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.top = new FormAttachment(widgetTypeLabel, 5);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(100, -10);
		widgetTypeViewerLeft.getControl().setLayoutData(fd);
		widgetTypeViewerLeft.setContentProvider(new WidgetTypeContentProvider());
		widgetTypeViewerLeft.setLabelProvider(new WidgetTypeLabelProvider());
		widgetTypeViewerLeft.setInput(Arrays.copyOfRange(widgetTypes, 0, cut - 1));

		widgetTypeViewerRight = new TreeViewer(parent, SWT.SINGLE);
		fd = new FormData();
		fd.left = new FormAttachment(widgetTypeViewerLeft.getControl(), 0);
		fd.top = new FormAttachment(widgetTypeLabel, 5);
		fd.right = new FormAttachment(100, -10);
		fd.bottom = new FormAttachment(100, -10);
		widgetTypeViewerRight.getControl().setLayoutData(fd);
		widgetTypeViewerRight.setContentProvider(new WidgetTypeContentProvider());
		widgetTypeViewerRight.setLabelProvider(new WidgetTypeLabelProvider());
		widgetTypeViewerRight.setInput(Arrays.copyOfRange(widgetTypes, cut, n));

		widgetTypeViewerLeft.addSelectionChangedListener(new WidgetTypeSelectionListener(widgetTypeViewerRight));
		widgetTypeViewerRight.addSelectionChangedListener(new WidgetTypeSelectionListener(widgetTypeViewerLeft));
	}

	public String getSelectedWidgetType() {
		return selectedWidgetType;
	}

	public String getSelectedProcessVariable() {
		return selectedProcessVariable;
	}

	public String getSelectedBehaviourId() {
		return selectedBehaviourId;
	}

	@Override
	protected void okPressed() {
		if (processVariableText != null) {
			selectedProcessVariable = processVariableText.getText();
		} else {
			selectedProcessVariable = processVariablesCombo.getCCombo().getText();
		}

		if (behaviorViewer != null && behaviorViewer.getSelection() != null) {
			BehaviorDescriptor behavior = (BehaviorDescriptor) ((IStructuredSelection) behaviorViewer.getSelection()).getFirstElement();
			selectedBehaviourId = behavior != null ? behavior.getBehaviorId() : null;
		}

		if (widgetTypeViewerLeft != null) {
			selectedWidgetType = resolveWidgetTypeFromSelection(widgetTypeViewerLeft.getSelection());

			if (selectedWidgetType == null) {
				selectedWidgetType = resolveWidgetTypeFromSelection(widgetTypeViewerRight.getSelection());
			}

		}
		super.okPressed();
	}

	private String resolveWidgetTypeFromSelection(ISelection selection) {
		String result = null;

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			if (!structuredSelection.isEmpty()) {
				Object o = structuredSelection.getFirstElement();

				if (o instanceof WidgetTypeInput) {
					result = ((WidgetTypeInput) o).getTypeId();
				}
			}
		}

		return result;
	}

	private final class WidgetTypeContentProvider extends BaseWorkbenchContentProvider {
		@Override
		public Object[] getElements(Object element) {
			return (WidgetTypeInput[]) element;
		}
	}

	private final class WidgetTypeLabelProvider extends LabelProvider {
		@Override
		public String getText(Object o) {
			return ((WidgetTypeInput) o).getName();
		}

		@Override
		public Image getImage(Object o) {
			return ((WidgetTypeInput) o).getIcon();
		}
	}

	private static class WidgetTypeInput implements Comparable<WidgetTypeInput> {
		private String typeId;
		private String name;
		private Image icon;

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setIcon(Image icon) {
			this.icon = icon;
		}

		public String getTypeId() {
			return typeId;
		}

		public Image getIcon() {
			return icon;
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(WidgetTypeInput other) {
			return name.compareTo(other.name);
		}

	}

}