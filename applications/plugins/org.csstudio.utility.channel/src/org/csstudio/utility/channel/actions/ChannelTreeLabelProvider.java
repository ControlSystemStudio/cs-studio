/**
 * 
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;
import gov.bnl.channelfinder.api.Tag;

import java.util.Collection;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author shroffk
 * 
 */
public class ChannelTreeLabelProvider extends LabelProvider {
	@SuppressWarnings("unchecked")
	@Override
	public String getText(Object element) {
		if (element instanceof ChannelTreeModel) {
			return "Channels";
		} else if (element instanceof Channel) {
			return ((Channel) element).getName();
		} else if (element instanceof Collection<?>) {
			 if(((Collection) element).toArray().length != 0){
				 if (((Collection) element).toArray()[0] instanceof Property) {
					 return "Properties";
				}else if (((Collection) element).toArray()[0] instanceof Tag) {
					 return "Tags";
				}else {
					return "unknown";
				}
			 }
		} else if (element instanceof Property) {
			return ((Property) element).getName() + " = "
					+ ((Property) element).getValue();
		} else if (element instanceof Tag) {
			return ((Tag) element).getName();
		}
		return super.getText(element);

	}
}
