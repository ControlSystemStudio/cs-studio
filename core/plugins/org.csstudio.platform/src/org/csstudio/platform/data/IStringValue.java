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
    /** @return Returns the whole array of values. */
    public String[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most samples are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public String getValue();
}