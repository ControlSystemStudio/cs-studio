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

package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
	 * An Action, which encapsulates a {@link AbstractWidgetAction}.
	 * 
	 * @author Xihui Chen, Helge Rickens, Kai Meyer(part of the code is copied from SDS).
	 * 
	 */
	public final class WidgetActionMenuAction extends Action {
		/**
		 * The {@link AbstractWidgetActionModel}.
		 */
		private AbstractWidgetAction _widgetAction;

		/**
		 * Constructor.
		 * 
		 * @param widgetAction
		 *            The encapsulated {@link AbstractWidgetAction}
		 */
		public WidgetActionMenuAction(final AbstractWidgetAction widgetAction) {
			_widgetAction = widgetAction;
			this.setText(_widgetAction.getDescription());
			Object adapter = widgetAction.getAdapter(IWorkbenchAdapter.class);
			if (adapter != null && adapter instanceof IWorkbenchAdapter) {
				this.setImageDescriptor(((IWorkbenchAdapter)adapter)
						.getImageDescriptor(widgetAction));
			}
			setEnabled(widgetAction.isEnabled());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			_widgetAction.run();

		}
	}