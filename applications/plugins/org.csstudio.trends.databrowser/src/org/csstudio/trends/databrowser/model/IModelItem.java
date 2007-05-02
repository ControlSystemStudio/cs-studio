package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.swt.chart.Range;
import org.csstudio.swt.chart.TraceType;
import org.eclipse.swt.graphics.Color;

/** This is the user's interface to one item of the Model.
 *  @see Model
 *  @author Kay Kasemir
 */
public interface IModelItem
{
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
    
    /** @return Returns <code>true</code> if trace should be auto-scaled. */
    public abstract boolean getAutoScale();
    
    /** Set auto-scale mode. */
    public abstract void setAutoScale(boolean auto_scale); 
    
    /** Get the color of this item.
     *  Note that the item own the color.
     *  Do NOT dispose this color!
     *  @return The color.
     */
	public abstract Color getColor();

	/** Set item to a new color. */
	public abstract void setColor(Color new_color);
	
	/** @return Returns the trace line width. */
    public abstract int getLineWidth();
    
    /** Set the trace to a new line width. */
    public abstract void setLineWidth(int new_width);
    
    /** @return Returns current trace type */
    public abstract TraceType getTraceType();
    
    /** Set new trace type for this model. */
    public abstract void setTraceType(TraceType new_trace_type);
    
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