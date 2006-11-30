package org.csstudio.util.swt;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/** A table cell editor for values of type RGB.
 *  <p>
 *  There is already a ColorCellEditor, but when activated,
 *  it adds another step where it only displays a small color
 *  patch, the RGB indices and then a button to start the dialog.
 *  <p>
 *  That's a waste of real estate, adds another 'click' to the
 *  editing of colors, plus the overall layout was really poor
 *  on Mac OS X, where the button didn't fully show.
 *  <p>
 *  This implementation, based on the CheckboxCellEditor sources,
 *  jumps right into the color dialog.
 *  @author Kay Kasemir
 */
public class RGBCellEditor extends CellEditor
{
	private Shell shell;
	private RGB value;
	
    /** Creates a new color cell editor parented under the given control.
     *  The cell editor value is an SWT RGB value.
     *  @param parent The parent table.
     */
    public RGBCellEditor(Composite parent)
    {
        super(parent, SWT.NONE);
        shell = parent.getShell();
    }

    /** Opens the color dialog. */
    @Override
    public void activate()
    {
        ColorDialog dialog = new ColorDialog(shell);
        if (value != null)
            dialog.setRGB(value);
        value = dialog.open();
        if (value != null)
        	fireApplyEditorValue();
    }

    /** NOP */
    @Override
    protected Control createControl(Composite parent)
    {
        return null;
    }

    /** @return Returns the current RGB value. */
    @Override
    protected Object doGetValue()
    {
        return value;
    }

    @Override
    protected void doSetFocus()
    {
        // Ignore
    }

    /** Called by the framework to initialize the RGB value.
     * @param value Should be an RGB.
     */
    @Override
    protected void doSetValue(Object value)
    {
        Assert.isTrue(value instanceof RGB);
        this.value = (RGB) value;
    }
}
