package org.csstudio.nams.configurator.editor.stackparts;

import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;
import org.csstudio.nams.configurator.modelmapping.IConfigurationModel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class FilterStackPart extends AbstractStackPart<FilterBean> {

	private Composite main;
	
	public FilterStackPart(DirtyFlagProvider flagProvider, int numColumns) {
		super(flagProvider, FilterBean.class, 2);
//TODO create layout
	}

	@Override
	public Control getMainControl() {
		return main;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInput(IConfigurationBean input, IConfigurationModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyChangedListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initDataBinding() {
		// TODO Auto-generated method stub
		
	}

}
