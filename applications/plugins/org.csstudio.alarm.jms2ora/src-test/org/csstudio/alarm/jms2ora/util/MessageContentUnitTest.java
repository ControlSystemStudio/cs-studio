
package org.csstudio.alarm.jms2ora.util;

import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessageContentUnitTest
{
    @Test
    public void testConstructor()
    {
        ArchiveMessage out = new ArchiveMessage();
        
        assertFalse(out.hasContent());
        assertFalse(out.discard());
    }
}
