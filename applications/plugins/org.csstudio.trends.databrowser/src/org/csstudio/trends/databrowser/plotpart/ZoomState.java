package org.csstudio.trends.databrowser.plotpart;

import org.eclipse.jface.action.Action;

/** Description of the current zoom state.
 *  @author Kay Kasemir
 */
public class ZoomState extends Action
{
    final private boolean scrolling;
    final private String start_specification, end_specification;
    final double starts[], ends[];
                                             
    /** Constructor
     *  @param description Text shown in GUI to describe this step
     *  @param scrolling <code>true</code> if scrolling enabled
     *  @param start_specification Time axis start spec.
     *  @param end_specification Time axis end spec.
     *  @param starts Y Axes range start points
     *  @param ends Y Axes range end points
     */
    public ZoomState(final String description,
            final boolean scrolling,
            final String start_specification,
            final String end_specification,
            final double starts[], final double ends[])
    {
        super(description);
        if (starts.length != ends.length)
            throw new Error("Size of start and end arrays don't match"); //$NON-NLS-1$
        this.scrolling = scrolling;
        this.start_specification = start_specification;
        this.end_specification = end_specification;
        this.starts = starts;
        this.ends = ends;
    }
    
    // TODO run()

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append(scrolling ? "Scrolling" : "Not scrolling");
        buf.append(", ");
        buf.append(start_specification);
        buf.append(" ... ");
        buf.append(end_specification);
        buf.append("\n");
        for (int i=0; i<starts.length; ++i)
        {
            buf.append(String.format("Y Axis #%2d: %g ... %g\n",
                    i, starts[i], ends[i]));
        }
        return buf.toString();
    }
}
