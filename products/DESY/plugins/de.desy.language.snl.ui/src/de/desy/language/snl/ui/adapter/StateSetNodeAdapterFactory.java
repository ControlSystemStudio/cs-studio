package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.StateSetNode;

/**
 * The specialized {@link IAdapterFactory} for {@link StateSetNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class StateSetNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof StateSetNode) {
			final StateSetNode node = (StateSetNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<StateSetNode>(node) {
					@Override
					public String getImageName(final StateSetNode nodeToRender) {
						return "stateset.gif";
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
		return new Class[] { StateSetNode.class };
	}

}
