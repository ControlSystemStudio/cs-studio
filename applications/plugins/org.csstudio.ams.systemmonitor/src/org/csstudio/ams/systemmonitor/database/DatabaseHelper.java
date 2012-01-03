
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.systemmonitor.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.UserGroupDAO;
import org.csstudio.ams.dbAccess.configdb.UserGroupKey;
import org.csstudio.ams.dbAccess.configdb.UserGroupUserDAO;
import org.csstudio.ams.dbAccess.configdb.UserTObject;

/**
 * @author Markus Moeller
 *
 */
public class DatabaseHelper {
    
    public static String[] getPhoneNumbers(final String groupNames) {
        
        StringTokenizer token = null;
        Connection connection = null;
        ArrayList<UserGroupKey> groupKey = null;
        final Vector<String> number = new Vector<String>();
        String[] result = null;
        String name = null;
        int groupId = 0;

        token = new StringTokenizer(groupNames, ",");

        try
        {
            connection = AmsConnectionFactory.getApplicationDB();
            groupKey = (ArrayList<UserGroupKey>)UserGroupDAO.selectKeyList(connection);

            while(token.hasMoreTokens())
            {
                name = token.nextToken();

                for(final UserGroupKey k : groupKey)
                {
                    if(k.userGroupName.compareTo(name) == 0)
                    {
                        groupId = k.userGroupID;
                        break;
                    }
                }

                final Vector<UserTObject> users = UserGroupUserDAO.selectByGroupAndState(connection, groupId, 1);
                if(users.isEmpty() == false)
                {
                    for(final UserTObject u : users)
                    {
                        if(number.contains(u.getMobilePhone()) == false)
                        {
                            number.add(u.getMobilePhone());
                        }
                    }
                }
            }
        }
        catch(final SQLException sqle)
        {
            connection = null;
            number.clear();
        }
        finally
        {
            if(connection != null)
            {
                AmsConnectionFactory.closeConnection(connection);
            }
        }

        if(number.isEmpty() == false)
        {
            result = new String[number.size()];
            result = number.toArray(result);
        }

        return result;
    }

    public static String[] getEMailAddresses(final String groupNames)
    {
        StringTokenizer token = null;
        Connection connection = null;
        ArrayList<UserGroupKey> groupKey = null;
        final Vector<String> adr = new Vector<String>();
        String[] result = null;
        String name = null;
        int groupId = 0;

        token = new StringTokenizer(groupNames, ",");

        try
        {
            connection = AmsConnectionFactory.getApplicationDB();
            groupKey = (ArrayList<UserGroupKey>)UserGroupDAO.selectKeyList(connection);

            while(token.hasMoreTokens())
            {
                name = token.nextToken();

                for(final UserGroupKey k : groupKey)
                {
                    if(k.userGroupName.compareTo(name) == 0)
                    {
                        groupId = k.userGroupID;
                        break;
                    }
                }

                final Vector<UserTObject> users = UserGroupUserDAO.selectByGroupAndState(connection, groupId, 1);
                if(users.isEmpty() == false)
                {
                    for(final UserTObject u : users)
                    {
                        if(adr.contains(u.getEmail()) == false)
                        {
                            adr.add(u.getEmail());
                        }
                    }
                }
            }
        }
        catch(final SQLException sqle)
        {
            connection = null;
            adr.clear();
        }
        finally
        {
            if(connection != null)
            {
                AmsConnectionFactory.closeConnection(connection);
            }
        }

        if(adr.isEmpty() == false)
        {
            result = new String[adr.size()];
            result = adr.toArray(result);
        }

        return result;
    }
}
