
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

package org.csstudio.ams.application.monitor.status;

import java.io.Serializable;

/**
 * @author mmoeller
 * @version 1.0
 * @since 13.04.2012
 */
public class CheckStatusInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private long timestamp;
    
    private CheckStatus checkStatus;
    
    private ErrorReason errorReason;
    
    private String text;
    
    public CheckStatusInfo() {
        timestamp = System.currentTimeMillis();
        checkStatus = CheckStatus.UNDEFINED;
        errorReason = ErrorReason.UNDEFINED;
        text = null;
    }
    
    public CheckStatusInfo(CheckStatus status, ErrorReason error) {
        this();
        this.checkStatus = status;
        this.errorReason = error;
    }

    public CheckStatusInfo(long time, CheckStatus status, ErrorReason error) {
        this.timestamp = time;
        this.checkStatus = status;
        this.errorReason = error;
    }

    public CheckStatusInfo(long time, CheckStatus status, ErrorReason error, String msg) {
        this.timestamp = time;
        this.checkStatus = status;
        this.errorReason = error;
        this.text = msg;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("CheckStatusInfo {");
        result.append(timestamp + ",");
        result.append(checkStatus.toString() + ",");
        result.append(errorReason + ",");
        if (hasErrorText()) {
            result.append(text);
        } else {
            result.append("NONE");
        }
        result.append("}");
        return result.toString();
    }
    
    public void setTimestamp(long time) {
        timestamp = time;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public CheckStatus getCheckStatus() {
        return checkStatus;
    }
    
    public void setCheckStatus(CheckStatus status) {
        checkStatus = status;
    }
    
    public void setErrorReason(ErrorReason reason) {
        errorReason = reason;
    }
    
    public ErrorReason getErrorReason() {
        return errorReason;
    }
    
    public boolean hasErrorText() {
        return (text != null);
    }
    
    public void setErrorText(String t) {
        text = t;
    }
    
    public String getErrorText() {
        return text;
    }
}
