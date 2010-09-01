/**
 * 
 */
package org.csstudio.channelfinder.views;

import java.text.Collator;
import java.util.Iterator;
import java.util.regex.Pattern;

import gov.bnl.channelfinder.model.XmlChannel;
import gov.bnl.channelfinder.model.XmlProperty;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author shroffk
 *
 */
public class PropertySorter extends AbstractColumnViewerSorter {

	private String propertyName;
	
	public PropertySorter(String propertyName, ColumnViewer viewer, TableViewerColumn column) {
		super(viewer, column);
		this.propertyName = propertyName;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.channelfinder.views.AbstractColumnViewerSorter#doCompare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected int doCompare(Viewer viewer, Object e1, Object e2) {
		// TODO Auto-generated method stub
		return compare((XmlChannel)e1, (XmlChannel)e2);
	}

	public int compare(XmlChannel d1, XmlChannel d2) {
		XmlProperty prop1 = getProperty(d1, propertyName);
		XmlProperty prop2 = getProperty(d2, propertyName);
		if ((prop1 == null) && (prop2 == null))
			return 0;
		else if (prop1 == null)
			return -1;
		else if (prop2 == null)
			return +1;
		else
			return smartCompare(prop1.getValue(),
					prop2.getValue());
	}

	private int smartCompare(String value1, String value2) {
		Pattern p2 = Pattern.compile( "((-|\\+)?[0-9]+(\\.[0-9]+)?)+" );
		if(p2.matcher(value1).matches() && p2.matcher(value2).matches()){
			return Double.valueOf(value1).compareTo(Double.valueOf(value2));
		}			
		else if(p2.matcher(value1).matches() && !p2.matcher(value2).matches()){
			return +1;
		}
		else if(!p2.matcher(value1).matches() && p2.matcher(value2).matches()){
			return -1;
		}
		else if(!p2.matcher(value1).matches() && !p2.matcher(value2).matches()){
			return Collator.getInstance().compare(value1, value2);
		}			
		return 0;
	}

	/**
	 * 
	 * @param channel
	 * @param PropertyName
	 * @return the XmlProperty with the matching name else null;
	 */
	private XmlProperty getProperty(XmlChannel channel, String PropertyName) {
		Iterator<XmlProperty> itr = channel.getXmlProperties().iterator();
		while (itr.hasNext()) {
			XmlProperty item = itr.next();
			if (item.getName().equals(propertyName)) {
				return item;
			}
		}
		return null;
	}

}
