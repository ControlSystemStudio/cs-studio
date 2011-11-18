package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractChannelWidgetEditPart extends AbstractWidgetEditPart {
	
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
	protected abstract AbstractChannelWidgetFigure<?> doCreateFigure();
	
	@Override
	public AbstractChannelWidgetFigure<?> getFigure() {
		// TODO Auto-generated method stub
		return (AbstractChannelWidgetFigure<?>) super.getFigure();
	}
}
