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
package org.csstudio.sds.model.properties;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.properties.actions.WidgetAction;

/**
 * A POJO that encapsulates all Data for a {@link ActionDataProperty}.
 * @author Kai Meyer
 *
 */
public final class ActionData {
	
	/**
	 * The type of the action.
	 */
	private List<WidgetAction> _actions;
	
	/**
	 * Constructor.
	 */
	public ActionData() {
		_actions = new ArrayList<WidgetAction>();
	}
	
	/**
	 * Returns the type of the action.
	 * @return The type of the action
	 */
	public List<WidgetAction> getWidgetActions() {
		return _actions;
	}
	
	/**
	 * Adds a {@link WidgetAction} to the {@link ActionData}.
	 * @param action The new {@link WidgetAction} (should not be null)
	 */
	public void addAction(final WidgetAction action) {
		if (action!=null) {
			_actions.add(action);
		}
	}
	
	/**
	 * Removes the given {@link WidgetAction} from this {@link ActionData}. 
	 * @param action The {@link WidgetAction}, which should be removed.
	 */
	public void removeAction(final WidgetAction action) {
		_actions.remove(action);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ActionData) {
			ActionData that = (ActionData) obj;
			if (this.getWidgetActions().size()==that.getWidgetActions().size()) {
				for (int i=0;i<this.getWidgetActions().size();i++) {
					WidgetAction thisAction = this.getWidgetActions().get(i);
					WidgetAction thatAction = that.getWidgetActions().get(i);
					if (!thisAction.equals(thatAction)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (_actions.size()>0) {
			buffer.append(_actions.size());
			buffer.append(" Actions: ");
			buffer.append(_actions.get(0).getActionLabel());
			for (int i=1;i<_actions.size();i++) {
				buffer.append(", ");
				buffer.append(_actions.get(i).getActionLabel());
			}
		} else {
			buffer.append("No Actions defined");
		}
		return buffer.toString();
	}

}
