package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
	 * An Action, which encapsulates a {@link AbstractWidgetAction}.
	 * 
	 * @author XihuiCehn
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
		 *            The encapsulated {@link AbstractWidgetActionModel}
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