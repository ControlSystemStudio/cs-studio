package org.csstudio.utility.toolbox.framework.controller;

import org.csstudio.utility.toolbox.framework.builder.DirtyFlag;
import org.eclipse.swt.widgets.Widget;


public interface CrudController<T>  extends DirtyFlag {

	void delete();

	void copy();

	void create();

	boolean save();

	boolean isValid();
	
	void markErrors();
	
	void setDirty(boolean value);
	
	boolean isDirty();
	
	void setFocusWidget(Widget widget);

	
}
