package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.EventFlagNode;

/**
 * The specialized {@link IAdapterFactory} for {@link EventFlagNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class EventFlagNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof EventFlagNode) {
			final EventFlagNode node = (EventFlagNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<EventFlagNode>(node) {
					@Override
					public String getImageName(final EventFlagNode nodeToRender) {
						if (nodeToRender.isSynchronized()) {
							return "eventflag_sync.gif";
						}
						return "eventflag.gif";
					}

					@Override
					protected String doGetLabel(final EventFlagNode nodeToRender) {
						return nodeToRender.getSourceIdentifier() + " (evtflag)";
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
		return new Class[] { EventFlagNode.class };
	}

}
