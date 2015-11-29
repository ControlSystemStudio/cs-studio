/*******************************************************************************
* Copyright (c) 2010-2015 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.ui.util;

import org.eclipse.jface.dialogs.IDialogConstants;

/**
 * <code>DialogConstants</code> provide a single source access to the real IDialogConstants, which for some reason is
 * not same for RAP and RCP. RAP implements access to labels as methods, RCP does it with static with fields.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class DialogConstants {

    public static final String OK_LABEL = IDialogConstants.get().OK_LABEL;
    public static final String CANCEL_LABEL = IDialogConstants.get().CANCEL_LABEL;
    public static final String YES_LABEL = IDialogConstants.get().YES_LABEL;
    public static final String NO_LABEL = IDialogConstants.get().NO_LABEL;
    public static final String NO_TO_ALL_LABEL = IDialogConstants.get().NO_TO_ALL_LABEL;
    public static final String YES_TO_ALL_LABEL = IDialogConstants.get().YES_TO_ALL_LABEL;
    public static final String SKIP_LABEL = IDialogConstants.get().SKIP_LABEL;
    public static final String STOP_LABEL = IDialogConstants.get().STOP_LABEL;
    public static final String ABORT_LABEL = IDialogConstants.get().ABORT_LABEL;
    public static final String RETRY_LABEL = IDialogConstants.get().RETRY_LABEL;
    public static final String IGNORE_LABEL = IDialogConstants.get().IGNORE_LABEL;
    public static final String PROCEED_LABEL = IDialogConstants.get().PROCEED_LABEL;
    public static final String OPEN_LABEL = IDialogConstants.get().OPEN_LABEL;
    public static final String CLOSE_LABEL = IDialogConstants.get().CLOSE_LABEL;
    public static final String SHOW_DETAILS_LABEL = IDialogConstants.get().SHOW_DETAILS_LABEL;
    public static final String HIDE_DETAILS_LABEL = IDialogConstants.get().HIDE_DETAILS_LABEL;
    public static final String BACK_LABEL = IDialogConstants.get().BACK_LABEL;
    public static final String NEXT_LABEL = IDialogConstants.get().NEXT_LABEL;
    public static final String FINISH_LABEL = IDialogConstants.get().FINISH_LABEL;
    public static final String HELP_LABEL = IDialogConstants.get().HELP_LABEL;
}
