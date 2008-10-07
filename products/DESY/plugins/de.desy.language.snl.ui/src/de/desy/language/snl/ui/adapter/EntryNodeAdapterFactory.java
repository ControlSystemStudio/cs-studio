package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.EntryNode;

/**
 * The specialized {@link IAdapterFactory} for {@link EntryNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class EntryNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof EntryNode) {
			final EntryNode node = (EntryNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<EntryNode>(node) {
					
					@Override
					protected String doGetLabel(EntryNode node) {
						return "Entry statement";
					}
					
					@Override
					public String getImageName(final EntryNode nodeToRender) {
						return "entry.gif";
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
		return new Class[] { EntryNode.class };
	}

}
