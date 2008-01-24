package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * An <code>ILabelProvider</code> that assists in rendering labels for 
 * <code>ComboBoxPropertyDescriptors</code>.  The label for a given 
 * <code>Integer</code> value is the <code>String</code> at the value in 
 * the provided values array.  
 * 
 * @since 3.0
 * 
 *  @author Sven Wende
 */
public final class ComboBoxLabelProvider extends LabelProvider {

    /**
     * The array of String labels.
     */
    private String[] _values;

    /**
     * @param values the possible label values that this 
     * <code>ILabelProvider</code> may return.
     */
    public ComboBoxLabelProvider(final String[] values) {
        _values = values;
    }

    /**
     * @return the possible label values that this 
     * <code>ILabelProvider</code> may return.
     */
    public String[] getValues() {
        return _values;
    }

    /**
     * @param values the possible label values that this 
     * <code>ILabelProvider</code> may return.
     */
    public void setValues(final String[] values) {
        this._values = values;
    }

    /**
     * Returns the <code>String</code> that maps to the given 
     * <code>Integer</code> offset in the values array.
     * 
     * @param element an <code>Integer</code> object whose value is a valid 
     * location within the values array of the receiver
     * @return a <code>String</code> from the provided values array, or the 
     * empty <code>String</code> 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
	public String getText(final Object element) {
        if (element == null) {
            return ""; //$NON-NLS-1$
        }

        if (element instanceof Integer) {
            int index = ((Integer) element).intValue();
            if (index >= 0 && index < _values.length) {
                return _values[index];
            }
			return ""; //$NON-NLS-1$
        }

        return ""; //$NON-NLS-1$
    }
}
