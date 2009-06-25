package org.csstudio.swt.xygraph.dataprovider;

import java.util.Iterator;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.swt.widgets.Display;


/**
 * Provides data to a trace. 
 * @author Xihui Chen
 *
 */
public class CircularBufferDataProvider extends AbstractDataProvider{
	
	public enum UpdateMode{
		X_OR_Y("X or Y"),
		X_AND_Y("X AND Y"),
		X("X"),
		Y("Y"),
		TRIGGER("Trigger"),
		PERIODICALLY_SAMPLE("Sample at regular intervals");		
				
		private UpdateMode(String description) {
			 this.description = description;
		}
		private String description;
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(UpdateMode p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}	

	public enum PlotMode{
		LAST_N("Plot last n pts."),
		N_STOP("Plot n pts & stop.");	
				
		private PlotMode(String description) {
			 this.description = description;
		}
		private String description;
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(PlotMode p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	private CircularBuffer<ISample> traceData;	
	
	private double currentXData;
	
	private double currentYData;
	
	private long currentYDataTimestamp;	
	
	private boolean currentXDataChanged = false;
	
	private boolean currentYDataChanged = false;
	
	private boolean currentYDataTimestampChanged = false;
	
	private double[] currentXDataArray = new double[]{};
	
	private double[] currentYDataArray = new double[]{};
	
	private boolean currentXDataArrayChanged = false;
	
	private boolean currentYDataArrayChanged = false;
	
	private boolean xAxisDateEnabled = false;
	
	private int updateDelay = 0;
	private boolean duringDelay = false;
	private boolean updateTriggered = false;
	
	private UpdateMode updateMode = UpdateMode.X_AND_Y;
	
	private PlotMode plotMode = PlotMode.LAST_N;

	private Runnable fireUpdate;
	
	public CircularBufferDataProvider(boolean chronological) {
		super(chronological);
		traceData = new CircularBuffer<ISample>(100);
		fireUpdate = new Runnable(){
			public void run() {
				for(IDataProviderListener listener : listeners){
					listener.dataChanged(CircularBufferDataProvider.this);
				}
				duringDelay = false;
			}			
		};
	}

	/**
	 * @param currentXData the currentXData to set
	 */
	public void setCurrentXData(double newValue) {
		this.currentXData = newValue;
		currentXDataChanged = true;
		tryToAddDataPoint();
	}


	/**Set current YData.
	 * @param currentYData the currentYData to set
	 */
	public void setCurrentYData(double newValue) {
		this.currentYData = newValue;
		currentYDataChanged = true;
		if(!xAxisDateEnabled|| (xAxisDateEnabled && currentYDataTimestampChanged))
			tryToAddDataPoint();
	}
	
	public void addSample(ISample sample){
		if(traceData.size() == traceData.getBufferSize() && plotMode == PlotMode.N_STOP)
			return;
		traceData.add(sample);
		fireDataChange();
	}
	
	/**Set the time stamp of currrent YData
	 * @param timestamp timestamp of Y data in milliseconds.
	 */
	public void setCurrentYDataTimestamp(long timestamp){
		if(!xAxisDateEnabled){
			clearTrace();
			xAxisDateEnabled = true;
		}		
		this.currentYDataTimestamp = timestamp;
		currentYDataTimestampChanged = true;
		if(currentYDataChanged)
			tryToAddDataPoint();		
	}
	
	/**Set current YData and its timestamp when the new value generated.
	 * @param currentYData the currentYData to set
	 * @param timestamp timestamp of Y data in milliseconds.
	 */
	public void setCurrentYData(double newValue, long timestamp) {
		xAxisDateEnabled = true;
		this.currentYData = newValue;
		currentYDataChanged = true;
		this.currentYDataTimestamp = timestamp;
		currentYDataTimestampChanged = true;		
		tryToAddDataPoint();
	}
	
	/**
	 * Try to add a new data point to trace data. 
	 * Whether it will be added or not is up to the update mode.
	 */
	private void tryToAddDataPoint(){
		if(traceData.size() == traceData.getBufferSize() && plotMode == PlotMode.N_STOP)
			return;
		switch (updateMode) {
		case X_OR_Y:
			if((chronological && currentYDataChanged) ||
					(!chronological && (currentXDataChanged || currentYDataChanged)))
				addDataPoint();
			break;
		case X_AND_Y:
			if((chronological && currentYDataChanged) ||
					(!chronological && (currentXDataChanged && currentYDataChanged)))
				addDataPoint();					
			break;
		case X:
			if((chronological && currentYDataChanged) || 
					(!chronological && currentXDataChanged))
				addDataPoint();
			break;
		case Y:			
			if(currentYDataChanged)
				addDataPoint();
			break;
		case TRIGGER:
			if(updateTriggered && (chronological || 
					(!chronological && (currentXDataChanged || currentYDataChanged)))){
				addDataPoint();
				updateTriggered = false;				
			}
				
			break;
		case PERIODICALLY_SAMPLE:
			if(chronological || 
					(!chronological && (currentXDataChanged || currentYDataChanged)))
				addDataPoint();
			break;
		default:
			break;
		}
	}


	/**
	 * add a new data point to trace data.
	 */
	private void addDataPoint() {	
		double newXValue;		
		if(chronological){
			if(xAxisDateEnabled)
				newXValue = currentYDataTimestamp;		
			else{
				if(traceData.size() == 0)
					newXValue = 0;
				else
					newXValue = traceData.getTail().getXValue() +1;
			}					
		}else{
			newXValue = currentXData;
		}
			traceData.add(new Sample(newXValue, currentYData));
			currentXDataChanged = false;
			currentYDataChanged = false;
			currentYDataTimestampChanged = false;
			fireDataChange();			
	}
	
	
	/**
	 * @param currentXData the currentXData to set
	 */
	public void setCurrentXDataArray(double[] newValue) {
		this.currentXDataArray = newValue;
		currentXDataArrayChanged = true;
		tryToAddDataArray();
	}
	
	/**
	 * @param currentXData the currentXData to set
	 */
	public void setCurrentYDataArray(double[] newValue) {
		this.currentYDataArray = newValue;
		currentYDataArrayChanged = true;
		tryToAddDataArray();
	}
	
	
	/**
	 * Try to add a new data array to trace data. 
	 * Whether it will be added or not is up to the update mode.
	 */
	private void tryToAddDataArray(){
		if(traceData.size() == traceData.getBufferSize() && plotMode == PlotMode.N_STOP)
			return;
		switch (updateMode) {
		case X_OR_Y:
			if((chronological && currentYDataArrayChanged) ||
					(!chronological && (currentXDataArrayChanged || currentYDataArrayChanged)))
				addDataArray();
			break;
		case X_AND_Y:
			if((chronological && currentYDataArrayChanged) ||
					(!chronological && (currentXDataArrayChanged && currentYDataArrayChanged)))
				addDataArray();					
			break;
		case X:
			if((chronological && currentYDataArrayChanged) || 
					(!chronological && currentXDataArrayChanged))
				addDataArray();
			break;
		case Y:			
			if(currentYDataArrayChanged)
				addDataArray();
			break;
		case TRIGGER:
			if(updateTriggered && (chronological || 
					(!chronological && (currentXDataArrayChanged || currentYDataArrayChanged)))){
				addDataArray();
				updateTriggered = false;
			}
				
			break;
		case PERIODICALLY_SAMPLE:			
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * add a new data point to trace data.
	 */
	private void addDataArray() {	
		double[] newXValueArray;		
		if(chronological){		
			newXValueArray = new double[currentYDataArray.length];
			if(traceData.size() == 0)
				for(int i=0; i<currentYDataArray.length; i++){
					newXValueArray[i] = i;
				}
			else
				for(int i=1; i<currentYDataArray.length+1; i++){
					newXValueArray[i-1] = traceData.getTail().getXValue() + i;
				}
								
		}else{
			traceData.clear();
			newXValueArray = currentXDataArray;
		}
		for(int i=0; i<Math.min(newXValueArray.length, currentYDataArray.length); i++){
			traceData.add(new Sample(newXValueArray[i], currentYDataArray[i]));
		}
			
			currentXDataChanged = false;
			currentYDataChanged = false;
			currentYDataTimestampChanged = false;
			fireDataChange();		
	}
	

	public void clearTrace(){
		traceData.clear();
		fireDataChange();
	}
	
	public Iterator<ISample> iterator() {
		return traceData.iterator();
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		traceData.setBufferSize(bufferSize, false);
	}

	
	/**
	 * @param updateMode the updateMode to set
	 */
	public void setUpdateMode(UpdateMode updateMode) {
		this.updateMode = updateMode;
	}

	/**In TRIGGER update mode, the trace data could be updated by this method 
	 * @param triggerValue the triggerValue to set
	 */
	public void triggerUpdate() {
		updateTriggered = true;
		tryToAddDataPoint();
		tryToAddDataArray();
	}

	@Override
	protected void innerUpdate() {
		if(getSize() > 0){
			double xMin;
			double xMax;
			xMin = traceData.getHead().getXValue();
			xMax = xMin;
			
			double yMin;
			double yMax;
			yMin = traceData.getHead().getYValue();
			yMax = yMin;
			for(ISample dp : traceData){
				if(xMin > dp.getXValue()-dp.getXMinusError())
					xMin = dp.getXValue()-dp.getXMinusError();
				if(xMax < dp.getXValue()+dp.getXPlusError())
					xMax = dp.getXValue()+ dp.getXPlusError();	
				
				if(yMin > dp.getYValue() - dp.getYMinusError())
					yMin = dp.getYValue() - dp.getYMinusError();
				if(yMax < dp.getYValue() + dp.getYPlusError())
					yMax = dp.getYValue() + dp.getYPlusError();	
			}
			
			xDataMinMax = new Range(xMin, xMax);
			yDataMinMax = new Range(yMin, yMax);
		}else {
			xDataMinMax = null;
			yDataMinMax = null;
		}
		
	}

	/**
	 * @param plotMode the plotMode to set
	 */
	public void setPlotMode(PlotMode plotMode) {
		this.plotMode = plotMode;
	}

	@Override
	public ISample getSample(int index) {
		return traceData.getElement(index);
	}

	@Override
	public int getSize() {
		return traceData.size();
	}

	/**If xAxisDateEnable is true, you will need to use 
	 * {@link #setCurrentYData(double, long)} or {@link #setCurrentYDataTimestamp(long)} to set the 
	 * time stamp of ydata. This flag will be automatically enabled when
	 * either of these two methods were called.
	 * The default value is false.
	 * @param xAxisDateEnabled the xAxisDateEnabled to set
	 */
	public void setXAxisDateEnabled(boolean xAxisDateEnabled) {
		this.xAxisDateEnabled = xAxisDateEnabled;
	}

	/**
	 * @param updateDelay Delay in milliseconds between plot updates. This may help to reduce CPU
	 * usage. The default value is 0ms.
	 */
	public void setUpdateDelay(int updateDelay) {
		this.updateDelay = updateDelay;
	}
	
	@Override
	protected void fireDataChange() {
		if(updateDelay >0){
			innerUpdate();			
			if(!duringDelay){
				Display.getCurrent().timerExec(updateDelay, fireUpdate);	
				duringDelay = true;
			}					
		}else
			super.fireDataChange();			
	}
	

}
