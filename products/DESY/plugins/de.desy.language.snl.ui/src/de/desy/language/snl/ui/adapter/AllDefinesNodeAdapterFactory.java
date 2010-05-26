package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.AllDefineStatementsNode;

/**
 * The specialized {@link IAdapterFactory} for {@link AllDefineStatementsNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class AllDefinesNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof AllDefineStatementsNode) {
			final AllDefineStatementsNode varNode = (AllDefineStatementsNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<AllDefineStatementsNode>(varNode) {
					@Override
					protected String getImageName(final AllDefineStatementsNode node) {
						return "define.gif";
					}

					@Override
					protected String doGetLabel(final AllDefineStatementsNode node) {
						return node.getSourceIdentifier();
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
		return new Class[] { AllDefineStatementsNode.class };
	}

}
