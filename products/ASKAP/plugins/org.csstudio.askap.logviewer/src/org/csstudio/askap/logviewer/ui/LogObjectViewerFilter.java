package org.csstudio.askap.logviewer.ui;

import org.csstudio.askap.logviewer.util.FilterObject;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class LogObjectViewerFilter extends ViewerFilter {

	private FilterObject filter = new FilterObject();
	
	public LogObjectViewerFilter() {
	}

	public void setFilter(FilterObject filter) {
		this.filter = filter;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		LogObject log = (LogObject) element;
		if (filter.getTag()!=null && filter.getTag().length()>0) {
			if (!log.getTag().equals(filter.getTag()))
				return false;
		}
		
		if (filter.getLogLevel()!=null && filter.getLogLevel().length()>0) {
			if (!log.getLogLevel().equals(filter.getLogLevel()))
				return false;
		}
		
		if (filter.getRegex()!=null && filter.getRegex().length()>0) {
			if (!matches(log, filter.getRegex(), filter.isRegex()))
				return false;
		}

		// TODO Auto-generated method stub
		return true;
	}

	private boolean matches(LogObject o, String regex, boolean isRegex) {
		if (isRegex) {
			if (o.getLogLevel().matches(regex))
				return true;

			if (o.getOrigin().matches(regex))
				return true;

/*
			if (o.timeStamp.matches(regex))
				return true;
*/
			if (o.getTag().matches(regex))
				return true;

			if (o.getHostName().matches(regex))
				return true;

			if (o.getLogMessage().matches(regex))
				return true;
		} else {
			if (o.getLogLevel().indexOf(regex) >= 0)
				return true;

			if (o.getOrigin().indexOf(regex) >= 0)
				return true;

/*			
			if (o.timeStamp.indexOf(regex) >= 0)
				return true;
*/
			if (o.getTag().indexOf(regex) >= 0)
				return true;

			if (o.getHostName().indexOf(regex) >= 0)
				return true;

			if (o.getLogMessage().indexOf(regex) >= 0)
				return true;
		}
		
		return false;
	}

}
