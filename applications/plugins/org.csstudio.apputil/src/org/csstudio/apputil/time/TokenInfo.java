/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

/** Result of getValueOfToken()
 *  @see RelativeTimeParser#getValueOfToken(String, String)
 */
class TokenInfo
{
    /** End of the token in parsed string. */
    private int end;
    
    /** Numeric value of the token */
    private double value;
    
    /** Construct from pieces */
    public TokenInfo(int end, double value)
    {
        super();
        this.end = end;
        this.value = value;
    }
    
    /** @return End of the token in parsed string. */
    public final int getEnd()
    {   return end;  }
    
    /** @return Numeric value of the token */
    public final double getValue()
    {   return value;  }
}

