/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.swt.graphics.RGB;
import org.diirt.vtype.AlarmSeverity;

/**The scheme for alarm color which provide unified colors and borders for alarms.
 * @author Xihui Chen
 * @author Takashi Nakamoto - implemented getAlarmColor() method.
 */
public class AlarmRepresentationScheme {


    public static final int ALARM_BORDER_WIDTH = 2;
    public static final String MAJOR = "Major"; //$NON-NLS-1$
    public static final String MINOR = "Minor"; //$NON-NLS-1$
    public static final String INVALID = "Invalid"; //$NON-NLS-1$
    public static final String DISCONNECTED = "Disconnected"; //$NON-NLS-1$

    private static final AbstractBorder DISCONNECT_BORDER = BorderFactory.createBorder(
            BorderStyle.TITLE_BAR, 1, AlarmRepresentationScheme.getDisconnectedColor(),
            "Disconnected");

    /**
     * Returns color of alarm severity.
     * @param severity
     * @return RGB color of the given alarm severity. Null if alarm severity is "OK".
     */
    public static RGB getAlarmColor(AlarmSeverity severity) {
        switch (severity) {
        case MAJOR:
            return getMajorColor();
        case NONE:
            return null;
        case MINOR:
            return getMinorColor();
        case INVALID:
        case UNDEFINED:
        default:
            return getInValidColor();
        }
    }

    public static RGB getMajorColor(){
        return MediaService.getInstance().getColor(MAJOR);
    }

    public static RGB getMinorColor(){
        return MediaService.getInstance().getColor(MINOR);
    }

    public static RGB getInValidColor(){
        return MediaService.getInstance().getColor(INVALID);
    }

    public static RGB getDisconnectedColor(){
        return MediaService.getInstance().getColor(DISCONNECTED);
    }

    public static Border getMajorBorder(BorderStyle borderStyle){
        BorderStyle newBorderStyle = getNewBorderStyle(borderStyle);
        return BorderFactory.createBorder(newBorderStyle, ALARM_BORDER_WIDTH, getMajorColor(), ""); //$NON-NLS-1$
    }

    private static BorderStyle getNewBorderStyle(BorderStyle borderStyle) {
        BorderStyle newBorderStyle = BorderStyle.LINE;
        switch (borderStyle) {
        case DASH_DOT:
        case DASH_DOT_DOT:
        case DASHED:
        case DOTTED:
            newBorderStyle = borderStyle;
            break;
        default:
            break;
        }
        return newBorderStyle;
    }

    public static Border getMinorBorder(BorderStyle borderStyle){
        BorderStyle newBorderStyle = getNewBorderStyle(borderStyle);

        return BorderFactory.createBorder(newBorderStyle, ALARM_BORDER_WIDTH, getMinorColor(), ""); //$NON-NLS-1$
    }

    public static Border getInvalidBorder(BorderStyle borderStyle){
        BorderStyle newBorderStyle = getNewBorderStyle(borderStyle);

        return BorderFactory.createBorder(newBorderStyle, ALARM_BORDER_WIDTH, getInValidColor(), ""); //$NON-NLS-1$
    }

    public static Border getDisonnectedBorder(){
        return DISCONNECT_BORDER;
    }
}
