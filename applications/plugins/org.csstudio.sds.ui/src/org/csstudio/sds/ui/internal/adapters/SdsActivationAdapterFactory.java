package org.csstudio.sds.ui.internal.adapters;

import org.csstudio.platform.security.IActivationAdapter;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.swt.graphics.RGB;

/**
 * Adapter factory for display widget models to switch their visibility.
 * @author Kai Meyer
 *
 */
public final class SdsActivationAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$

		if (adapterType == IActivationAdapter.class) {
			//return new SdsWidgetVisibilityAdapter();
			//return new SdsWidgetBackgroundAdapter();
			return new SdsWidgetEnabledAdapter();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] getAdapterList() {
		return new Class[] { IActivationAdapter.class };
	}
	
	/**
	 * An IWidgetAdapter for AbstractWidgetModels, which sets the visibility.
	 * @author Kai Meyer
	 */
	private final class SdsWidgetVisibilityAdapter implements IActivationAdapter {

		/**
		 * {@inheritDoc}
		 */
		public void activate(final Object o, final boolean activate) {
			assert o instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$
			AbstractWidgetModel model = (AbstractWidgetModel) o;
			model.setVisible(activate);
		}
		
	}
	
	/**
	 * An IWidgetAdapter for AbstractWidgetModels, which sets the visibility.
	 * @author Kai Meyer
	 */
	private final class SdsWidgetBackgroundAdapter implements IActivationAdapter {

		/**
		 * {@inheritDoc}
		 */
		public void activate(final Object o, final boolean activate) {
			assert o instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$
			AbstractWidgetModel model = (AbstractWidgetModel) o;
			RGB color;
			if (activate) {
				color = new RGB(180,180,180);
			} else {
				color = new RGB(0,0,0);
			}
			model.setBackgroundColor(color);
		}
		
	}
	
	/**
	 * An IWidgetAdapter for AbstractWidgetModels, which sets the visibility.
	 * @author Kai Meyer
	 */
	private final class SdsWidgetEnabledAdapter implements IActivationAdapter {

		/**
		 * {@inheritDoc}
		 */
		public void activate(final Object o, final boolean activate) {
			assert o instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$
			AbstractWidgetModel model = (AbstractWidgetModel) o;
			model.setEnabled(activate);
		}
		
	}

}
