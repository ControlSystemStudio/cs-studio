/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
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

    public static final String OK_LABEL = IDialogConstants.OK_LABEL;
    public static final String CANCEL_LABEL = IDialogConstants.CANCEL_LABEL;
    public static final String YES_LABEL = IDialogConstants.YES_LABEL;
    public static final String NO_LABEL = IDialogConstants.NO_LABEL;
    public static final String NO_TO_ALL_LABEL = IDialogConstants.NO_TO_ALL_LABEL;
    public static final String YES_TO_ALL_LABEL = IDialogConstants.YES_TO_ALL_LABEL;
    public static final String SKIP_LABEL = IDialogConstants.SKIP_LABEL;
    public static final String STOP_LABEL = IDialogConstants.STOP_LABEL;
    public static final String ABORT_LABEL = IDialogConstants.ABORT_LABEL;
    public static final String RETRY_LABEL = IDialogConstants.RETRY_LABEL;
    public static final String IGNORE_LABEL = IDialogConstants.IGNORE_LABEL;
    public static final String PROCEED_LABEL = IDialogConstants.PROCEED_LABEL;
    public static final String OPEN_LABEL = IDialogConstants.OPEN_LABEL;
    public static final String CLOSE_LABEL = IDialogConstants.CLOSE_LABEL;
    public static final String SHOW_DETAILS_LABEL = IDialogConstants.SHOW_DETAILS_LABEL;
    public static final String HIDE_DETAILS_LABEL = IDialogConstants.HIDE_DETAILS_LABEL;
    public static final String BACK_LABEL = IDialogConstants.BACK_LABEL;
    public static final String NEXT_LABEL = IDialogConstants.NEXT_LABEL;
    public static final String FINISH_LABEL = IDialogConstants.FINISH_LABEL;
    public static final String HELP_LABEL = IDialogConstants.HELP_LABEL;
}
