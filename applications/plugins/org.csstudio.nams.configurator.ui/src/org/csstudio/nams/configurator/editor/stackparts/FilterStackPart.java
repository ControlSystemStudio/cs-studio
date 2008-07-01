package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
@Deprecated
public class FilterStackPart extends AbstractStackPart<FilterBean> {

	private Text _nameTextEntry;
	private Combo _groupComboEntry;
	private Text _defaultMessageTextEntry;
	private Composite filterSpecificComposite;
	private Combo _filterTypeEntry;

	public FilterStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, FilterBean.class, 2);
		this.createPartControl(parent);
	}

	private void createPartControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_COLUMNS, false));
		_nameTextEntry = this.createTextEntry(main, "Name:", true);
		_groupComboEntry = this.createComboEntry(main, "Group:", true);
		this.addSeparator(main);
		_defaultMessageTextEntry = this.createDescriptionTextEntry(main,
				"Description:");
		
		_filterTypeEntry = this.createComboEntry(main, "Filtertype: ", true);
	}

	@Override
	protected void initDataBinding() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}


}
