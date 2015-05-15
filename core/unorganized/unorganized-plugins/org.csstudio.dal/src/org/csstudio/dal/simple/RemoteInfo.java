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

package org.csstudio.dal.simple;



/**
 * <p>
 * RemoteInfo class presents target in remote system. It briefly follows URI
 * notation, but does not necessary conform with the specifications.
 * RemoteInfo consists of following basic components:
 * </p>
 * <ul>
 * <li>remote name, by URI notation this is path with authority.
 * RemoteInfo does not try to interpret this part or modify (like URI escaping),
 * but leaves to plug implementation to handle the remote name as pleased.</li>
 * <li>type (connection or plug), by URI notation schema, part before :// sequence.</li>
 * <li>characteristic, by URI notation a fragment, end part of URI divided by # from rest of URI.</li>
 * <li>query, same as by URI notation, used for addressing commands on device.
 * End part of URI divided by ? from rest of URI.</li>
 * </ul>
 *
 * <p>
 * In current DAL implementation only type and remote name are handled by DAL.
 * Characteristic and query part are there for future compatibility.
 * </p>
 *
 * @author Igor Kriznar
 */
public final class RemoteInfo
{
    /**
     * Prefix, which is used for concate URI schema part from plug type name.
     */
    public static final String DAL_TYPE_PREFIX = "DAL-";

    /**
     * Separator betwean URI schema part and rest of remote name.
     */
    public static final String TYPE_SEPARATOR = "://";


    /**
     * If string is URI formated, like connectionType://remoteName#characteristic?query or DAL-plugType://remoteName#characteristic?query,
     * then it is properly parsed into RemoteInfo.
     *
     *
     * @param name remote name or URI
     * @return new remote info
     */
    public static RemoteInfo fromString(String name) {
        return fromString(name,null);
    }

    /**
     * @see RemoteInfo#fromString(String)
     * @deprecated use fromString(String)
     */
    @Deprecated
    public static RemoteInfo remoteInfoFromString(String name) {
        return fromString(name,null);
    }

    /**
     * @see RemoteInfo#fromString(String, String)
     * @deprecated use fromString(String, String)
     */
    @Deprecated
    public static RemoteInfo remoteInfoFromString(String name, String defaultType) {
        return fromString(name, defaultType);
    }

    /**
     * If string is URI formated, like connectionType://remoteName#characteristic?query or DAL-plugType://remoteName#characteristic?query,
     * then it is properly parsed into RemoteInfo.
     *
     *
     * @param name remote name or URI
     * @param defaultType default connection type
     * @return new remote info
     */
    public static RemoteInfo fromString(String name, String defaultType) {
        final String type, remoteName;
        String characteristic = null, query = null;
        int sep = name.indexOf('?');
        if (sep > 0) {
            if (sep < name.length()-1) {
                query = name.substring(sep+1);
            }
            name = name.substring(0, sep);
        }
        sep = name.indexOf('#');
        if (sep > 0) {
            if (sep < name.length()-1) {
                characteristic = name.substring(sep+1);
            }
            name = name.substring(0, sep);
        }
        sep = name.indexOf(TYPE_SEPARATOR);
        if (sep > 0)
        {
            type = name.substring(0, sep);
            remoteName = name.substring(sep+TYPE_SEPARATOR.length());
        }
        else
        {
            type = defaultType;
            remoteName = name;
        }

        return new RemoteInfo(type, remoteName, characteristic, query);
    }



    private final String connectionType;

    private final String remoteName;

    private final String characteristic;

    private final String query;

    /**
     * Creates a new remote info with all provided name components.
     *
     * @param connectionType type of remote info, may be formated like "DAL-PLUG" or just "PLUG",
     * @param remoteName name of remote target, must not be <code>null</code>
     * @param characteristic name of characteristic, can be <code>null</code>
     * @param query query part of remote name, can be <code>null</code>
     *
     * @throws NullPointerException if remote name is null
     */
    public RemoteInfo(String connectionType, String remoteName, String characteristic, String query)
    {
        if (remoteName == null) {
            throw new NullPointerException("The remoteName is null.");
        }

        this.connectionType = connectionType;
        this.remoteName = remoteName;
        this.characteristic = characteristic;
        this.query = query;
    }

    /**
     * Creates a new remote info with all provided name components.
     *
     * @param connectionType type of remote info, may be formated like "DAL-PLUG" or just "PLUG",
     * @param remoteName name of remote target, must not be <code>null</code>
     *
     * @throws NullPointerException if remote name is null
     */
    public RemoteInfo(String connectionType, String remoteName)
    {
        this(connectionType,remoteName,null,null);
    }


    /**
     * Returns the remote name part of this remote info.
     *
     * @return the name part of the remote info
     */
    public String getRemoteName()
    {
        return remoteName;
    }

    /**
     * Returns the connection type name of this RemoteInfo, corresponds to schema part of URI specifications.
     *
     * @return the connection type name of this RemoteInfo, or <code>null</code> if type is undefined.
     */
    public String getConnectionType()
    {
        return connectionType;
    }

    /**
     * Returns the DAL plug name of this RemoteInfo.
     * DAL Plug type is defined trough connection type, which begins with 'DAL-'.
     * Plug type corresponds to connection type stripped of the 'DAL-' part.
     * If connection type does not begins with 'DAL-', then plus type is same as connection type.
     *
     * @return the DAL plug name of this RemoteInfo, or <code>null</code> if type is <code>null</code> or does not conform to DAL plug type declaration
     */
    public String getPlugType()
    {
        if ((connectionType !=null) && (connectionType.length()>DAL_TYPE_PREFIX.length()) && connectionType.toUpperCase().startsWith(DAL_TYPE_PREFIX)) {
            return connectionType.substring(DAL_TYPE_PREFIX.length());
        }
        return connectionType;
    }

    /**
     * Returns the query part of this remote info if it exists.
     *
     * @return query or <code>null</code>
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * Returns the characteristic part of this remote info if it exists.
     *
     * @return characteristic or <code>null</code>
     */
    public String getCharacteristic() {
        return characteristic;
    }

    @Override
    public String toString() {
        if ((connectionType==null) && (characteristic==null) && (query==null)) {
            return remoteName;
        }

        StringBuilder sb= new StringBuilder(128);
        if (connectionType!=null) {
            sb.append(connectionType);
            sb.append(TYPE_SEPARATOR);
        }
        sb.append(remoteName);
        if (characteristic!=null) {
            sb.append('#');
            sb.append(characteristic);
        }
        if (query!=null) {
            sb.append('?');
            sb.append(query);
        }
        return sb.toString();
    }


}
/* __oOo__ */
