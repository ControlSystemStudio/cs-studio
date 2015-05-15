package com.cosylab.vdct.vdb;

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

import com.cosylab.vdct.inspector.*;
/**
 * Insert the type's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @author
 */
public class ROProperty implements InspectableProperty, ChangableVisibility {

    protected boolean allowVisibilityChange = false;
    protected InspectableProperty property = null;

/**
 * GUISeparator constructor comment.
 */
public ROProperty(InspectableProperty property) {
    this(property, false);
}

/**
 * GUISeparator constructor comment.
 */
public ROProperty(InspectableProperty property, boolean allowVisibilityChange) {
    this.property = property;
    this.allowVisibilityChange = allowVisibilityChange;
}

/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean allowsOtherValues() {
    return property.allowsOtherValues();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:02:40)
 * @return java.lang.String
 */
public java.lang.String getHelp() {
    return property.getHelp();
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String
 */
public String getName() {
    return property.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
    return property.getSelectableValues();
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return java.lang.String
 */
public String getValue() {
    return property.getValue();
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getInitValue()
{
    return property.getInitValue();
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean isEditable() {
    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @return boolean
 */
public boolean isSepatator() {
    return property.isSepatator();
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 22:40:34)
 * @param value java.lang.String
 */
public void setValue(String value) {}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.util.regex.Pattern
 */
public Pattern getEditPattern()
{
    return property.getEditPattern();
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 */
public String getToolTipText()
{
    return property.getToolTipText();
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isValid()
{
    return property.isValid();
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @return int
 */
public int getVisibility()
{
    return property.getVisibility();
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @param java.awt.Component
 * @param x
 * @param y
 */
public void popupEvent(Component component, int x, int y)
{
    // this is only RO
}

/**
 * @see com.cosylab.vdct.inspector.ChangableVisibility#setVisibility(int)
 */
public void setVisibility(int visibility)
{
    if (allowVisibilityChange && property instanceof ChangableVisibility)
        ((ChangableVisibility)property).setVisibility(visibility);
}

}
