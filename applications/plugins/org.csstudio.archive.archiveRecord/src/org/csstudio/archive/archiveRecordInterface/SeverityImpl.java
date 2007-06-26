package org.csstudio.archive.archiveRecordInterface;

import org.csstudio.platform.data.ISeverity;


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
