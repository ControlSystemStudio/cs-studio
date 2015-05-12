/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
