package org.csstudio.trends.sscan.model;

public class AxesConfig {
	/** Model to which this axes belongs */
	private Model model = null;

	private AxisConfig xAxis;
	private AxisConfig yAxis;
	
	/** Name, axes label */
	private String name;
	
	/**
	 * Fire Event when Axis config changed ?
	 */
	private boolean fireEvent;
	
	public void setXAxis(AxisConfig xAxis) {
		this.xAxis = xAxis;
	}
	
	public void setYAxis(AxisConfig yAxis) {
		this.yAxis = yAxis;
	}
	
	public AxesConfig (String name, final AxisConfig xAxis, final AxisConfig yAxis) {
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.name = name;
	}
	
	/**
	 * @param model
	 *            Model to which this item belongs
	 */
	void setModel(final Model model) {
		this.model = model;
		this.xAxis.setModel(model);
		this.yAxis.setModel(model);
	}
	
	public void setFireEvent(boolean fireEvent) {
		this.fireEvent = fireEvent;
	}
	
	public boolean isFireEvent() {
		return fireEvent;
	}
	
	/** @return Axes title */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            New axis title
	 */
	public void setName(final String name) {
		this.name = name;
	//	if(fireEvent)fireAxesChangeEvent();
	}
	
	public AxisConfig getXAxis() {
		return xAxis;
	}
	
	public AxisConfig getYAxis() {
		return yAxis;
	}
	
	/** Notify model about changes */
	//private void fireAxesChangeEvent() {
	//	if (model != null){
	//		model.fireAxisChangedEvent(this.xAxis);
	//		model.fireAxisChangedEvent(this.yAxis);
	//	}
	//}
	
	/** @return Copied axis configuration. Not associated with a model */
	public AxesConfig copy() {
		return new AxesConfig(name, xAxis.copy(),yAxis.copy());
	}

}
