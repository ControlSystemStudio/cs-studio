package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractChannelWidgetEditPart<Figure extends AbstractChannelWidgetFigure<?>,
    Model extends AbstractChannelWidgetModel> extends AbstractWidgetEditPart {
	
	protected void registerPopup(Control control) {
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				getViewer().select(AbstractChannelWidgetEditPart.this);
			}
		});
		control.setMenu(getViewer().getContextMenu().createContextMenu(getViewer().getControl()));
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

}
