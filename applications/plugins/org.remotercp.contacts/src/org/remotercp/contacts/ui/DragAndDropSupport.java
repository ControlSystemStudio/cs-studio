package org.remotercp.contacts.ui;

import org.eclipse.ecf.presence.roster.IRosterItem;

/**
 * This support has been introduces in order to enable drag and drop support for
 * IRosterItems. As IRosterItems are not implementing Serializable there is no
 * possibility to create an own DND Transfer without writing a wrapper. From my
 * point of view this is somehow to brake a butterfly on a wheel.
 * 
 * @author Eugen Reiswich
 * 
 */
public class DragAndDropSupport {

	private static DragAndDropSupport instance;
	private IRosterItem item;

	private DragAndDropSupport() {
		// singleton
	}

	public static DragAndDropSupport getInstance() {
		return instance == null ? instance = new DragAndDropSupport()
				: instance;
	}

	public void setDragItem(IRosterItem item) {
		this.item = item;

	}

	public IRosterItem getDragItem() {
		return this.item;
	}

}
