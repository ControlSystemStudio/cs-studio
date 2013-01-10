
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

package org.csstudio.ams.application.monitor.message;

/**
 * @author mmoeller
 * @version 1.0
 * @since 12.04.2012
 */
public class DeliveryWorkerAnswerMessage extends AbstractCheckMessage implements IAnswerMessage {

    private static final long serialVersionUID = 1L;
    
    private String _text;
    private String _value;
    
    public DeliveryWorkerAnswerMessage() {
        super();
    }

    @Override
    public synchronized boolean isAnswerForMessage(InitiatorMessage origin) {
        
        boolean isEqual = origin.getTypeValue().equals(this.getTypeValue());
        
        if (!isEqual || !this.getNameValue().equals(MessagePropertyValue.AMS_SYSTEM_CHECK_ANSWER)) {
            isEqual = false;
        }

        if (!isEqual || !origin.getClassValue().equals(this.getClassValue())) {
            isEqual = false;
        }

        if (!isEqual || !this.getApplicationIdValue().equals(MessagePropertyValue.SMS_DELIVERY_WORKER)) {
            isEqual = false;
        }

        if (!isEqual || !this.getDestinationValue().equals(MessagePropertyValue.AMS_SYSTEM_MONITOR)) {
            isEqual = false;
        }
        
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( (_text == null) ? 0 : _text.hashCode());
        result = prime * result + ( (_value == null) ? 0 : _value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeliveryWorkerAnswerMessage other = (DeliveryWorkerAnswerMessage) obj;
        if (_text == null) {
            if (other._text != null)
                return false;
        } else if (!_text.equals(other._text))
            return false;
        if (_value == null) {
            if (other._value != null)
                return false;
        } else if (!_value.equals(other._value))
            return false;
        return true;
    }

    public String getTextValue() {
        return _text;
    }

    public void setTextValue(String text) {
        _text = text;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }
}
