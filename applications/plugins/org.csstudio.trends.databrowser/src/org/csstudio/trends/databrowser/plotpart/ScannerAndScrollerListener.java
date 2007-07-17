package org.csstudio.trends.databrowser.plotpart;

/** Notification interface of the ScannerAndScroller
 *  @author Kay Kasemir
 */
public interface ScannerAndScrollerListener
{
    /** Scan the PVs, recompute formulas, and maybe redraw.
     *  <p>
     *  Invoked in the UI thread so that it may in fact redraw.
     */
    public void scan(boolean with_redraw);
}
