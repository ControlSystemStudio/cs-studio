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

package org.epics.css.dal.simulation;

import java.net.URI;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;

import com.cosylab.naming.Escaper;
import com.cosylab.naming.URIName;


/**
 * <code>RemoteInfo</code> interface represents the target in the remote
 * layer, be it an action, a query, a namespace etc. The most important
 * feature of the <code>RemoteInfo</code> implementation is to produce a
 * consistent representation of that remote target. In other words,  target
 * can be represented as an DAL URI, for instance<p><b>DAL-Simulator://manager/PSBEND_M.01/current</b></p>
 *  or it may be represented as a JNDI <code>javax.naming.Name</code>
 * sequence:<p>[0]=DAL-Simulator [1]=manager [2]=PSBEND_M.01 [3]=current</p>
 *  or it may be a simple string. The default implementation allows all
 * these conversions and is able to construct a new name from components like
 * <code>remoteName</code>, URI <code>authority</code>, <code>plugType</code>
 * and so on. For detailed documentation see <code>RemoteInfo</code> interface
 * javadoc.<p>This implementation buffers the <code>URI</code>, so once it
 * has been created for the first time, the invocations of
 * <code>toURI()</code> return immediately. Moreover, this class implements
 * <code>javax.naming.Name</code> directly.</p>
 *  <p>As an instance of <code>javax.naming.Name</code> this instance is
 * unmodifiable, i.e. all methods adding or removing a name component will
 * fail with an exception.</p>
 *  <p>Basically this RemoteInfo is constructed from following parts: plug
 * type, authority, connection name and query. Plug type and connection name
 * is obligatory. This RI implementation will try to use them unmodified as
 * thay are defined in constructors. To enshure this, they must not contain
 * directry escape characters (/ and ?), if they are not part of hirachical
 * structure. Use constructor with directory Name and Bindings or convenience
 * static methods to define RI parts when they need to be escaped for special
 * characters. RI parts will be escaped when they are transformed to Nane and
 * URI representation.</p>
 *
 * @author Gasper Tkacik
 */
public class RemoteInfo extends URIName
{
	private static final long serialVersionUID = 5208467218927145986L;

	private static final Escaper remoteInfoEscaper = new Escaper(new char[]{
			    '/', '?'
		    });

	/** Prefix, which is used for concate URI schema part from plug type name. */
	public static final String SCHEME_PREFIX = "DAL-";

	/**
	 * Takes a single escaped part of an URI and unescapes it to reveal
	 * the true name component. The string parameter should not contain any
	 * special characters (such as '/', ':', '?', ';', '#' etc.) except for
	 * the '%' character which should be used for escaping only.  Example:
	 * "Device%203%2F1" -&gt; "Device 3/1"
	 *
	 * @param escapedPart the String that should be escaped
	 *
	 * @return String which can be used to form an URI
	 */
	public static String unescapePartOfURI(String escapedPart)
	{
		String ret = URI.create("DAL:///" + escapedPart).getPath().substring(1);

		return ret;
	}

	private RemoteInfo()
	{
		//
	}

	/**
	 * Creates a new remote info object from the name of the remote entity, the
	 * authority and the plug type. This constructor will usually be used to
	 * name connectable entities. All string are expected to be encoded for
	 * nonhierarcical use of '/' or '?' characters.
	 *
	 * @param name the name of the connectable entity; this name may contain
	 *        slash hierarchical separators, or end with query part, separated
	 *        with '?' from remote name part; non-<code>null</code>
	 * @param authority an optional namespace authority (such as a name server,
	 *        context server, device manager etc); i.e. that is the entity in
	 *        the scope of which the <code>remoteName</code> becomes a unique
	 *        remote target designation; may be <code>null</code> if the plug
	 *        can either provide a default value or the system runs on top of
	 *        a global namespace
	 * @param plugType the name of the plug under which this remote info is
	 *        being issued; this is the plug where the remote target runs;
	 *        non-<code>null</code>
	 *
	 * @throws NamingException if name creation fails
	 * @throws NullPointerException if name or plug type is <code>null</code>.
	 */
	public RemoteInfo(String name, String authority, String plugType)
		throws NamingException
	{
		super();
		assert (name != null);
		assert (plugType != null);

		if (name == null) {
			throw new NullPointerException("name");
		}

		if (plugType == null) {
			throw new NullPointerException("plugType");
		}

		String query = null;
		int i = name.lastIndexOf('?');

		if (i > -1) {
			query = name.substring(i + 1);
			name = name.substring(0, i);
		}

		initialize(SCHEME_PREFIX + plugType, authority, name, query);
	}

