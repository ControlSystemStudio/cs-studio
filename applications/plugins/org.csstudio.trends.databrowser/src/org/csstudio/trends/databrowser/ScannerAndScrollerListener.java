package org.csstudio.trends.databrowser;

/** Notification interface of the ScannerAndScroller
 *  @author Kay Kasemir
 */
public interface ScannerAndScrollerListener
{
    /** Scan the PVs. */
    public void scan(boolean with_redraw);
}
