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

import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.util.StringUtils;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @author
 */
public class CommentProperty implements com.cosylab.vdct.inspector.InspectableProperty {
    Commentable record;
    private static String helpString = "\""+com.cosylab.vdct.db.DBConstants.commentString+"\" will be added automatically";
/**
 * CommentProperty constructor comment.
 */
public CommentProperty(Commentable record) {
    this.record=record;
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
private String addCommentChars(String str) {
    if (str==null || str.length()==0) return str;

    final char eofChar = '\n';
    final String space = " ";
    StringBuffer output = new StringBuffer("");
    int pos;

    str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();

    pos = str.indexOf(eofChar);
    while (pos>=0) {
        output.append(com.cosylab.vdct.db.DBConstants.commentString).append(space).append(StringUtils.removeBegining(str.substring(0, pos).trim(), com.cosylab.vdct.db.DBConstants.commentString).trim()).append(eofChar);
        str = str.substring(pos+1);
        pos = str.indexOf(eofChar);
    }

    str = com.cosylab.vdct.db.DBConstants.commentString+space+StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
    output.append(str);

    return output.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean allowsOtherValues() {
    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getHelp() {
    return helpString;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getName() {
    return record.getName()+".comment";
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String[]
 */
public java.lang.String[] getSelectableValues() {
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return java.lang.String
 */
public String getValue() {
    return removeCommentChars(record.getComment());
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:29:48)
 * @return java.lang.String
 */
public String getInitValue()
{
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean isEditable() {
    return true;
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @return boolean
 */
public boolean isSepatator() {
    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
private String removeCommentChars(String str) {
    if (str==null || str.length()==0) return str;

    final char eofChar = '\n';
    StringBuffer output = new StringBuffer("");
    int pos;

    str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();

    pos = str.indexOf(eofChar);
    while (pos>=0) {
        output.append(StringUtils.removeBegining(str.substring(0, pos).trim(), com.cosylab.vdct.db.DBConstants.commentString).trim()).append(eofChar);
        str = str.substring(pos+1);
        pos = str.indexOf(eofChar);
    }

    str = StringUtils.removeBegining(str.trim(), com.cosylab.vdct.db.DBConstants.commentString).trim();
    output.append(str);

    return output.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:51:20)
 * @param value java.lang.String
 */
public void setValue(String value) {
    String newValue = addCommentChars(value);

    if ((record.getComment()==null) || !record.getComment().equals(newValue))

        // do not store undo for <null> -> ""
        if (!(record.getComment()==null && (newValue==null || newValue.length()==0)))
            com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
                new com.cosylab.vdct.undo.CommentChangeAction(this, record.getComment(), newValue)
        );

    record.setComment(newValue);
}

/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.util.regex.Pattern
 */
public Pattern getEditPattern()
{
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (24/8/99 15:29:04)
 * @return java.lang.String
 */
public String getToolTipText()
{
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:28:51)
 * @return boolean
 */
public boolean isValid()
{
    return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11.1.2001 21:30:04)
 * @return int
 */
public int getVisibility()
{
    return InspectableProperty.UNDEFINED_VISIBILITY;
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
}

}
