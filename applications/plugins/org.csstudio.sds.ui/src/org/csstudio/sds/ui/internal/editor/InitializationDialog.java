package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.eventhandling.BehaviorDescriptor;
import org.csstudio.sds.internal.eventhandling.IBehaviorService;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Kai Meyer (C1 WPS)
 *
 */
public class InitializationDialog extends Dialog {

	private static final String NONE_BEHAVIOR = "none";
	private final String _widgetTypeId;
	private ComboViewer _behaviorCombo;
	private Text _pvTextField;
	private Button _defaultButton;
	
	private String _behaviorId;
	private String _pvName;

	protected InitializationDialog(Shell parentShell, String widgetTypeId, String fullPvName) {
		super(parentShell);
        _pvName = fullPvName;
		assert widgetTypeId != null : "widgetTypeId != null";
		assert widgetTypeId.trim().length() > 0 : "widgetTypeId.trim().length() > 0";
		
		_widgetTypeId = widgetTypeId;
		this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
				| SWT.BORDER | SWT.RESIZE);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Initialization");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("PV");
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).create());
		_pvTextField = new Text(composite, SWT.BORDER);
		_pvTextField.setLayoutData(GridDataFactory.fillDefaults().create());
		_pvTextField.setText(_pvName);
		
		label = new Label(composite, SWT.NONE);
		label.setText("Behavior");
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		
		_behaviorCombo = new ComboViewer(composite);
		_behaviorCombo.setContentProvider(new ArrayContentProvider());
		_behaviorCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				BehaviorDescriptor d = (BehaviorDescriptor) element;
				return d.getDescription();
			}
		});
		_behaviorCombo.getControl().setLayoutData(GridDataFactory.fillDefaults().create());
		
		List<BehaviorDescriptor> input = createInput();
		_behaviorCombo.setInput(input);
		
		_defaultButton = new Button(composite, SWT.CHECK);
		_defaultButton.setText("use selected behavior as default next time");
		_defaultButton.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
		
		String defaultBehavior = SdsUiPlugin.getCorePreferenceStore().getString(PreferenceConstants.PROP_DEFAULT_BEHAVIOR_PREFIX + _widgetTypeId);
		
		if (defaultBehavior == null || defaultBehavior.trim().length() == 0) {
			defaultBehavior = NONE_BEHAVIOR;
		}
		for (BehaviorDescriptor descriptor : input) {
			if (descriptor.getBehaviorId().equals(defaultBehavior)) {
				_behaviorCombo.setSelection(new StructuredSelection(descriptor), true);
				if (!defaultBehavior.equals(NONE_BEHAVIOR)) {
					_defaultButton.setSelection(true);
				}
				break;
			}
		}
		
		return composite;
	}

	@SuppressWarnings("unchecked")
	private List<BehaviorDescriptor> createInput() {
		List<BehaviorDescriptor> result = new ArrayList<BehaviorDescriptor>();

		// .. add empty
		BehaviorDescriptor noneDescriptor = new BehaviorDescriptor(NONE_BEHAVIOR, "*", "None", Collections.EMPTY_SET, null);
		result.add(noneDescriptor);

		// .. add real behaviors
		IBehaviorService service = SdsPlugin.getDefault().getBehaviourService();
		result.addAll(service.getBehaviors(_widgetTypeId));
		return result;
	}
	
	public String getBehaviorId() {
		return _behaviorId;
	}

	public String getPvName() {
		return _pvName;
	}

	@Override
	protected void okPressed() {
		_pvName = _pvTextField.getText();
		IStructuredSelection selection = (IStructuredSelection) _behaviorCombo.getSelection();
		_behaviorId = ((BehaviorDescriptor) selection.getFirstElement()).getBehaviorId();
		if (_defaultButton.getSelection()) {
			SdsUiPlugin.getCorePreferenceStore().setValue(PreferenceConstants.PROP_DEFAULT_BEHAVIOR_PREFIX + _widgetTypeId, _behaviorId);
		}
		super.okPressed();
	}

}
