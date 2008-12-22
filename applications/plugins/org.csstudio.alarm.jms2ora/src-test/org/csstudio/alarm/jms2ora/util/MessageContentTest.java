
package org.csstudio.alarm.jms2ora.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageContentTest
{
    @Test
    public void testConstructor()
    {
        MessageContent out = new MessageContent();
        
        assertFalse(out.hasContent());
        assertFalse(out.discard());
    }
}
