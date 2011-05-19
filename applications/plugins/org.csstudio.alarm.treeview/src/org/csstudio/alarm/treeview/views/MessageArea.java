/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.views;

import java.text.DateFormat;
import java.util.Date;

import javax.annotation.Nonnull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Encapsulation of the message area. It is located below the tree view.
 */
public final class MessageArea {
    /**
     * The message area which can display error messages inside the view part.
     */
    private final Composite _messageArea;

    /**
     * The icon displayed in the message area.
     */
    private final Label _messageAreaIcon;

    /**
     * The message displayed in the message area.
     */
    private final Label _messageAreaMessage;

    /**
     * The description displayed in the message area.
     */
    private final Label _messageAreaDescription;

    /**
     * Constructor.
     * @param parent the composite
     */
    public MessageArea(@Nonnull final Composite parent) {

        _messageArea = new Composite(parent, SWT.NONE);
        final GridData messageAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        messageAreaLayoutData.exclude = true;
        _messageArea.setVisible(false);
        _messageArea.setLayoutData(messageAreaLayoutData);
        _messageArea.setLayout(new GridLayout(2, false));

        _messageAreaIcon = new Label(_messageArea, SWT.NONE);
        _messageAreaIcon.setLayoutData(new GridData(SWT.BEGINNING,
                                                    SWT.BEGINNING,
                                                    false,
                                                    false,
                                                    1,
                                                    2));

        _messageAreaMessage = new Label(_messageArea, SWT.WRAP);
        // Be careful if changing the GridData below! The label will not wrap
        // correctly for some settings.
        _messageAreaMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        _messageAreaDescription = new Label(_messageArea, SWT.WRAP);
        // Be careful if changing the GridData below! The label will not wrap
        // correctly for some settings.
        _messageAreaDescription
                .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        clearMessage();
    }

    /**
     * Sets the message displayed in the message area of this view part. The message area is shown.
     *
     * @param icon the icon to be displayed next to the message. Must be one of
     *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
     *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
     * @param message the message.
     * @param description a descriptive text.
     */
    public void showMessage(final int icon, @Nonnull final String message, @Nonnull final String description) {
        _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(icon));
        _messageAreaMessage.setText(message);
        
        
        String dateOut = DateFormat.getDateTimeInstance().format(new Date());

        _messageAreaDescription.setText(dateOut + " " + description);
        _messageArea.layout();

        show();
    }

    
    /**
     * Reset to the default message and hide.
     */
    public void clearMessage() {
        _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));
        _messageAreaMessage.setText("No message");
        _messageAreaDescription.setText("");
        _messageArea.layout();

        hide();
    }
    
    /**
     * Makes the message area visible.
     */
    public void show() {
        _messageArea.setVisible(true);
        ((GridData) _messageArea.getLayoutData()).exclude = false;
        _messageArea.getParent().layout();
    }


    /**
     * Hides the message displayed in this view part.
     */
    public void hide() {
        _messageArea.setVisible(false);
        ((GridData) _messageArea.getLayoutData()).exclude = true;
        _messageArea.getParent().layout();
    }

    /**
     * @return true, if the message area is visible
     */
    public boolean isVisible() {
        return _messageArea.isVisible();
    }
    
    /**
     * Makes visible or hides, resp.
     */
    public void toggleVisibility() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }

}
