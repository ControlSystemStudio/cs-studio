package org.csstudio.util.swt;

/** Helper for providing default colors.
 * 
 *  @author Kay Kasemir
 */
public class DefaultColors
{
    /** Default colors for newly added series, used over when reaching the end.
     *  <p>
     *  Very hard to find a long list of distinct colors.
     *  This list is definetely too short...
     */
    private static final int[][] default_colors =
    {
        {  21,  21, 196 }, // blue
        { 242,  26,  26 }, // red
        {  33, 179,  33 }, // green
        { 255, 190,   0 }, // yellow
        { 214,   0, 255 }, // violett
        {   0,   0,   0 }, // black
        { 243, 132, 132 }, // peachy
        {   0, 255,  11 }, // neon green
        {   0, 214, 255 }, // neon blue
        { 114,  40,   3 }, // brown
        { 255,   0, 240 }, // orange
        { 131, 153,  36 }, // purple
    };

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'red' component of that default color.
     */
    public final static int getRed(int num)
    {
        num %= default_colors.length;
        return default_colors[num][0];
    }

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'green' component of that default color.
     */
    public final static int getGreen(int num)
    {
        num %= default_colors.length;
        return default_colors[num][1];
    }

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'green' component of that default color.
     */
    public final static int getBlue(int num)
    {
        num %= default_colors.length;
        return default_colors[num][2];
    }
}
