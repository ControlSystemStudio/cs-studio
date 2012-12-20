
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.action;

import java.util.ArrayList;

/**
 * 1. Reply alarm chain message
 * Message format: "<ChainIdAndPos>*<ConfirmCode>"
 * Example: "12345001*123"
 *          "#*123" - All message chain entries for the user will be answered
 *          "1234*adg" - Translate the handy codes to numbers (?)
 * 
 * 2. Change status of user in group
 * Message format without reason text: "<GroupNum>*<UserNum>*<Status>*<StatusCode>"
 * Example: "12*34*1*123"
 * 
 * Message format with reason text: "<GroupNum>*<UserNum>*<Status>*<StatusCode>*<Reason>"
 * Example: "12*34*0*123*I'm ill"
 *
 * 3. Change status of group
 * Message format without reason text: "G*<GroupNum>*<UserNum>*<Status>*<StatusCode>"
 * Example: "G*12*34*1*123" 
 *          "g*12*34*1*123"
 * 
 * Message format with reason text: "<GroupNum>*<UserNum>*<Status>*<StatusCode>*<Reason>"
 * Example: "G*12*34*0*123*I'm ill"
 *          "g*12*34*0*123*I'm ill"
 *
 * @author mmoeller
 * @version 1.0
 * @since 02.01.2012
 */
public class AmsUserAction {
    
    protected ActionFormat actionFormat;
    
    protected long chainId;
    
    protected long groupId;
    
    protected long userId;
    
    protected long status;
    
    protected String confirmCode;
    
    protected String reason;
    
    public AmsUserAction(String text) {
        actionFormat = ActionFormat.INVALID_FORMAT;
        chainId = 0L;
        groupId = 0L;
        userId = 0L;
        status = -1L;
        confirmCode = null;
        reason = null;
        parse(text);
    }
    
    private void parse(String text) {
        
        if (text == null) {
            return;
        }
        
        String frm = text.trim();
        if (frm.length() == 0) {
            return;
        }
        
        String[] parts = text.split("\\*");
        if (parts.length < 2 || parts.length > 6) {
            return;
        }
        
        if (parts.length == 2) {
            checkReplyFormat(parts);
            return;
        }
        
        checkStatusChangeFormat(parts);
    }
    
    private void checkReplyFormat(String[] parts) {
        
        parts[0] = parts[0].trim();
        
        try {
            if (!parts[0].equals("#")) {
                chainId = Long.parseLong(parts[0]);
            } else {
                // Indicates that all chain messages have to be answered
                chainId = -1000L;
            }
            confirmCode = this.handyButton2NumberMode(parts[1]);
            actionFormat = ActionFormat.REPLY_ALARM;
        } catch (NumberFormatException nfe) {
            chainId = 0L;
            confirmCode = null;
        }
    }
    
    private void checkStatusChangeFormat(String[] parts) {
        
        ArrayList<String> part = new ArrayList<String>();
        int startIndex = 0;
        
        if (parts[0].compareToIgnoreCase("G") == 0) {
            actionFormat = ActionFormat.CHANGE_GROUP_STATUS;
            startIndex = 1;
        } else {
            actionFormat = ActionFormat.CHANGE_USER_STATUS;
        }
        
        int newIndex = 0;
        for (int i = startIndex; i < parts.length; i++) {
            if (parts[i] != null) {
                part.add(newIndex++, parts[i].trim());
            }
        }

        if (part.size() < 4) {
            actionFormat = ActionFormat.INVALID_FORMAT;
            return;
        }
        
        try {
            groupId = Long.parseLong(part.get(0));
            userId = Long.parseLong(part.get(1));
            status = Long.parseLong(part.get(2));
            confirmCode = part.get(3);
            if (part.size() == 5) {
                reason = part.get(4);
            } else {
                reason = "";
            }
        } catch (NumberFormatException nfe) {
            actionFormat = ActionFormat.INVALID_FORMAT;
            groupId = 0L;
            userId = 0L;
            status = -1L;
            confirmCode = null;
            reason = null;
        }
    }
    
    public long getChainId() {
        return chainId;
    }
    
    public String getChainIdAsString() {
        String result = null;
        if (chainId < 0) {
            result = "#";
        } else {
            result = String.valueOf(chainId);
        }
        
        return result;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupIdAsString() {
        return String.valueOf(groupId);
    }

    public long getUserId() {
        return userId;
    }

    public String getUserIdAsString() {
        return String.valueOf(userId);
    }

    public long getStatus() {
        return status;
    }

    public String getStatusAsString() {
        return String.valueOf(status);
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public String getReason() {
        return reason;
    }

    public boolean hasValidFormat() {
        return (actionFormat != ActionFormat.INVALID_FORMAT);
    }
    
    public boolean isReplyAlarmAction() {
        return (actionFormat == ActionFormat.REPLY_ALARM);
    }
    
    public boolean isChangeUserAction() {
        return (actionFormat == ActionFormat.CHANGE_USER_STATUS);
    }

    public boolean isChangeGroupAction() {
        return (actionFormat == ActionFormat.CHANGE_GROUP_STATUS);
    }
    
    private String handyButton2NumberMode(String str) {
        
        String result = "";
        byte[] b = str.toLowerCase().getBytes();

        for (int i = 0; i < b.length; i++) {
            switch (b[i]) {
                default:
                    result += (b[i] - 48);
                    break;
                case '.':
                case ' ':
                    result += "1"; 
                    break;
                case 'a':
                case 'b':
                case 'c':
                    result += "2"; 
                    break;
                case 'd':
                case 'e':
                case 'f':
                    result += "3"; 
                    break;
                case 'g':
                case 'h':
                case 'i':
                    result += "4"; 
                    break;
                case 'j':
                case 'k':
                case 'l':
                    result += "5"; 
                    break;
                case 'm':
                case 'n':
                case 'o':
                    result += "6"; 
                    break;
                case 'p':
                case 'q':
                case 'r':
                case 's':
                    result += "7"; 
                    break;
                case 't':
                case 'u':
                case 'v':
                    result += "8"; 
                    break;
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    result += "9"; 
                    break;
            }
        }
        
        return result;
    }
}
