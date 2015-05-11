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
 package org.csstudio.sds.ui.internal.properties;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A table cell editor for values of type double[].
 *
 * @author Kai Meyer
 */
public final class DoubleArrayCellEditor extends CellEditor {
    /**
     * A shell.
     */
    private Shell _shell;

    /**
     * The current double[] value.
     */
    private double[] _value;

    /**
     * Creates a new double[] cell editor parented under the given control. The
     * cell editor value is an double[] value.
     *
     * @param parent
     *            The parent table.
     */
    public DoubleArrayCellEditor(final Composite parent) {
        super(parent, SWT.NONE);
        _shell = parent.getShell();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        String initValue = this.parseToString(_value);
        InputDialog dialog = new InputDialog(_shell,"Numbers","The numbers have to be separated by a comma and a spare.\nExample: 0.5, 10.1, 2",initValue,null);
        if (dialog.open()==Window.OK) {
            _value = this.parseToDoubleArray(dialog.getValue());
        }
        if (_value != null) {
            fireApplyEditorValue();
        }
    }

    /**
     * Parses the given double[] into a comma separated String.
     * @param array
     *                 The double[]
     * @return String
     *                 The corresponding string
     */
    private String parseToString(final double[] array) {
        StringBuffer buffer = new StringBuffer();
        if (array.length>0) {
            buffer.append(array[0]);
            for (int i=1;i<array.length;i++) {
                buffer.append(", ");
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * Parses the given comma separated String into double[].
     * @param text
     *                 The String, which contains doubles separated by comma
     * @return double[]
     *                 The corresponding double[]
     */
    private double[] parseToDoubleArray(final String text) {
        String[] strings = text.split(", ");

        // String#split in Java returns a single-element array (instead of an
        // empty array) if the input was the empty string. But we must return
        // an empty array in that case!
        if (text.equals("")) {
            return new double[0];
        }

        double[] result = new double[strings.length];
        for (int i=0;i<strings.length;i++) {
            Double d = Double.valueOf(strings[i]);
            if (d.isNaN()) {
                result[i] = 0;
            } else {
                result[i] = d.doubleValue();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createControl(final Composite parent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetFocus() {
        // Ignore
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(final Object value) {
        Assert.isTrue(value instanceof double[]);
        this._value = (double[]) value;
    }

}
