package org.csstudio.opibuilder.widgets.editparts;

import org.eclipse.draw2d.IFigure;

public interface IButtonEditPartDelegate {

	public abstract IFigure doCreateFigure();

	public abstract void hookMouseClickAction();

	public abstract void deactivate();

	public abstract void registerPropertyChangeHandlers();

	public abstract void setValue(Object value);

	public abstract Object getValue();
	
	public abstract boolean isSelected();

}