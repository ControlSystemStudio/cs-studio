
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

package org.csstudio.application.weightrequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 01.12.2011
 */
public class ParseResult {
    
    private Double value;
    
    private boolean valueIsMoving;
    
    private boolean validValue;

    public ParseResult() {
        value = Double.NaN;
        validValue = false;
        valueIsMoving = false;
    }
    
    public ParseResult(String doubleValue) {
        
        try {
            DecimalFormat f = new DecimalFormat(",####0.0", new DecimalFormatSymbols(Locale.ENGLISH));
            value = new Double(doubleValue);
            value = new Double(f.format(value));
            validValue = true;
        } catch (NumberFormatException nfe) {
            value = Double.valueOf(0.0D);
            validValue = false;
        }
        
        this.valueIsMoving = false;
    }

    public ParseResult(String doubleValue, boolean isValid) {
        this(doubleValue);
        validValue = isValid;
        valueIsMoving = false;
    }

    public ParseResult(String doubleValue, boolean isValid, boolean isMoving) {
        this(doubleValue, isValid);
        valueIsMoving = isMoving;
    }

    public Double getValue() {
        return value;
    }

    public boolean isValid() {
        return validValue;
    }

    public boolean isValueMoving() {
        return valueIsMoving;
    }
}
