package org.csstudio.alarm.treeView.cacher;

import java.util.List;

public interface ITreeParser {

	public List<String> getAncestors(String name);
	
	public String getParent(String name);
	
	public String getMyName(String name);
	
	public String getParentRName(String name);

	public String specialClean(String toClean);
	
}