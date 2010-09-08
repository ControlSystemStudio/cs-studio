/**
 * 
 */
package org.csstudio.utility.channel.actions;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import gov.bnl.channelfinder.model.XmlChannel;

/**
 * @author shroffk
 * 
 */
public class ChannelTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ChannelModel){
			//return ((ChannelModel) parentElement).channelInfo.get("name").toArray();
			return ((ChannelModel) parentElement).getChild().toArray();
		}else if (parentElement instanceof XmlChannel){
			XmlChannel channel = (XmlChannel) parentElement;
			Object[] array = new Object[4];
			array[0] = channel.getName();
			array[1] = channel.getOwner();
			array[2] = channel.getXmlProperties();
			array[3] = channel.getXmlTags();
			return array;
		}else if(parentElement instanceof Collection<?>){
			return ((Collection<?>) parentElement).toArray();
		}
			return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		if (element == null) {
			return null;
		}
		return ((ChannelModel) element).getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ChannelModel) {
			return (((ChannelModel) element).getChild().size() > 0);
		} else if (element instanceof XmlChannel) {
			return true;
		} else if (element instanceof Collection<?>) {
			return !((Collection<?>) element).isEmpty();
		}
		return false;
	}

}
