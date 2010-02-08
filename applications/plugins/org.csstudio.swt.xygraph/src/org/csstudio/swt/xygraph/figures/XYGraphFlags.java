package org.csstudio.swt.xygraph.figures;

/** Bits for flags that the XYGraph package uses
 *  @author Kay Kasemir
 */
public interface XYGraphFlags
{
    /** Flag to create toolbar with combined zoom in/out buttons (default).
     *  @see ToolbarArmedXYGraph
     */
    final public static int DEFAULT_ZOOMS = 1 << 0;

    /** Flag to create toolbar with separate horizontal/vertical zoom buttons.
     *  @see ToolbarArmedXYGraph
     */
    final public static int SEPARATE_ZOOMS = 1 << 1;

    /** Include the 'stagger' button.
     *  @see ToolbarArmedXYGraph
     */
    final public static int STAGGER = 1 << 2;
}
