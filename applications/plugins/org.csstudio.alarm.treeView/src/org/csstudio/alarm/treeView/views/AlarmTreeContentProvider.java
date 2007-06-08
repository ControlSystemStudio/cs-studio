package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provides the content for the alarm tree view.
 */
public class AlarmTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	/**
	 * Creates a new alarm tree content provider.
	 */
	public AlarmTreeContentProvider() {
		super();
	}
	
	/**
	 * Returns the root elements to display in the viewer when its input is
	 * set to the given element. These elements will be presented as the root
	 * elements in the tree view.
	 * 
	 * @param inputElement the input element.
	 * @return the array of elements to display in the viewer.
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SubtreeNode) {
			return getChildren(inputElement);
		} else if (inputElement instanceof Object[]) {
			return ((Object[]) inputElement);
		} else {
			throw new IllegalArgumentException(
					"Invalid input element: " + inputElement);
		}
	}
	
	/**
	 * Returns the parent of the object passed to this method.
	 * @param child the child element.
	 * @return the child element's parent, or {@code null} if it has none or if
	 * the parent element cannot be computed.
	 */
	public Object getParent(Object child) {
		if (child instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode)child).getParent();
		}
		return null;
	}
	
	/**
	 * Returns the children of the object passed to this method.
	 * @param parent the input element.
	 * @return the children of the input element.
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof SubtreeNode) {
			return ((SubtreeNode) parent).getChildren();
		} else {
			return new Object[0];
		}
	}
	
	/**
	 * Returns whether the given element has children.
	 * @param parent the element
	 * @return {@code true} if the given element has children, {@code false}
	 * otherwise.
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof SubtreeNode) {
			return ((SubtreeNode) parent).hasChildren();
		}
		return false;
	}
	
	/**
	 * Disposes of this content provider.
	 */
	public void dispose() {
		// nothing to do
	}

	/**
	 * Notifies this content provider that the viewer's input has switched.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do
	}

}