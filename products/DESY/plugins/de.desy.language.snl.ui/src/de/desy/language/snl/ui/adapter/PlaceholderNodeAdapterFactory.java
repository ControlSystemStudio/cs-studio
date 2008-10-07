package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.PlaceholderNode;

/**
 * The specialized {@link IAdapterFactory} for {@link PlaceholderNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class PlaceholderNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof PlaceholderNode) {
			final PlaceholderNode node = (PlaceholderNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<PlaceholderNode>(node) {
					@Override
					public String getImageName(final PlaceholderNode nodeToRender) {
						return "missing_program.gif";
					}

					@Override
					protected String doGetLabel(final PlaceholderNode nodeToRender) {
						return nodeToRender.getSourceIdentifier();
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
		return new Class[] { PlaceholderNode.class };
	}

}
