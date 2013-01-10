/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview.views.dialog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Factory for a dialog containing a long message and a detail button.
 * 
 * A detail dialog displays
 * - an icon (error or information)
 * - a short message
 * - a detail button
 * - an ok button
 * - optional (depending on the state of the detail button) a scrollable text area with a multi-line message 
 * 
 * @author jpenning
 */
public final class DetailDialog {
    
    private static final String OK_BUTTON_TEXT = "Ok";
    private static final String DETAIL_BUTTON_COLLAPSED_TEXT = "Details >>";
    private static final String DETAIL_BUTTON_EXPANDED_TEXT = "<< Details";

    private static final int MINIMUM_BUTTON_WIDTH = 80;
    private static final int HORIZONTAL_INDENT_2ND_COLUMN = 15;
    private static final int VERTICAL_INDENT_1ST_ROW = 5;
    
    private DetailDialog() {
        // hidden constructor because this is a utility class
    }
    
    public static void open(@Nonnull final Shell parentShell,
                            final boolean isError,
                            @Nonnull final String title,
                            @Nonnull final String shortMessage,
                            @Nonnull final String longMessage) {
        final Shell dialog = createDialog(parentShell, isError, title, shortMessage, longMessage);
        dialog.open();
    }
    
    @Nonnull
    private static Shell createDialog(@Nonnull final Shell parentShell,
                                      final boolean isError,
                                      @Nonnull final String title,
                                      @Nonnull final String shortMessage,
                                      @Nonnull final String longMessage) {
        final Shell dialog = new Shell(parentShell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM
                | SWT.RESIZE);
        GridLayout layout = new GridLayout(3, false);
        dialog.setLayout(layout);
        dialog.setText(title);
        dialog.setSize(450, 400);
        
        Image image = PlatformUI.getWorkbench().getDisplay()
                .getSystemImage(isError ? SWT.ICON_ERROR : SWT.ICON_INFORMATION);
        
        // Create the widgets
        Label imageLabel = new Label(dialog, SWT.LEFT);
        Label messageLabel = new Label(dialog, SWT.LEFT);
        Button okButton = new Button(dialog, SWT.PUSH);
        okButton.setFocus();
        final Button detailButton = new Button(dialog, SWT.PUSH);
        addFillerLabel(dialog);
        final Text text = new Text(dialog, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER
                | SWT.READ_ONLY);
        
        imageLabel.setImage(image);
        layoutImageLabel(imageLabel);
        
        messageLabel.setText(shortMessage);
        layoutMessageLabel(messageLabel);
        
        okButton.setText(OK_BUTTON_TEXT);
        okButton.addListener(SWT.Selection, createOkButtonListener(dialog));
        layoutOkButton(okButton);
        
        detailButton.setText(DETAIL_BUTTON_COLLAPSED_TEXT);
        detailButton
        .addListener(SWT.Selection,
                     createDetailButtonListener(longMessage, dialog, detailButton, text));
        layoutDetailButton(detailButton);
        
        text.setVisible(false);
        layoutText(text);
        
        dialog.pack();
        return dialog;
    }

    @SuppressWarnings("unused")
    private static void addFillerLabel(@Nonnull final Shell dialog) {
        new Label(dialog, SWT.LEFT);
    }

    private static void layoutImageLabel(@Nonnull final Label imageLabel) {
        GridData layoutData = new GridData();
        layoutData.verticalSpan = 2;
        layoutData.verticalIndent = VERTICAL_INDENT_1ST_ROW;
        imageLabel.setLayoutData(layoutData);
    }

    private static void layoutMessageLabel(@Nonnull final Label labelForMessage) {
        GridData layoutData;
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.horizontalIndent = HORIZONTAL_INDENT_2ND_COLUMN;
        layoutData.verticalIndent = VERTICAL_INDENT_1ST_ROW;
        labelForMessage.setLayoutData(layoutData);
    }

    private static void layoutOkButton(@Nonnull final Button okButton) {
        GridData layoutData = new GridData();
        layoutData.horizontalIndent = HORIZONTAL_INDENT_2ND_COLUMN;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = MINIMUM_BUTTON_WIDTH;
        okButton.setLayoutData(layoutData);
    }
    
    private static void layoutDetailButton(@Nonnull final  Button detailButton) {
        GridData layoutData;
        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.END;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.minimumWidth = MINIMUM_BUTTON_WIDTH;
        detailButton.setLayoutData(layoutData);
    }
    
    private static void layoutText(@Nonnull final Text text) {
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalIndent = HORIZONTAL_INDENT_2ND_COLUMN;
        text.setLayoutData(layoutData);
    }
    
    @Nonnull
    private static Listener createDetailButtonListener(@Nonnull final String longMessage,
                                                       @Nonnull final Shell dialog,
                                                       @Nonnull final Button detailButton,
                                                       @Nonnull final Text text) {
        return new Listener() {
            @Override
            public void handleEvent(@Nullable Event event) {
                text.setVisible(!text.isVisible());
                if (text.isVisible()) {
                    text.setText(longMessage);
                    detailButton.setText(DETAIL_BUTTON_EXPANDED_TEXT);
                    ((GridData) text.getLayoutData()).heightHint = 600;
                    ((GridData) text.getLayoutData()).widthHint = 600;
                } else {
                    text.setText("");
                    detailButton.setText(DETAIL_BUTTON_COLLAPSED_TEXT);
                    ((GridData) text.getLayoutData()).heightHint = SWT.DEFAULT;
                    ((GridData) text.getLayoutData()).widthHint = SWT.DEFAULT;
                }
                dialog.pack();
            }
        };
    }
    
    @Nonnull
    private static Listener createOkButtonListener(@Nonnull final Shell dialog) {
        return new Listener() {
            @Override
            public void handleEvent(@Nullable Event event) {
                dialog.close();
            }
        };
    }
    
}
