/**
 * 
 */
package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Property;

import java.util.Collection;

import org.csstudio.channelfinder.util.AlphanumComparator;
import org.csstudio.channelfinder.util.ChannelUtilities;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.swtdesigner.TableViewerColumnSorter;

/**
 * @author shroffk
 * 
 */
public class TableViewerChannelPropertySorter extends TableViewerColumnSorter {

	private String propertyName;

	public TableViewerChannelPropertySorter(TableViewerColumn column,
			String propertyName) {
		super(column);
		this.propertyName = propertyName;
	}

	@Override
	protected int doCompare(Viewer viewer, Object e1, Object e2) {
		// TODO Auto-generated method stub
		return compareProperties((Channel) e1, (Channel) e2);
	}

	private int compareProperties(Channel ch1, Channel ch2) {
		Property prop1 = ChannelUtilities.getProperty(ch1, propertyName);
		Property prop2 = ChannelUtilities.getProperty(ch2, propertyName);
		if ((prop1 == null) && (prop2 == null))
			return 0;
		else if (prop1 == null)
			return -1;
		else if (prop2 == null)
			return +1;
		else
			// Using a Alphanum comparator from http://www.DaveKoelle.com
			return new AlphanumComparator().compare(prop1.getValue(),
					prop2.getValue());
	}
}
