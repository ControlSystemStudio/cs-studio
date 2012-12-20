/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.table.ui;

import java.text.DateFormat;
import java.util.Date;

import org.csstudio.alarm.table.internal.localization.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Encapsulation of the message area. It is located below the tree view.<br>
 * FIXME (jpenning) This is a copy of the inner class of the AlarmTreeView.
 * 
 * @author jpenning
 * @since 12.01.2012
 * 
 */
public final class MessageArea {
    /**
     * The message area which can display error messages inside the view part.
     */
    private final Composite _messageAreaComposite;

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

    public MessageArea(final Composite parent) {
        _messageAreaComposite = new Composite(parent, SWT.NONE);
        final GridData messageAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        messageAreaLayoutData.exclude = true;
        _messageAreaComposite.setVisible(false);
        _messageAreaComposite.setLayoutData(messageAreaLayoutData);
        _messageAreaComposite.setLayout(new GridLayout(2, false));

        _messageAreaIcon = new Label(_messageAreaComposite, SWT.NONE);
        _messageAreaIcon.setLayoutData(new GridData(SWT.BEGINNING,
                                                    SWT.BEGINNING,
                                                    false,
                                                    false,
                                                    1,
                                                    2));
        _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION));

        _messageAreaMessage = new Label(_messageAreaComposite, SWT.WRAP);
        _messageAreaMessage.setText(Messages.LogView_defaultMessageText);
        // Be careful if changing the GridData below! The label will not wrap
        // correctly for some settings.
        _messageAreaMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        _messageAreaDescription = new Label(_messageAreaComposite, SWT.WRAP);
        _messageAreaDescription.setText(Messages.LogView_defaultMessageDescription);
        // Be careful if changing the GridData below! The label will not wrap
        // correctly for some settings.
        _messageAreaDescription
                .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    }

    /**
     * Sets the message displayed in the message area of this view part.
     *
     * @param icon the icon to be displayed next to the message. Must be one of
     *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
     *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
     * @param title the title for the message.
     * @param message a descriptive text.
     */
    public void showMessage(final int icon, final String title, final String message) {
        _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(icon));
        _messageAreaMessage.setText(title);

        String dateOut = DateFormat.getDateTimeInstance().format(new Date());
        _messageAreaDescription.setText(dateOut + ": " + message);
        _messageAreaComposite.layout();

        show();
    }

    public void show() {
        _messageAreaComposite.setVisible(true);
        ((GridData) _messageAreaComposite.getLayoutData()).exclude = false;
        _messageAreaComposite.getParent().layout();
    }

    /**
     * Hides the message displayed in this view part.
     */
    public void hide() {
        _messageAreaComposite.setVisible(false);
        ((GridData) _messageAreaComposite.getLayoutData()).exclude = true;
        _messageAreaComposite.getParent().layout();
    }

    public boolean isVisible() {
        return _messageAreaComposite.isVisible();
    }
}