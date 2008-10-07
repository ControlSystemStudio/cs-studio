package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.MonitorStatementNode;

/**
 * The specialized {@link IAdapterFactory} for {@link MonitorStatementNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class MonitorNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof MonitorStatementNode) {
			final MonitorStatementNode node = (MonitorStatementNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<MonitorStatementNode>(
						node) {
					@Override
					public String getImageName(final MonitorStatementNode nodeToRender) {
						return "monitor.gif";
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
		return new Class[] { MonitorStatementNode.class };
	}

}
