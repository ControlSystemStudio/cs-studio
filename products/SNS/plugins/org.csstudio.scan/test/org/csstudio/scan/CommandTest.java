package org.csstudio.scan;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.junit.Test;

@SuppressWarnings("nls")
public class CommandTest
{
    @Test
    public void testCommands() throws Exception
    {
        final ScanCommand command = new SetCommand("setpoint", 3.14);
        
        // Use beans to determine properties?
        BeanInfo info = Introspector.getBeanInfo(command.getClass());
        PropertyDescriptor[] properties = info.getPropertyDescriptors();
        for (PropertyDescriptor property : properties)
            System.out.println(property.getDisplayName());
    }
}
