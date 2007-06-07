package org.csstudio.alarm.treeView.views;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.PendingUpdateAdapter;


/**
 * Provides labels for the alarm tree items.
 */
public class AlarmTreeLabelProvider extends LabelProvider {

	private Map<String, Image> imageCache;
	
	/**
	 * Creates a new alarm tree label provider.
	 */
	public AlarmTreeLabelProvider() {
		imageCache = new HashMap<String, Image>();
	}

	/**
	 * Returns the element's name.
	 * @param element the element.
	 * @return the element's name, or an empty string if the element doesn't
	 * have a name.
	 */
	public String getText(Object element) {
		if (element instanceof IAlarmTreeNode){
			return ((IAlarmTreeNode)element).getName();
		}
		if (element instanceof PendingUpdateAdapter) {
			return ((PendingUpdateAdapter) element).getLabel(element);
		}
		return "";
	}
	
	/**
	 * Returns the character that represents the given alarm severity in the
	 * icon's filename.
	 * @param alarmSeverity the severity.
	 * @return the character that represents the given severity.
	 */
	private char getIconChar(Severity alarmSeverity) {
		if (alarmSeverity == null)
			return 'w';
		
		switch (alarmSeverity.toIntValue()) {
		case 1:
			return 'g';
		case 2:
			return 'b';
		case 4:
			return 'y';
		case 7:
			return 'r';
		default:
			return 'w';
		}
	}
	
	/**
	 * Returns the icon for the given element.
	 * @param element the element.
	 * @return the icon for the element, or {@code null} if there is no icon
	 * for the element.
	 */
	public Image getImage(Object element) {
		if (element instanceof IAlarmTreeNode) {
			IAlarmTreeNode node = (IAlarmTreeNode) element;
			if (node.hasAlarm()) {
				return alarmImageFor(node);
			} else {
				return defaultImageFor(node);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the image for the given node if that node is in an alarm state.
	 * @param node the node.
	 */
	private Image alarmImageFor(IAlarmTreeNode node) {
		Severity severity = node.getAlarmSeverity();
		char iconChar = getIconChar(severity);
		String iconName = "./icons/" + iconChar + iconChar + ".gif";
		return loadImage(iconName);
	}
	
	/**
	 * Returns the image for the given node if there is no alarm for that node.
	 * @param node the node.
	 */
	private Image defaultImageFor(IAlarmTreeNode node) {
		String image = (node instanceof SubtreeNode)
			? ISharedImages.IMG_OBJ_FOLDER : ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(image);
	}
	
	/**
	 * Loads an image. The image is added to a cache kept by this provider and
	 * is disposed of when this provider is disposed of.
	 * @param name the image file name.
	 */
	private Image loadImage(String name) {
		if (imageCache.containsKey(name)) {
			return imageCache.get(name);
		} else {
			Image image = AlarmTreePlugin.getImageDescriptor(name).createImage();
			imageCache.put(name, image);
			return image;
		}
	}
	
	/**
	 * Disposes of the images created by this label provider.
	 */
	@Override
	public void dispose() {
		for (Image image : imageCache.values()) {
			image.dispose();
		}
	}
}
