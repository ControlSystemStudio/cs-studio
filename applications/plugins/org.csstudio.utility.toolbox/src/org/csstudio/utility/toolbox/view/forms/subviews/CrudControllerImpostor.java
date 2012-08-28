package org.csstudio.utility.toolbox.view.forms.subviews;

import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.eclipse.swt.widgets.Widget;

public class CrudControllerImpostor<T,A> implements CrudController<A> {

	private final CrudController<T> crudController;
	
	public CrudControllerImpostor(CrudController<T> crudController) {
		this.crudController = crudController;
	}
	
	@Override
	public void delete() {
		crudController.delete();
	}

	@Override
	public void copy() {
		crudController.copy();
	}

	@Override
	public void create() {
		crudController.create();
	}

	@Override
	public boolean save() {
		return crudController.save();
		
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void markErrors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDirty(boolean value) {
		crudController.setDirty(value);
	}

	@Override
	public boolean isDirty() {
		return crudController.isDirty();
	}

	@Override
	public void setFocusWidget(Widget widget) {
		// TODO Auto-generated method stub
		
	}

}
