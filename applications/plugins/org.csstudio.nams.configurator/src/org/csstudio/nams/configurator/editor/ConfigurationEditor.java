package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ams.configurationStoreService.util.TObject;
import org.csstudio.nams.configurator.editor.stackparts.AbstractStackPart;
import org.csstudio.nams.configurator.editor.stackparts.DefaultStackPart;
import org.csstudio.nams.configurator.editor.stackparts.TopicStackPart;
import org.csstudio.nams.configurator.editor.stackparts.UserStackPart;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class ConfigurationEditor extends EditorPart {
	
	public static final String ID = "org.csstudio.nams.configurator.ConfigurationEditor";

	private StackLayout _stackLayout;
	
	private List<AbstractStackPart> _stackParts;

	private ConfigurationEditorInput _input;

	private DefaultStackPart _defaultStackPart;

	public ConfigurationEditor() {
		_stackParts = new ArrayList<AbstractStackPart>();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		_input = (ConfigurationEditorInput) input;
		if (_defaultStackPart!=null) {
			this.showCorrespondingStackPart(_input.getInput());
		}
	}
	
	private void showCorrespondingStackPart(TObject input) {
		AbstractStackPart stackPart = this.getStackPartfor(input.getClass());
		Control mainControl = stackPart.getMainControl();
		_stackLayout.topControl = mainControl;
	}

	private AbstractStackPart getStackPartfor(Class<? extends TObject> tObjectClass) {
		for (AbstractStackPart part : _stackParts) {
			if (tObjectClass.equals(part.getAssociatedTObject())) {
				return part;
			}
		}
		return _defaultStackPart;
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
	public void createPartControl(Composite parent) {
		ScrolledComposite scrollPane = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollPane.setExpandHorizontal(true);
		scrollPane.setExpandVertical(true);
		
		Composite main = new Composite(scrollPane, SWT.NONE);
		_stackLayout = new StackLayout();
		main.setLayout(_stackLayout);
		
		this.createAndAddEditorStackParts(main);
		
		scrollPane.setContent(main);
		scrollPane.setMinSize(scrollPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		this.showCorrespondingStackPart(_input.getInput());
	}
	
	private void createAndAddEditorStackParts(Composite parent) {
		_defaultStackPart = new DefaultStackPart(parent);
		_stackParts.add(new UserStackPart(parent));
		_stackParts.add(new TopicStackPart(parent));
	}

	@Override
	public void setFocus() {

	}

}
