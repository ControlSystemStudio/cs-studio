package org.csstudio.platform.data;

/** An string value.
 *  <p>
 *  <p>
 *  {@link IStringValue} values have no meta data, i.e.
 *  <code>getMetaData()</code> will return <code>null</code>!
 *  @see IValue
 *  @author Kay Kasemir
 */
public interface IStringValue extends IValue
{
    /** @return Returns the String. */
    public String getValue();
}