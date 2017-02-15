/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * An horizontal separator.
 *
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 9 Dec 2016
 */
public class Separator {

    private static Font font3pt = null;

    private final Label separator;

    /**
     * Constructs a new instance of this class given its parent and height.
     *
     * @param parent A {@link Composite} control which will be the parent of
     *            the new instance (cannot be @{code null}).
     */
    public Separator ( Composite parent ) {

        separator = new Label(parent, SWT.HORIZONTAL);

        if ( font3pt == null ) {

            Font f = separator.getFont();
            FontData[] fontData = f.getFontData();

            for (int i = 0; i < fontData.length; i++) {
                fontData[i].setHeight(3);
            }

            font3pt = new Font(separator.getDisplay(), fontData);

        }

        separator.setFont(font3pt);

    }

    /**
     * Sets the layout data associated with the receiver to the argument.
     *
     * @param layoutData The new layout data for the receiver.
     * @throws SWTException If the operation failed:
     *             <UL>
     *               <LI>{@link SWT#ERROR_WIDGET_DISPOSED} if the receiver has been disposed, </LI>
     *               <LI>{@link SWT#ERROR_THREAD_INVALID_ACCESS} if not called from the thread that created the receiver.</LI>
     *             </UL>
     */
    public void setLayoutData ( Object layoutData ) throws SWTException {
        separator.setLayoutData(layoutData);
    }

    /**
     * @return The {@link Label} widget used to implement the separator.
     */
    public Label getSeparator() {
        return separator;
    }

}
