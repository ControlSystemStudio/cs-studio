package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.swt.chart.ChartSample;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** The JFace label provider for ChartSample data. 
 *  @author Kay Kasemir
 */
public class SampleTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider
{
    /** Get text for all but the 'select' column. */
	public String getColumnText(Object obj, int index)
	{
        ChartSample sample = (ChartSample) obj;
        if (index == 0)
            return TimestampUtil.fromDouble(sample.getX()).toString();
        else if (index == 1)
        {
            double value = sample.getY();
            if (Double.isInfinite(value))
                return ""; //$NON-NLS-1$
            return Double.toString(value);
        }
        else
        {
            String info = sample.getInfo();
            return info == null ? "" : info; //$NON-NLS-1$
        }
	}

    /** Get column image (only for the 'select' column) */
	public Image getColumnImage(Object obj, int index)
	{
        return null;
	}

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getBackground(Object obj, int index)
    {
        ChartSample sample = (ChartSample) obj;
        if (sample.getInfo() == null)
            return null; // no special color
        // Make entry stand out, 
        // using system color that we don't have to dispose
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getForeground(Object obj, int index)
    {
        return null;
    }
}
