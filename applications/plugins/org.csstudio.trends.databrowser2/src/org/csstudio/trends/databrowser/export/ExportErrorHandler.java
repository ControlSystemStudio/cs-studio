package org.csstudio.trends.databrowser.export;

/** Interface for handler of errors during data export
 *  @author Kay Kasemir
 */
public interface ExportErrorHandler
{
    /** Will be invoked on error */
    public void handleExportError(Exception ex);
}
