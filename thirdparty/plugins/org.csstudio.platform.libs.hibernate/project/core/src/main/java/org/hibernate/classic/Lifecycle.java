/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.classic;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.Session;

/**
 * Provides callbacks from the <tt>Session</tt> to the persistent object.
 * Persistent classes <b>may</b> implement this interface but they are not
 * required to.<br>
 * <br>
 * <b>onSave:</b> called just before the object is saved<br>
 * <b>onUpdate:</b> called just before an object is updated,
 * ie. when <tt>Session.update()</tt> is called<br>
 * <b>onDelete:</b> called just before an object is deleted<br>
 * <b>onLoad:</b> called just after an object is loaded<br>
 * <br>
 * <tt>onLoad()</tt> may be used to initialize transient properties of the
 * object from its persistent state. It may <b>not</b> be used to load
 * dependent objects since the <tt>Session</tt> interface may not be
 * invoked from inside this method.<br>
 * <br>
 * A further intended usage of <tt>onLoad()</tt>, <tt>onSave()</tt> and
 * <tt>onUpdate()</tt> is to store a reference to the <tt>Session</tt>
 * for later use.<br>
 * <br>
 * If <tt>onSave()</tt>, <tt>onUpdate()</tt> or <tt>onDelete()</tt> return
 * <tt>VETO</tt>, the operation is silently vetoed. If a
 * <tt>CallbackException</tt> is thrown, the operation is vetoed and the
 * exception is passed back to the application.<br>
 * <br>
 * Note that <tt>onSave()</tt> is called after an identifier is assigned
 * to the object, except when identity column key generation is used.
 *
 * @see CallbackException
 * @author Gavin King
 */
public interface Lifecycle {

	/**
	 * Return value to veto the action (true)
	 */
	public static final boolean VETO = true;

	/**
	 * Return value to accept the action (false)
	 */
	public static final boolean NO_VETO = false;

	/**
	 * Called when an entity is saved.
	 * @param s the session
	 * @return true to veto save
	 * @throws CallbackException
	 */
	public boolean onSave(Session s) throws CallbackException;

	/**
	 * Called when an entity is passed to <tt>Session.update()</tt>.
	 * This method is <em>not</em> called every time the object's
	 * state is persisted during a flush.
	 * @param s the session
	 * @return true to veto update
	 * @throws CallbackException
	 */
	public boolean onUpdate(Session s) throws CallbackException;

	/**
	 * Called when an entity is deleted.
	 * @param s the session
	 * @return true to veto delete
	 * @throws CallbackException
	 */
	public boolean onDelete(Session s) throws CallbackException;

	/**
	 * Called after an entity is loaded. <em>It is illegal to
	 * access the <tt>Session</tt> from inside this method.</em>
	 * However, the object may keep a reference to the session
	 * for later use.
	 *
	 * @param s the session
	 * @param id the identifier
	 */
	public void onLoad(Session s, Serializable id);
}






