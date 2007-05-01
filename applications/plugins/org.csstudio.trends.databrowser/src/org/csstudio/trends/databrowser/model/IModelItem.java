package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.swt.graphics.Color;

/** This is the user's interface to one item of the Model.
 *  @see Model
 *  @author Kay Kasemir
 */
public interface IModelItem
{
	/** Defines basic display types for this model */
	public enum DisplayType 
    {
		Lines(0),
    	Markers(1),
    	Candlestick(2),
    	//HighLowArea(3),
		MinMaxAverage(3),
		Bars(4);
    	
    	private final byte value;
    	
    	private static String[] strDisplayTypes;
    	
    	DisplayType(int value) {
		   this.value = (byte) value;
		}

		public byte convert() {
		   return value;
		}
		
		public final static DisplayType fromInteger(int value) {
			for(DisplayType type : DisplayType.values()) {
				if(value == type.convert())
					return type;
			}
			return DisplayType.Lines;
		}
		
		public final static String[] toStringArray()
		{
			if(strDisplayTypes == null) 
			{
				DisplayType[] displayTypes = DisplayType.values();
	        	strDisplayTypes = new String[displayTypes.length]; 
	        	
	        	for(int i = 0; i < displayTypes.length; i++)
	        		strDisplayTypes[i] = displayTypes[i].toString();
			}
			
			return strDisplayTypes;
		}
    }
	
	/** @return The name to use for this item in the legend or axis label. */
	public abstract String getName();

    /** @return The engineering units string. */
    public abstract String getUnits();
    
    /** Change the name of this entry. */
    public abstract void changeName(String new_name);
    
	/** @return The axis index. */
	public abstract int getAxisIndex();
    
    /** Set a new axis index. */
    public abstract void setAxisIndex(int axis);
    
    /** @return The lower Y-axis limit. */
    public abstract double getAxisLow();

    /** @return The upper Y-axis limit. */
    public abstract double getAxisHigh();

    /** Set lower Y-axis limit. */
    public abstract void setAxisLow(double limit);
    
    /** Set upper Y-axis limit. */
    public abstract void setAxisHigh(double limit);
    
	/** @return The color. */
	public abstract Color getColor();

	/** Set item to a new color. */
	public abstract void setColor(Color new_color);
	
	/** @return Returns the trace line width. */
    public abstract int getLineWidth();
    
    /** Set the trace to a new line width. */
    public abstract void setLineWidth(int new_width);
    
    /** @return Returns current display type */
    public abstract DisplayType getDisplayType();
    
    /** Set new display type for this model. */
    public abstract void setDisplayType(DisplayType new_display_type);
    
    /** @return Returns weather trace should be auto scaled. */
    public abstract boolean getIsTraceAutoScalable();
    
    /** @return Returns server default top range value for y axis */ 
    public abstract double getDefaultScaleMax(); 
    
    /** @return Returns server default bottom range value for y axis */
    public abstract double getDefaultScaleMin();
    
    /** Set weather trace should be auto scaled. */
    public abstract void setIsTraceAutoScalable(boolean scalable); 
    
    /** @return <code>true</code> if using log. scale */
    public abstract boolean getLogScale();

    /** Configure to use log. scale or not. */
    public abstract void setLogScale(boolean use_log_scale);

	/** @return The samples. */
	public abstract ModelSamples getSamples();
    
    /** Add samples obtained from the archive.
     *  <p>
     *  Called from a non-GUI thread!
     */
	public abstract void addArchiveSamples(ArchiveValues samples);

    /** @return The archive data source descriptions. */
    public abstract IArchiveDataSource[] getArchiveDataSources();
    
    /** Add another archive data source. */
    public abstract void addArchiveDataSource(IArchiveDataSource archive);

    /** Remove given archive data source. */
    public abstract void removeArchiveDataSource(IArchiveDataSource archive);
    
    /** Move given archive data source 'up' in the list. */
    public abstract void moveArchiveDataSourceUp(IArchiveDataSource archive);
    
    /** Move given archive data source 'down' in the list. */
    public abstract void moveArchiveDataSourceDown(IArchiveDataSource archive);
}