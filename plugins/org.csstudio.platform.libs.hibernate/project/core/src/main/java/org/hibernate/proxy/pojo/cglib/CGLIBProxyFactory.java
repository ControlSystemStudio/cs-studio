/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
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
 */
package org.hibernate.proxy.pojo.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.AbstractComponentType;

/**
 * @author Gavin King
 *
 * @deprecated Per HHH-5451 support for cglib as a bytecode provider has been deprecated.
 */
public class CGLIBProxyFactory implements ProxyFactory {

	protected static final Class[] NO_CLASSES = new Class[0];

	private Class persistentClass;
	private String entityName;
	private Class[] interfaces;
	private Method getIdentifierMethod;
	private Method setIdentifierMethod;
	private AbstractComponentType componentIdType;
	private Class factory;

	public void postInstantiate(
		final String entityName,
		final Class persistentClass,
		final Set interfaces,
		final Method getIdentifierMethod,
		final Method setIdentifierMethod,
		AbstractComponentType componentIdType)
	throws HibernateException {
		this.entityName = entityName;
		this.persistentClass = persistentClass;
		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		this.componentIdType = componentIdType;
		factory = CGLIBLazyInitializer.getProxyFactory(persistentClass, this.interfaces);
	}

	public HibernateProxy getProxy(Serializable id, SessionImplementor session)
		throws HibernateException {

		return CGLIBLazyInitializer.getProxy(
				factory, 
				entityName, 
				persistentClass, 
				interfaces, 
				getIdentifierMethod, 
				setIdentifierMethod,
				componentIdType,
				id, 
				session
			);
	}

}
