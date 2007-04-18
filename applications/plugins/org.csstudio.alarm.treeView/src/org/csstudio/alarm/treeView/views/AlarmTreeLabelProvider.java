package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.LdaptreePlugin;
import org.csstudio.alarm.treeView.images.LdapImageCache;
import org.csstudio.alarm.treeView.views.models.AlarmConnection;
import org.csstudio.alarm.treeView.views.models.ContextTreeObject;
import org.csstudio.alarm.treeView.views.models.ContextTreeParent;
import org.csstudio.alarm.treeView.views.models.ISimpleTreeObject;
import org.csstudio.alarm.treeView.views.models.ISimpleTreeParent;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * Provides labels for the alarm tree items.
 */
public class AlarmTreeLabelProvider extends LabelProvider {

	/**
	 * Creates a new alarm tree label provider.
	 */
	public AlarmTreeLabelProvider() {
	}

	/**
	 * Returns the element's name.
	 * @param element the element.
	 * @return the element's name, or an empty string if the element doesn't
	 * have a name.
	 */
	public String getText(Object element) {
		if (element instanceof ISimpleTreeObject){
			return ((ISimpleTreeObject)element).getName();
		}
		return "";
	}
	
	/**
	 * Returns the character that represents the given alarm severity in the
	 * icon's filename.
	 * @param alarmSeverity the severity.
	 * @return the character that represents the given severity.
	 */
	private char getIconChar(int alarmSeverity) {
		switch (alarmSeverity) {
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
		try{
			LdapImageCache lic = LdaptreePlugin.getDefaultImageCache();
			ISimpleTreeParent obj = (ISimpleTreeParent)element;
			String imageKey = ISharedImages.IMG_OBJ_FILE;
			if (obj.hasChildren())
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			if (obj instanceof ContextTreeObject){
				if (((ContextTreeObject)obj).getTotalAlarmCount()>0){
					int mxalarm = ((ContextTreeObject)obj).getMaxAlarm();
					int maxUalarm = ((ContextTreeObject)obj).getMaxUnacknowledgedAlarm();
					String iconn = new String(new char[]{getIconChar(maxUalarm),getIconChar(mxalarm)});
					if ((iconn.equals("ww"))) {imageKey = ISharedImages.IMG_OBJ_ELEMENT;}
					else return lic.getImage(LdaptreePlugin.getImageDescriptor("./icons/"+iconn+".gif"));
				}
				else imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			}
			if (obj instanceof LdapConnection)
				imageKey = ISharedImages.IMG_DEF_VIEW;
			if  (obj instanceof AlarmConnection)
				return LdaptreePlugin.getDefaultImageCache().getImage(LdaptreePlugin.getImageDescriptor("./icons/alarm.gif"));
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		catch (Exception e){
			e.printStackTrace();			
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);			
		}
	}	
}
