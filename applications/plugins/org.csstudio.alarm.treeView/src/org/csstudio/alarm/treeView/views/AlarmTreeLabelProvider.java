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
 package org.csstudio.alarm.treeView.views;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
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

	/**
	 * Cache for Image objects.
	 */
	private Map<String, Image> _imageCache;
	
	/**
	 * Creates a new alarm tree label provider.
	 */
	public AlarmTreeLabelProvider() {
		_imageCache = new HashMap<String, Image>();
	}

	/**
	 * Returns the element's name.
	 * @param element the element.
	 * @return the element's name, or an empty string if the element doesn't
	 * have a name.
	 */
	public final String getText(final Object element) {
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
	private char getIconChar(final Severity alarmSeverity) {
		switch (alarmSeverity) {
		case NO_ALARM:
			return 'g';
		case INVALID:
			return 'b';
		case MINOR:
			return 'y';
		case MAJOR:
			return 'r';
		default:
			// should never get here
		    return 'w';
		}
	}
	
	/**
	 * Returns the icon for the given element.
	 * @param element the element.
	 * @return the icon for the element, or {@code null} if there is no icon
	 * for the element.
	 */
	public final Image getImage(final Object element) {
		if (element instanceof ProcessVariableNode) {
			ProcessVariableNode node = (ProcessVariableNode) element;
			return node.hasAlarm() ? alarmImageFor(node) : defaultNodeImage();
		} else if (element instanceof SubtreeNode) {
			return alarmImageFor((SubtreeNode) element);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the image for the given node if that node is in an alarm state.
	 * @param node the node.
	 * @return the image.
	 */
	private Image alarmImageFor(final IAlarmTreeNode node) {
		Severity activeAlarmSeverity = node.getAlarmSeverity();
		Severity unacknowledgedAlarmSeverity = node.getUnacknowledgedAlarmSeverity();
		char rightIconChar = getIconChar(activeAlarmSeverity);
		char leftIconChar = getIconChar(unacknowledgedAlarmSeverity);
		String iconName = "./icons/" + leftIconChar + rightIconChar + ".gif";
		return loadImage(iconName);
	}
	
	/**
	 * Returns the image for a leaf node that does not have any alarms set.
	 * @return the image.
	 */
	private Image defaultNodeImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}
	
	/**
	 * Loads an image. The image is added to a cache kept by this provider and
	 * is disposed of when this provider is disposed of.
	 * @param name the image file name.
	 * @return the image.
	 */
	private Image loadImage(final String name) {
		if (_imageCache.containsKey(name)) {
			return _imageCache.get(name);
		} else {
			Image image = AlarmTreePlugin.getImageDescriptor(name).createImage();
			_imageCache.put(name, image);
			return image;
		}
	}
	
	/**
	 * Disposes of the images created by this label provider.
	 */
	@Override
	public final void dispose() {
		for (Image image : _imageCache.values()) {
			image.dispose();
		}
	}
}
