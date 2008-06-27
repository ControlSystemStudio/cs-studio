package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;

public class FilterStackPart extends AbstractStackPart<FilterBean> {

	public FilterStackPart(DirtyFlagProvider flagProvider, int numColumns) {
		super(flagProvider, FilterBean.class, 2);
//TODO create layout
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
