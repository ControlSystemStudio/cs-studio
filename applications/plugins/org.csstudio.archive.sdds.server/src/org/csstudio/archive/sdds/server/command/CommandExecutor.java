
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.archive.sdds.server.command;

import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.util.IntegerValue;
import org.csstudio.archive.sdds.server.util.RawData;

/**
 * @version 1.0
 * @author Markus Moeller
 *
 */
public class CommandExecutor {

    /** */
    private final AbstractServerCommand[] commands;

    /**
     * Standard constructor
     * @throws ServerCommandException
     */
    public CommandExecutor() throws ServerCommandException {

        commands = new AbstractServerCommand[] {
                new VersionServerCommand(),
                new DataRequestServerCommand(),
                new ChannelInfoServerCommand(),
                new ChannelListServerCommand(),
                new HierarchyChannelListServerCommand(),
                new FilterListServerCommand(),
                new RegExpChannelListServerCommand(),
                new SkeletonListServerCommand(),
                new WaveFormDataRequestServerCommand(),
         };
    }

    /**
     * Executes the command with the given number - 1.
     *
     * @param cmd
     * @param buffer
     * @param receivedValue
     * @param resultLength
     * @throws ServerCommandException
     * @throws CommandNotImplementedException
     */
    public void executeCommand(@Nonnull final int cmd,
                               @Nonnull final RawData buffer,
                               @Nonnull final RawData receivedValue,
                               @Nonnull final IntegerValue resultLength)
    throws ServerCommandException, CommandNotImplementedException {
        commands[cmd - 1].execute(buffer, receivedValue, resultLength);
    }
}