	/**
	 * Creates new RemoteInfo from URI instance.
	 *
	 * @param uri remote URI
	 */
	public RemoteInfo(URI uri)
	{
		super(uri);
	}

	/**
	 * Creates new RemoteInfo from URI instance.
	 *
	 * @param uri URI based implementation ov Name
	 */
	public RemoteInfo(URIName uri)
	{
		super(uri);
	}

	/**
	 * Creates new RemoteInfo by parsing URI string.
	 *
	 * @param uri URI string
	 *
	 * @throws NamingException if creation fails
	 */
	public RemoteInfo(String uri) throws NamingException
	{
		super(uri);
	}

	/**
	 * Creates a new remote info by appending both a hierarchical component and
	 * finally a query to the given <code>remoteName</code>. This constructor
	 * is used internally by the <code>create...()</code> methods of this
	 * class.
	 *
	 * @param remoteName the name of the connectable for which this remote info
	 *        is to be created, may be hierarchical with slash separators
	 * @param query the query to be added right after the
	 *        <code>component</code> part
	 * @param authority authority to use with this remote info, may be
	 *        <code>null</code> if the plug will determine authority or if a
	 *        global namespace is used
	 * @param plugType the type of the plug in the scope of which this info is
	 *        going to be issued / valid, non-<code>null</code>
	 *
	 * @throws NamingException if creation fails
	 * @throws NullPointerException if plug type is null
	 */
	public RemoteInfo(String remoteName, String query, String authority,
	    String plugType) throws NamingException
	{
		super();
		assert (plugType != null);

		if (plugType == null) {
			throw new NullPointerException("plugType");
		}

		initialize(SCHEME_PREFIX + plugType, authority, remoteName, query);
	}

	/**
	 * Creates a new remote info by appending both a hierarchical component and
	 * finally a query to the given <code>remoteName</code>. This constructor
	 * is used internally by the <code>create...()</code> methods of this
	 * class.
	 *
	 * @param parent name, from which to copy contents; non-<code>null</code>
	 * @param component the part of name that will be appended right after the
	 *        <code>remoteName</code>, without slashes, non-<code>null</code>
	 * @param query the query to be added right after the
	 *        <code>remoteName</code> part
	 *
	 * @throws InvalidNameException if creation fails
	 */
	public RemoteInfo(RemoteInfo parent, String component, String query)
		throws InvalidNameException
	{
		super();
		initialize(0, parent.isWithQuery() ? parent.size() - 1 : parent.size(),
		    parent.components, parent.relative, parent.withAuthority,
		    parent.withQuery);

		if (component != null) {
			super.add(component);
		}

		if (query != null) {
			super.add(query);
			setWithQuery(true);
		}
	}

	/**
	 * Returns the name part of this remote info: the remote name with
	 * the query. This is the part of URI without the schema and authority.
	 *
	 * @return the name part of the remote info
	 */
	public String getName()
	{
		return extractName(toURI());
	}

	/**
	 * Returns the remote name part of this remote info. This is the
	 * part of URI without the schema, authority and query.
	 *
	 * @return the name part of the remote info
	 */
	public String getRemoteName()
	{
		return extractRemoteName(toURI());
	}

	/**
	 * Returns the plug name by which this remote info has been
	 * instantiated.
	 *
	 * @return name of the plug that instantiated this remote info
	 */
	public String getPlugType()
	{
		return extractPlugType(toURI());
	}

