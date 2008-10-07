package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.SyncStatementNode;

/**
 * The specialized {@link IAdapterFactory} for {@link SyncStatementNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class SyncNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof SyncStatementNode) {
			final SyncStatementNode node = (SyncStatementNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<SyncStatementNode>(node) {
					@Override
					public String getImageName(final SyncStatementNode nodeToRender) {
						return "sync.gif";
					}

					@Override
					protected String doGetLabel(final SyncStatementNode nodeToRender) {
						return nodeToRender.getSourceIdentifier() + " => "
								+ nodeToRender.getContent();
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
		return new Class[] { SyncStatementNode.class };
	}

}
