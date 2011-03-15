/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 */
 package org.csstudio.archive.archiveRecordInterface;

import org.csstudio.data.values.ISeverity;


/** Implementation of the Severity interface for EPICS samples.
 *  @author Albert Kagarmanov
 */
public class SeverityImpl implements ISeverity
{
	private String text;
	private boolean has_value;
	private boolean txt_stat;
	
	public SeverityImpl(String text, boolean has_value, boolean txt_stat)
	{
		this.text = text;
		this.has_value = has_value;
		this.txt_stat = txt_stat;
	}

	/* @see org.csstudio.archive.Severity#getText() */
	public String getText()
	{
		return text;
	}
    
    /* @see org.csstudio.archive..Severity#isOK() */
    public boolean isOK()
    {
        return text.length() == 0  ||  text.equals("NO_ALARM");
    }

    /* @see org.csstudio.archive.Severity#isMinor() */
    public boolean isMinor()
    {
        return text.equals("MINOR");
    }

    /* @see org.csstudio.archive.Severity#isMajor() */
    public boolean isMajor()
    {
        return text.equals("MAJOR");
    }

    /* @see org.csstudio.archive.Severity#isInvalid() */
    public boolean isInvalid()
    {
        return !hasValue() || text.equals("INVALID");
    }

    /* @see org.csstudio.archive.Severity#hasValue() */
	public boolean hasValue()
	{
		return has_value;
	}

	/* @see org.csstudio.archive.Severity#statusIsText() */
	public boolean statusIsText()
	{
		return txt_stat;
	}
	
	@Override public String toString()
	{
		String result = text;
		if (!has_value)
			text = text + " (no value)";
		if (!txt_stat)
			text = text + " (status is repeat count)";
		return result;
	}
}