	/**
	 * Creates a new remote info instance by appending a new path
	 * component part into the name. For example, "abeans-Simulator:///PSBEND"
	 * plus "current" will result in "abeans-Simulator:///PSBEND/current".
	 * Quary part is not included in new RemoteInfo.
	 *
	 * @param componentName the name to append into the pathcomponent URI part,
	 *        non-<code>null</code>
	 *
	 * @return a new remote info instance with appended path
	 */
	public RemoteInfo createHierarchy(String componentName)
	{
		try {
			return new RemoteInfo(this, componentName, null);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Creates a new remote info instance by appending a query string
	 * to the end of the name. For example,
	 * "abeans-Simulator:///PSBEND/current" plus query "get" will result in
	 * "abeans-Simulator:///PSBEND/current?get".
	 *
	 * @param queryName the query that will be appended after all pathcomponent
	 *        parts of the URI name, non-<code>null</code>
	 *
	 * @return a new remote info instance with appended query
	 */
	public RemoteInfo createQuery(String queryName)
	{
		try {
			return new RemoteInfo(this, null, queryName);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Returns the authority part of this remote info if it exists.
	 *
	 * @return authority or <code>null</code> if either the plug is calculating
	 *         the authority on the fly or the plug is running on no-authority
	 *         (global) namespace
	 */
	public String getAuthority()
	{
		return extractAuthority(toURI());
	}

	/**
	 * Creates a new remote info by appending to an existing remote
	 * name both a hierarchy pathcomponent and a new query. Other properties
	 * of the existing remote info are retained (such as the authority, plug
	 * type etc).
	 *
	 * @param componentName the component to append to the existing remote
	 *        name, non-<code>null</code>
	 * @param queryName the query to append to the end of pathcomponent
	 *        sequence, non-<code>null</code>
	 *
	 * @return a new instance of remote info with names appended
	 */
	public RemoteInfo createHierarchyAndQuery(String componentName,
	    String queryName)
	{
		try {
			return new RemoteInfo(this, componentName, queryName);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Throws an exception.
	 *
	 * @param posn ignored
	 * @param comp ignored
	 *
	 * @return exception always thrown
	 *
	 * @throws InvalidNameException always thrown
	 */
	public Name add(int posn, String comp) throws InvalidNameException
	{
		throw new InvalidNameException(
		    "Cannot add to a remote info with generic add method. Use 'create...' methods instead.");
	}

	/**
	 * Throws an exception.
	 *
	 * @param comp ignored
	 *
	 * @return exception always thrown
	 *
	 * @throws InvalidNameException always thrown
	 */
	public Name add(String comp) throws InvalidNameException
	{
		throw new InvalidNameException(
		    "Cannot add to a remote info with generic add method. Use 'create...' methods instead.");
	}

	/**
	 * Throws an exception.
	 *
	 * @param posn ignored
	 * @param n ignored
	 *
	 * @return exception always thrown
	 *
	 * @throws InvalidNameException always thrown
	 */
	public Name addAll(int posn, Name n) throws InvalidNameException
	{
		throw new InvalidNameException(
		    "Cannot add to a remote info with generic add method. Use 'create...' methods instead.");
	}

	/**
	 * Throws an exception.
	 *
	 * @param suffix ignored
	 *
	 * @return exception always thrown
	 *
	 * @throws InvalidNameException always thrown
	 */
	public Name addAll(Name suffix) throws InvalidNameException
	{
		throw new InvalidNameException(
		    "Cannot add to a remote info with generic add method. Use 'create...' methods instead.");
	}

	/**
	 * Throws an exception.
	 *
	 * @param posn ignored
	 *
	 * @return exception always thrown
	 *
	 * @throws InvalidNameException always thrown
	 */
	public Object remove(int posn) throws InvalidNameException
	{
		throw new InvalidNameException(
		    "Cannot remove name elements from remote info.");
	}

	/**
	 * Returns the query part of this remote info if it exists.
	 *
	 * @return query or <code>null</code>
	 */
	public String getQuery()
	{
		return extractQuery(toURI());
	}

	/**
	 * Creates Name instance by parsing URI string.
	 *
	 * @param uri URI string
	 *
	 * @return Name instance
	 *
	 * @throws NamingException if Name creation fails
	 */
	public static Name parseURIstring(String uri) throws NamingException
	{
		return new URIName(uri);
	}

	/**
	 * Parses remote name to relative URIName. Remote name corresponds
	 * to path part of URI.
	 *
	 * @param remoteName remote name, a relative path in URI with no
	 *
	 * @return relative URIName
	 */
	public static Name parseRemoteName(String remoteName)
	{
		String[] comps = remoteInfoEscaper.stringToComponents(remoteName);

		URIName ret = new URIName(comps);

		ret.setRelative(true);

		return ret;
	}

	/**
	 * Parses Abeans URI string to URIName.
	 *
	 * @param uri URI sting
	 * @param relative flags URIName to be relative or not
	 *
	 * @return instance of URIName
	 */
	public static Name parseAbeansURI(String uri, boolean relative)
	{
		String[] comps = remoteInfoEscaper.stringToComponents(uri);

		URIName ret = new URIName(comps);

		ret.setRelative(relative);

		return ret;
	}

	/**
	 * Takes a single unescaped part of a hierarchical name and escapes
	 * it so that it can be used to form remoteName part of remoteInfo. Escape
	 * rules are similar to those in javax.naming.CompositeName, only that '?'
	 * character is added as a separator and is escaped in the same way as '/'
	 * character.   Example "Device 3/1" -&gt; "Device 3\/1"
	 *
	 * @param string which should be escaped to represent a part of a Name
	 *        instance
	 *
	 * @return an escaped string, which can be concatenated with '/' and '?' to
	 *         form remoteName part of RemoteInfo
	 */
	public static String escapePartToRemoteName(String string)
	{
		return remoteInfoEscaper.escapeComponent(string);
	}

	/**
	 * Extracts name from <code>URI</code>.<p>This method helps
	 * creating <code>RemoteInfo</code> from <code>URI</code> serialized form.</p>
	 *
	 * @param uri the target <code>URI</code> for remote entity or operation
	 *
	 * @return extracted Abeans name
	 */
	public static String extractRemoteName(URI uri)
	{
		if (uri == null) {
			return null;
		}

		String path = uri.getPath();

		if (path != null && path.length() > 0) {
			if (path.charAt(0) == '/') {
				return path.substring(1);
			}

			return path;
		}

		return null;
	}

	/**
	 * Extracts remote name from <code>URI</code>. Extracted
	 * nametontains remote entity name plus query.<p>This method helps
	 * creating <code>RemoteInfo</code> from <code>URI</code> serialized form.</p>
	 *
	 * @param uri the target <code>URI</code> for remote entity or operation
	 *
	 * @return extracted Abeans name
	 */
	public static String extractName(URI uri)
	{
		if (uri == null) {
			return null;
		}

		String path = uri.getPath();
		String query = uri.getQuery();

		int l = path != null ? path.length() : 0;
		l += query != null ? query.length() : 0;
		l++;

		StringBuffer sb = new StringBuffer(l);

		if (path != null && path.length() > 0) {
			if (path.charAt(0) == '/') {
				sb.append(path.toCharArray(), 1, path.length() - 1);
			} else {
				sb.append(path.toCharArray());
			}
		}

		if (uri.getQuery() != null) {
			sb.append('?');
			sb.append(uri.getQuery());
		}

		return sb.length() > 0 ? sb.toString() : null;
	}

	/**
	 * Extracts authority part of URI for Abeans entity.<p>This method
	 * helps creating <code>RemoteInfo</code> from <code>URI</code> serialized
	 * form.</p>
	 *
	 * @param uri the target <code>URI</code> for Abeans Entity
	 *
	 * @return extracted Abeans authority
	 */
	public static String extractAuthority(URI uri)
	{
		if (uri == null) {
			return null;
		}

		return uri.getAuthority();
	}

	/**
	 * Extracts query part of URI for Abeans entity.<p>This method
	 * helps creating <code>RemoteInfo</code> from <code>URI</code> serialized
	 * form.</p>
	 *
	 * @param uri the target <code>URI</code> for Abeans Entity
	 *
	 * @return extracted Abeans query
	 */
	public static String extractQuery(URI uri)
	{
		if (uri == null) {
			return null;
		}

		return uri.getQuery();
	}

	/**
	 * Extracts plug type from scheme part of URI for Abeans entity.<p>This
	 * method helps creating <code>RemoteInfo</code> from <code>URI</code>
	 * serialized form.</p>
	 *
	 * @param uri the target <code>URI</code> for Abeans Entity
	 *
	 * @return extracted Abeans plug type
	 */
	public static String extractPlugType(URI uri)
	{
		if (uri == null) {
			return null;
		}

		String scheme = uri.getScheme();

		if (scheme == null) {
			return null;
		}

		int i = SCHEME_PREFIX.length();

		if (scheme.length() > i) {
			return scheme.substring(i);
		}

		return null;
	}
} /* __oOo__ */


/* __oOo__ */
