package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.OptionStatementNode;

/**
 * The specialized {@link IAdapterFactory} for {@link OptionStatementNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class OptionNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof OptionStatementNode) {
			final OptionStatementNode node = (OptionStatementNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<OptionStatementNode>(
						node) {
					@Override
					public String getImageName(final OptionStatementNode nodeToRender) {
						return "option.gif";
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
		return new Class[] { OptionStatementNode.class };
	}

}
