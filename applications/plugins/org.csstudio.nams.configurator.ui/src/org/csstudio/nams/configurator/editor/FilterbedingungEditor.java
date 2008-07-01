package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

public class FilterbedingungEditor extends AbstractEditor<FilterbedingungBean> {

	private Text _nameTextEntry;
	private Combo _groupComboEntry;
	private Text _defaultMessageTextEntry;
	private Composite filterSpecificComposite;
	private Combo _filterTypeEntry;
	
	private static final String EDITOR_ID = "org.csstudio.nams.configurator.editor.FilterbedingungEditor";

	public static String getId() {
		return EDITOR_ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");
		
		_filterTypeEntry = this.createComboEntry(main, "Filtertype: ", true);
		initDataBinding();
	}

	@Override
	protected void doInit(IEditorSite site, IEditorInput input) {
	}

	@Override
	protected int getNumColumns() {
		return 2;
	}

	@Override
	protected void initDataBinding() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		_nameTextEntry.setFocus();
		
	}

}
