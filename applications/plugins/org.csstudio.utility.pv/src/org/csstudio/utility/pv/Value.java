package org.csstudio.utility.pv;

/** General Value interface.
 *  TODO: Handle Array data.
 *  @author Kay Kasemir
 */
public interface Value
{
    /** @return Value as a double.
     *  @throws Exception if that is not possible.
     */
    public double toDouble();
    
    /** @return Value as integer.
     *  @throws Exception if that is not possible.
     */
    public int toInt();
    
    /** @return Value as a String. */
    public String toString();
    
    /** @return MetaData that might help to display this value. */
    public MetaData getMeta();
    
    /** Check if the values match within the given tolerance.
     *  <p>
     *  Strings are compared without any tolerance.
     *  Rest is compared like a 'double', using the '&le; tolerance' sense.
     *  
     *  @param other The other value
     *  @param tolerance Numeric tolerance, e.g. 0.1.
     *  @return Returns <code>true</code> if the values match.
     */
    public boolean match(Value other, double tolerance);

    /** @return -1 if this &lt; value,
     *           0 if this == other,
     *           1 if this &gt; other. */
    public int compareTo(Value other);
}
