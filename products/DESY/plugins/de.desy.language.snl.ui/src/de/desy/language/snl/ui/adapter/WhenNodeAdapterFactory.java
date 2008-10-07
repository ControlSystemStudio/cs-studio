package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.WhenNode;

/**
 * The specialized {@link IAdapterFactory} for {@link WhenNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class WhenNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof WhenNode) {
			final WhenNode node = (WhenNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<WhenNode>(node) {
					@Override
					public String getImageName(final WhenNode nodeToRender) {
						return "when.gif";
					}
				};
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { WhenNode.class };
	}

}
