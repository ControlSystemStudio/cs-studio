/**
 *
 */
package org.csstudio.utility.channel.actions;

import java.util.Arrays;

import gov.bnl.channelfinder.api.Channel;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Property.Builder.property;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
/**
 * A UI testing class to observe the behavior of the ChannelEditDialog
 *
 * @author Kunal Shroff
 *
 */
public class ChannelEditDialogDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        Channel channel = channel("test-channel")
                .with(property("test-propertyA", "test-value"))
                .with(property("test-propertyB", "test-value"))
                .with(tag("test-tagA")).build();
        ChannelEditDialog channelEditDialog = new ChannelEditDialog(shell,
                channel,
                Arrays.asList("test-tagA", "test-tagB", "test-tagC"),
                Arrays.asList("test-propertyA", "test-propertyB", "test-propertyC"));
        channelEditDialog.open();
    }

}
