package org.csstudio.trends.databrowser.sampleview;

//import java.text.Format;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.trends.databrowser.model.ModelSample;
import org.csstudio.trends.databrowser.model.QualityHelper;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** The JFace label provider for ModelSample data.
 *  @author Kay Kasemir
 *  @author Albert Kagarmanov
 */
public class SampleTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider
{
    enum Column
    {
        Time,
        Value,
        StatSevr,
        Quality,
        Source
    };
    
    /** Get text for all but the columns.
     *  @param index: 0, 1, 2 for Time, Value, Info
     */
	public String getColumnText(final Object obj, final int index)
	{
        final TableItem item = (TableItem) obj;
        final ModelSample sample = item.getSample();
        switch (Column.values()[index])
        {
        case Time:
            return sample.getSample().getTime().toString();
        case Value:
            return sample.getSample().format();
        case StatSevr:
            final IValue value = sample.getSample();
            final String sevr = value.getSeverity().toString();
            final String stat = value.getStatus();
            return sevr + " " + stat; //$NON-NLS-1$
        case Quality:
            return QualityHelper.getString(sample.getSample().getQuality());
        default: // case Source:
            String source = sample.getSource();
            return source == null ? "" : source; //$NON-NLS-1$
        }
	}

    /** Get column image */
	public Image getColumnImage(Object obj, int index)
	{
        return null; // no column images
	}

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getBackground(final Object obj, final int index)
    {
        final TableItem item = (TableItem) obj;
        final ModelSample sample = item.getSample();
        final ISeverity severity = sample.getSample().getSeverity();
        if (severity.isOK())
            return null; // no special color
        // Make entry stand out,
        // using system color that we don't have to dispose
        if (severity.isMinor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        if (severity.isMajor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
    }

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getForeground(Object obj, int index)
    {
        return null;
    }
}
