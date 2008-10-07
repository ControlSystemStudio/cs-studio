package de.desy.language.snl.ui.adapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.snl.parser.nodes.AbstractSNLNode;
import de.desy.language.snl.ui.SNLUiActivator;

/**
 * An abstract (re-)implementation of an {@link WorkbenchAdapter}. This class
 * can be used to adapt a specific subclass of an {@link AbstractSNLNode}.
 * 
 * @param <N>
 *            The Type of adapted {@link AbstractSNLNode}.
 * @author C1 WPS / km, mz
 * 
 */
public abstract class AbstractSNLWorkbenchAdapter<N extends AbstractSNLNode>
		extends WorkbenchAdapter {

	/**
	 * The adapted node (a subclass of {@link AbstractSNLNode}).
	 */
	private final N _node;

	/**
	 * Constructor.
	 * @param node The adapted node
	 */
	public AbstractSNLWorkbenchAdapter(final N node) {
		this._node = node;
	}

	/**
	 * Returns the node of this adapter.
	 * 
	 * @return the node
	 */
	protected final Node getNode() {
		return this._node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object[] getChildren(final Object o) {
		return this._node.getChildrenNodesAsArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ImageDescriptor getImageDescriptor(final Object object) {
		final String fullPath = this.getFullPath(this._node);
		if (fullPath == null) {
			return null;
		}
		return SNLUiActivator.getImageDescriptor(fullPath);
	}

	/**
	 * Returns the path to the {@link ImageDescriptor} for the node. The path
	 * starts with <i>/icons/nodes/</i>
	 * 
	 * @param node
	 *            The node
	 * @return The path to the {@link ImageDescriptor} for the node
	 */
	private String getFullPath(final N node) {
		final String imageName = this.getImageName(node);
		if (imageName == null) {
			return null;
		}
		return "/icons/nodes/" + imageName;
	}

	/**
	 * Return the name of the image for the node. The image has to be in the
	 * folder <i>/icons/nodes/</i>.
	 * 
	 * @param nodeToRender
	 *            The node
	 * @return The name of the image
	 */
	protected abstract String getImageName(N nodeToRender);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getLabel(final Object o) {
		return this.clearDescription(this.doGetLabel(this._node));
	}

	/**
	 * Returns a textual representation of the given node. Subclasses may
	 * override
	 * 
	 * @param node
	 *            The node
	 * @return The textual representation
	 */
	protected String doGetLabel(final N node) {
		return node.humanReadableRepresentation();
	}

	/**
	 * Replaces all line breaks and tabs from the given description with a ' '.
	 * 
	 * @param description
	 *            The description
	 * @return The cleared description
	 */
	private String clearDescription(final String description) {
		return description.replace("\n", " ").replace("\r", " ").replace("\t",
				" ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(final Object o) {
		return null;
	}

}
