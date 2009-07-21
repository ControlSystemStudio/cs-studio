package org.csstudio.opibuilder.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractBaseEditpart;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.eclipse.draw2d.IFigure;

/**
 * The listener on widget property change.
 * @author Xihui Chen
 *
 */
public class WidgetPropertyChangeListener implements PropertyChangeListener {

	private AbstractBaseEditpart editpart;
	
	private List<IWidgetPropertyChangeHandler> handlers;
	
	/**Constructor.
	 * @param editpart backlint to the editpart, which uses this listener.
	 */
	public WidgetPropertyChangeListener(AbstractBaseEditpart editpart) {
		assert editpart != null;
		this.editpart = editpart;
		handlers = new ArrayList<IWidgetPropertyChangeHandler>();
	}
	
	public void propertyChange(final PropertyChangeEvent evt) {
		Runnable task = new Runnable() {			
			public void run() {
				for(IWidgetPropertyChangeHandler h : handlers) {
					IFigure figure = editpart.getFigure();
					boolean repaint = h.handleChange(
							evt.getOldValue(), evt.getNewValue(), figure);
					if(repaint)
						figure.repaint();
				}
			}
		};
		
		UIBundlingThread.getInstance().addRunnable(task);
	}
	
	/**Add handler, which is informed when a property changed.
	 * @param handler
	 */
	public void addHandler(final IWidgetPropertyChangeHandler handler) {
		assert handler != null;
		handlers.add(handler);
	}

}
