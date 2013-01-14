/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.view;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Open a Dialog with a Device Database Error message.
 * 
 * @author Rickens Helge
 * @author $Author: $
 * @since 06.01.2011

 */
public final class DeviceDatabaseErrorDialog {
    
    /**
     * Constructor.
     */
    private DeviceDatabaseErrorDialog() {
        // Constructor.
    }
    
    public static void open(@Nullable final Shell parent,@CheckForNull final String message,@CheckForNull final Exception e) {
        final StringBuilder sb = buildMessage(message, e);
        openDialog(parent,  sb, null);
    }
    public static void open(@Nullable final Shell parent,@CheckForNull final String message,@CheckForNull final Exception e, @CheckForNull final ProfiBusTreeView busTreeView) {
        final StringBuilder sb = buildMessage(message, e);
        openDialog(parent,  sb, busTreeView);
        
    }
    
    /**
     * @param message
     * @param e
     * @return
     */
    @Nonnull
    private static StringBuilder buildMessage(@CheckForNull final String message, @CheckForNull final Exception e) {
        final StringBuilder sb = new StringBuilder();
        if(message!=null) {
            sb.append(message)
            .append("\r");
            
        }
        if(e!=null) {
            String eMessage = e.getLocalizedMessage();
            if (eMessage == null) {
                eMessage = e.getMessage();
                if (eMessage == null) {
                    eMessage = "Unknown Exception";
                }
            }
            
            sb.append(eMessage);
        }
        return sb;
    }
    
    /**
     * @param parent
     * @param title
     * @param sb
     * @param busTreeView
     */
    private static void openDialog(@Nullable final Shell parent, @Nonnull final StringBuilder sb, @CheckForNull final ProfiBusTreeView busTreeView) {
        final String title = "Device Database Error!";
        String[] dialogButtonLabels;
        if(busTreeView!=null) {
            dialogButtonLabels = new String[] {"Close", "Reload DB" };
        } else {
            dialogButtonLabels = new String[] {"Close"};
        }
        final MessageDialog messageDialog = new MessageDialog(parent, title, null,
                                                              sb.toString(), MessageDialog.ERROR,
                                                              dialogButtonLabels, 1);
        if (messageDialog.open() == 1 && busTreeView!=null) {
            busTreeView.reload();
        }
    }
    
}
