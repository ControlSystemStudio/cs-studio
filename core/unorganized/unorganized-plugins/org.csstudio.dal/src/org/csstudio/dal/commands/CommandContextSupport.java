/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.commands;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A Simple CommandContext implementation.
 *
 * @author Blaz Hostnik
 */
public class CommandContextSupport implements CommandContext
{
    private List<Command> commands = new ArrayList<Command>();
    private List<String> names = new ArrayList<String>();

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.commands.CommandContext#getCommands()
     */
    public Command[] getCommands()
    {
        return commands.toArray(new Command[commands.size()]);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.CommandContext#getCommand(java.lang.String)
     */
    public Command getCommand(String name)
    {
        Iterator<Command> ite = commands.iterator();
        Command com;

        while (ite.hasNext()) {
            com = ite.next();

            if (name.equals(com.getName())) {
                return com;
            }
        }

        return null;
    }

    /**
     * Creates new command, based on specified method, and adds it to context.
     *
     * @param host on it, method will be called.
     * @param method new command will call it.
     */
    public void addCommand(Object host, Method method)
    {
        commands.add(new CommandSupport(this, host, method));
        names.add(method.getName());
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.commands.CommandContext#getCommandNames()
     */
    public String[] getCommandNames()
    {
        return names.toArray(new String[names.size()]);
    }
}

/* __oOo__ */
