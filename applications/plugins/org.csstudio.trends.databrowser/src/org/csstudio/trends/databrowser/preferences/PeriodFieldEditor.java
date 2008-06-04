package org.csstudio.trends.databrowser.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/** Editor for handling a 'double' typed period with minimum.
 *  @author Kay Kasemir
 */
public class PeriodFieldEditor extends StringFieldEditor
{
    final private double minimum;

    public PeriodFieldEditor(final String name, final String labelText,
            final Composite parent, final double minimum)
    {
        super(name, labelText, parent);
        this.minimum = minimum;
    }

    @Override
    protected boolean checkState()
    {
        final Text text = getTextControl();
        if (text == null)
            return false;

        try
        {
            final double period = Double.parseDouble(text.getText().trim());
            if (period < minimum)
            {
                showErrorMessage(NLS.bind(Messages.PeriodFieldEditor_MinimumMsg,
                        minimum));
                return false;
            }
            clearErrorMessage();
            return true;
        }
        catch (Exception ex)
        {
            showErrorMessage(Messages.PeriodFieldEditor_ParseError);
            return false;
        }
    }
}
