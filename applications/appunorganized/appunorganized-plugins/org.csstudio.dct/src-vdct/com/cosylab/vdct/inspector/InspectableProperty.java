package com.cosylab.vdct.inspector;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Component;
import java.util.regex.Pattern;

/**
 * Insert the type's description here.
 * Creation date: (11.1.2001 21:28:00)
 * @author Matej Sekoranja
 */
public interface InspectableProperty {

    public static final int NON_DEFAULT_VISIBLE = 0;
    public static final int ALWAYS_VISIBLE = 1;
    public static final int NEVER_VISIBLE = 2;
    public static final int UNDEFINED_VISIBILITY = Integer.MAX_VALUE;

/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:34:58)
 * @return boolean
 */
public boolean allowsOtherValues();
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:01:28)
 * @return java.lang.String
 */
public String getHelp();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:54:12)
 * @return java.lang.String
 */
public String getName();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:36:10)
 * @return java.lang.String[]
 */
public String[] getSelectableValues();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getValue();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getInitValue();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isEditable();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isValid();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:44:32)
 * @return boolean
 */
public boolean isSepatator();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @param value java.lang.String
 */
public void setValue(String value);
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @return java.lang.String
 */
public Pattern getEditPattern();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @return java.lang.String
 */
public String getToolTipText();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @return int
 */
public int getVisibility();
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @param java.awt.Component
 * @param x
 * @param y
 */
public void popupEvent(Component component, int x, int y);
}
