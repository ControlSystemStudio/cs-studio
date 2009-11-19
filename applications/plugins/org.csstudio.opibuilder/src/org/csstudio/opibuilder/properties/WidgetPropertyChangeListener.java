/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.eclipse.draw2d.IFigure;

/**
 * The listener on widget property change.
 * 
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen, 
 *
 */
public class WidgetPropertyChangeListener implements PropertyChangeListener {

	private AbstractBaseEditPart editpart;
	
	private List<IWidgetPropertyChangeHandler> handlers;
	
	/**Constructor.
	 * @param editpart backlint to the editpart, which uses this listener.
	 */
	public WidgetPropertyChangeListener(AbstractBaseEditPart editpart) {
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
	
	public void removeAllHandlers(){
		handlers.clear();
	}

}
