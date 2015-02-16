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

package org.csstudio.dal.context;


/**
 * <p>
 * An interface that describes a <code>Identifiable</code>
 * instance. The description is based on the uniquielly defined name of
 * <code>Identifiable</code> in regard to remote system. Identifier can be used
 * in ordinary and debug output to uniquely define the component.
 * The implementation behind the <code>Identifier</code>
 * shuld avoid using inner class sinc it can lead to memory leak.
 * Unmodifiable external class instance should be used, for example created by
 * <code>IdentifierUtilities</code> class.
 * <code>Identifier</code> must give consistent results through
 * the lifecycle of the <code>Identifiable</code>. Objects that change
 * identity through time (such as beans that are connected to remote
 * objects and are identified by the name of the remote
 * object) must create new identifier object, when identity changes.
 * The <code>Identifier</code> implementations should not contain
 * references to <code>Identifiables</code> that they identify to
 * prevent excessive cross-linkage and memory leaks.
 *
 * @author    Gasper Tkacik
 */
public interface Identifier
{
	enum Type {PROPERTY, DEVICE, PROXY, APPLICATION, PLUG, OTHER;
	}
	;

	/**
	 * Returns the name of the <code>Identifiable</code>. This function
	 * must never return <code>null</code>.
	 *
	 * @return the name of <code>Identifiable</code>
	 */
	String getName();

	/**
	 * Returns the name of the <code>Identifiable</code>. This function
	 * must never return <code>null</code>.
	 *
	 * @return the name of <code>Identifiable</code>
	 */
	String getUniqueName();

	/**
	 * Returns the qualified name of the <code>Identifiable</code>.
	 *
	 * @return full name of the described object
	 */
	String getLongQualifiedName();

	/**
	 * Returns one of the constants defined by this interface that
	 * describe the nature of the <code>Identifiable</code>.
	 *
	 * @return the type of the <code>Identifiable</code>
	 */
	Type getType();
}

/* __oOo__ */
