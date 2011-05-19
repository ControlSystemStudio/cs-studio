/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.alarm.treeview.views;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provides the content for the alarm tree view.
 */
public class AlarmTreeContentProvider implements ITreeContentProvider {

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
	@Override
    @Nonnull
	public final Object[] getElements(@Nullable final Object inputElement) {
		if (inputElement instanceof IAlarmSubtreeNode) {
			return getChildren(inputElement);
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
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
	@Override
    @CheckForNull
	public final Object getParent(@Nullable final Object child) {
		if (child instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode) child).getParent();
		}
		return null;
	}

	/**
	 * Returns the children of the object passed to this method.
	 * @param parent the input element.
	 * @return the children of the input element.
	 */
	@Override
    @Nonnull
	public final Object[] getChildren(@Nonnull final Object parent) {
		if (parent instanceof IAlarmSubtreeNode) {
			return ((IAlarmSubtreeNode) parent).getChildren().toArray();
		}
		return new Object[0];
	}

	/**
	 * Returns whether the given element has children.
	 * @param parent the element
	 * @return {@code true} if the given element has children, {@code false}
	 * otherwise.
	 */
	@Override
    public final boolean hasChildren(@Nullable final Object parent) {
		if (parent instanceof IAlarmTreeNode) {
			return ((IAlarmTreeNode) parent).hasChildren();
		}
		return false;
	}

	/**
	 * Disposes of this content provider.
	 */
	@Override
    public void dispose() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void inputChanged(@CheckForNull final Viewer viewer,
	                         @CheckForNull final Object oldInput,
	                         @CheckForNull final Object newInput) {
		// nothing to do
	}

}
