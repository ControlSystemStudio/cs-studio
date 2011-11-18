package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

public abstract class AbstractChannelWidgetEditPart<Figure extends AbstractChannelWidgetFigure<?>,
    Model extends AbstractChannelWidgetModel> extends AbstractWidgetEditPart {
	
	private void registerMouseListener(Control control) {
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				getViewer().select(AbstractChannelWidgetEditPart.this);
			}
		});
		
		if (control instanceof Composite) {
			for (Control child : ((Composite) control).getChildren()) {
				registerMouseListener(child);
			}
		}
	}
	
	protected void registerPopup(final Control control) {
		registerMouseListener(control);
		Menu menu = getViewer().getContextMenu().createContextMenu(getViewer().getControl());
		control.setMenu(menu);
	}
	
	@Override
	protected abstract Figure doCreateFigure();
	
	@Override
	public Figure getFigure() {
		@SuppressWarnings("unchecked")
		Figure figure = (Figure) super.getFigure();
		return figure;
	}
	
	@Override
	public Model getWidgetModel() {
		@SuppressWarnings("unchecked")
		Model widgetModel = (Model) super.getWidgetModel();
		return widgetModel;
	}
	
	@Override
	public void deactivate() {
		getFigure().getSWTWidget().dispose();		
		super.deactivate();
	}

}
