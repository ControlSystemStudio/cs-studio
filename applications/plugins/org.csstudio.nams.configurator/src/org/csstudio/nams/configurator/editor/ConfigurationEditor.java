package org.csstudio.nams.configurator.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.nams.configurator.editor.stackparts.AbstractStackPart;
import org.csstudio.nams.configurator.editor.stackparts.DefaultStackPart;
import org.csstudio.nams.configurator.editor.stackparts.TopicStackPart;
import org.csstudio.nams.configurator.editor.stackparts.UserStackPart;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
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

/**
 * This Editor will create a working copy of the bean to edit. On save is will
 * transfer all changes to the original bean - in other words: saving means to
 * change the given model bean.
 */
public class ConfigurationEditor extends EditorPart implements
		DirtyFlagProvider {

	public static final String ID = "org.csstudio.nams.configurator.ConfigurationEditor";

	private StackLayout _stackLayout;

	private List<AbstractStackPart<?>> _stackParts;

	private AbstractStackPart<?> _showedStackPart;

	private ConfigurationEditorInput _input;

	private DefaultStackPart _defaultStackPart;

	private ConfigurationBean _originalModel;

	public ConfigurationEditor() {
		_stackParts = new ArrayList<AbstractStackPart<?>>();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		_showedStackPart.save(_originalModel);
	}

	@Override
	public void doSaveAs() {
		throw new RuntimeException("This method (doSaveAs) is not supported");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		_input = (ConfigurationEditorInput) input;
		_originalModel = _input.getBean();
		if (_defaultStackPart != null) {
			this.showCorrespondingStackPart(_originalModel);
		}
	}

	private void showCorrespondingStackPart(ConfigurationBean input) {
		AbstractStackPart stackPart = this.getStackPartfor(input.getClass());
		Control mainControl = stackPart.getMainControl();
		_showedStackPart = stackPart;
		_stackLayout.topControl = mainControl;
	}

	private AbstractStackPart<?> getStackPartfor(
			Class<? extends ConfigurationBean> tObjectClass) {
		for (AbstractStackPart part : _stackParts) {
			if (tObjectClass.equals(part.getAssociatedBean())) {
				return part;
			}
		}
		return _defaultStackPart;
	}

	@Override
	public boolean isDirty() {
		return _showedStackPart.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite scrollPane = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL);
		scrollPane.setExpandHorizontal(true);
		scrollPane.setExpandVertical(true);

		Composite main = new Composite(scrollPane, SWT.NONE);
		_stackLayout = new StackLayout();
		main.setLayout(_stackLayout);

		this.createAndAddEditorStackParts(main);

		scrollPane.setContent(main);
		scrollPane.setMinSize(scrollPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		this.showCorrespondingStackPart(_input.getBean());
	}

	private void createAndAddEditorStackParts(Composite parent) {
		_defaultStackPart = new DefaultStackPart(this, parent);
		_stackParts.add(new UserStackPart(this, parent));
		_stackParts.add(new TopicStackPart(this, parent));
	}

	@Override
	public void setFocus() {

	}

	public void fireDirtyFlagChanged() {
		this.firePropertyChange(EditorPart.PROP_DIRTY);
	}

}
