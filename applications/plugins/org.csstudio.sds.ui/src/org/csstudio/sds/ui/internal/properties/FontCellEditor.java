/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;

/**
 * A cell editor for fonts.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class FontCellEditor extends DialogCellEditor {
    /**
     * The composite widget.
     */
    private Composite _composite;

    /**
     * The label widget showing the FontData values.
     */
    private Label _fontLabel;

    /**
     * Creates a new font cell editor parented under the given control. The cell
     * editor value is <code>null</code> initially, and has no validator.
     *
     * @param parent
     *            the parent control
     */
    public FontCellEditor(final Composite parent) {
        this(parent, SWT.NONE);
    }

    /**
     * Creates a new font cell editor parented under the given control. The cell
     * editor value is <code>null</code> initially, and has no validator.
     *
     * @param parent
     *            the parent control
     * @param style
     *            the style bits
     */
    public FontCellEditor(final Composite parent, final int style) {
        super(parent, style);
        doSetValue(new FontData("Arial", 8, SWT.NONE)); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite cell) {
        Color bg = cell.getBackground();
        _composite = new Composite(cell, getStyle());
        _composite.setBackground(bg);
        _composite.setLayout(new FillLayout());
        _fontLabel = new Label(_composite, SWT.LEFT);
        _fontLabel.setBackground(bg);
        _fontLabel.setFont(cell.getFont());
        return _composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object openDialogBox(final Control cellEditorWindow) {
        FontDialog dialog = new FontDialog(cellEditorWindow.getShell());
        Object value = getValue();
        if (value != null) {
            FontData[] fontData = new FontData[] { (FontData) value };
            dialog.setFontList(fontData);
        }
        value = dialog.open();
        return dialog.getFontList()[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateContents(final Object value) {
        FontData font = (FontData) value;
        if (font == null) {
            font = new FontData("Arial", 8, SWT.NONE); //$NON-NLS-1$
        }

        _fontLabel.setText("(" + font.getName() + "," + font.getHeight() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
