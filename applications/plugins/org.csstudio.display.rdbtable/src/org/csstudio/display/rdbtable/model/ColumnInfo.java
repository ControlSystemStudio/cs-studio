package org.csstudio.display.rdbtable.model;

/** Information about one table column.
 *  @author Kay Kasemir
 */
public class ColumnInfo
{
    final private String header;
    final private int width;
    
    /** Initialize
     *  @param header Column name (header)
     *  @param width Column widths (in percent)
     */
    public ColumnInfo(final String header, final int width)
    {
        this.header = header;
        this.width = width;
    }

    /** @return Column name (header) */
    protected String getHeader()
    {
        return header;
    }

    /** @return Column widths (in percent) */
    protected int getWidth()
    {
        return width;
    }
}
