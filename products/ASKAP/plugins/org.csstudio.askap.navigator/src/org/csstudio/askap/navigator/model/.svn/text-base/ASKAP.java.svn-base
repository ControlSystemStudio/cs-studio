package org.csstudio.askap.navigator.model;

public class ASKAP {
	
	private OPI opiList[];
	private View viewList[];
	
	private static ASKAP askap = null;
	
	public ASKAP() {
		askap = this;
	}
	
	public ASKAP(OPI opiList[], View viewList[]) {
		this.opiList = opiList;
		this.viewList = viewList;
		
		askap = this;
	}
	
	
	public static ASKAP getASKAP() {
		return askap;
	}

	public OPI[] getOpiList() {
		return opiList;
	}

	public void setOpiList(OPI[] opiList) {
		this.opiList = opiList;
	}

	public View[] getViewList() {
		return viewList;
	}

	public void setViewList(View[] viewList) {
		this.viewList = viewList;
	}
	
	// because child element inherit parent macros and this is not set up by Gson
	// need to traver the tree once to set up all the macros
	public void setupMacros() {
		
		if (viewList==null)
			return;
		
		for (View view : viewList) {
			view.setupMacros();
		}
	}
}
