package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

/** Static value that holds a text
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TextValue extends Value
{
    /** Initialize
     *  @param name
     *  @param text
     *  @param valid
     */
    public TextValue(final String name, final String text, final boolean valid)
    {
        super(name);
        final ISeverity severity;
        final String status;
        if (valid)
        {
            severity = ValueFactory.createOKSeverity();
            status = severity.toString();
        }
        else
        {
            severity = ValueFactory.createInvalidSeverity();
            status = "undefined";
        }
        setValue(ValueFactory.createStringValue(TimestampFactory.now(), severity, status, Quality.Original,
                new String[] { text }));
    }

    /** Initialize
     *  @param name
     *  @param text
     */
    public TextValue(final String name, final String text)
    {
        this(name, text, true);
    }
}
