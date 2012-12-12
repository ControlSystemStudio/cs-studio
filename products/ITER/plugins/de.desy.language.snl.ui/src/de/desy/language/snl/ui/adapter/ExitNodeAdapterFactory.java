package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.ExitNode;

/**
 * The specialized {@link IAdapterFactory} for {@link ExitNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class ExitNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof ExitNode) {
			final ExitNode node = (ExitNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<ExitNode>(node) {
					
					@Override
					protected String doGetLabel(ExitNode node) {
						return "Exit statement";
					}
					
					@Override
					public String getImageName(final ExitNode nodeToRender) {
						return "exit.gif";
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
		return new Class[] { ExitNode.class };
	}

}
