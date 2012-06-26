
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.nams.application.department.decision.management;

import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;

/**
 * @author mmoeller
 * @version 1.0
 * @since 22.03.2012
 */
public class Restart implements IManagementCommand {
    
    private static String ACTION_LOGIN_FAILED = "ERROR: [3] - Possible hacking attempt: XMPP-remote-login: Authorization failed! (no details avail)"
    + " [requested action: \"restarting\"]";
    
    private static String ACTION_LOGIN_SUCCEDED = "OK: [0] - Login succeded for user "
    + "ams-department-decision"
    + ", stopping has been initiated [requested action: \"restarting\"]";

    static final String ADMIN_PASSWORD = "admin4AMS";

    private static ILogger logger;

    private static RemotelyStoppable thingToBeRestarted;

    /**
     * Constructor.
     */
    public Restart() {
        if (Restart.logger == null) {
            throw new RuntimeException(
                    "Class has not been intialized. Expected call of staticInject(Logger) before instantation.");
        }
        if (Restart.thingToBeRestarted == null) {
            throw new RuntimeException(
                    "Class has not been intialized. Expected call of staticInject(RemotelyStoppable) before instantation.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(CommandParameters parameters) {
        
        String param = (String) parameters.get("Password");
        String password = thingToBeRestarted.getPassword();
        
        if(password.length() > 0) {
            if(param.equals(password)) {
                Restart.thingToBeRestarted.restartRemotly(Restart.logger);
                Restart.logger.logInfoMessage(this, Restart.ACTION_LOGIN_SUCCEDED);
                return CommandResult.createMessageResult(Restart.ACTION_LOGIN_SUCCEDED);
            }
        } else {
            Restart.thingToBeRestarted.restartRemotly(Restart.logger);
            Restart.logger.logInfoMessage(this, Restart.ACTION_LOGIN_SUCCEDED);
            return CommandResult.createMessageResult(Restart.ACTION_LOGIN_SUCCEDED);
        }
        
        Restart.logger.logWarningMessage(this, Restart.ACTION_LOGIN_FAILED);
        return CommandResult.createMessageResult(Restart.ACTION_LOGIN_FAILED);
    }
    
    /**
     * Injection of logger. Note: This method have to be called before any
     * instance of this class is created!
     */
    public static void staticInject(final ILogger log) {
        Restart.logger = log;
    }

    /**
     * Injection of stoppable thing. Note: This method have to be called before
     * any instance of this class is created!
     */
    public static void staticInject(final RemotelyStoppable thingToBeStopped) {
        Restart.thingToBeRestarted = thingToBeStopped;
    }
}
