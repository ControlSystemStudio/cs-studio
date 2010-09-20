/**
 * 
 */
package org.csstudio.utility.channel.actions;

import java.util.Collection;

import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlProperty;
import gov.bnl.channelfinder.model.XmlTag;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author shroffk
 * 
 */
public class ChannelTreeLabelProvider extends LabelProvider {
	@SuppressWarnings("unchecked")
	@Override
	public String getText(Object element) {
		if (element instanceof ChannelModel) {
			return "Channels";
		} else if (element instanceof XmlChannel) {
			return ((XmlChannel) element).getName();
		} else if (element instanceof Collection<?>) {
			 if(((Collection) element).toArray().length != 0){
				 if (((Collection) element).toArray()[0] instanceof XmlProperty) {
					 return "Properties";
				}else if (((Collection) element).toArray()[0] instanceof XmlTag) {
					 return "Tags";
				}else {
					return "unknown";
				}
			 }
		} else if (element instanceof XmlProperty) {
			return ((XmlProperty) element).getName() + " = "
					+ ((XmlProperty) element).getValue();
		} else if (element instanceof XmlTag) {
			return ((XmlTag) element).getName();
		}
		return super.getText(element);

	}
}
