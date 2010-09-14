/*
 * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
 * third-party contributors as indicated by either @author tags or express
 * copyright attribution statements applied by the authors.  All
 * third-party contributions are distributed under license by Red Hat Inc.
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
 * Boston, MA  02110-1301  USA\
 */
package org.hibernate.ejb.criteria.components;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The client domain entity
 *
 */
@Entity
public class Client implements Serializable {
	private int id;
	private Name name;

	public Client() {
	}

	public Client(int id, Name name) {
		this.id = id;
		this.name = name;
	}

	public Client(int id, String firstName, String lastName) {
		this( id, new Name( firstName, lastName ) );
	}

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Embedded
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}
}
