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
package org.hibernate.engine.profile;

/**
 * Models an individual fetch within a profile.
 *
 * @author Steve Ebersole
 */
public class Fetch {
	private final Association association;
	private final Style style;

	public Fetch(Association association, Style style) {
		this.association = association;
		this.style = style;
	}

	public Association getAssociation() {
		return association;
	}

	public Style getStyle() {
		return style;
	}

	/**
	 * The type or style of fetch.  For the moment we limit this to
	 * join and select, though technically subselect would be valid
	 * here as as well; however, to support subselect here would
	 * require major changes to the subselect loading code (which is
	 * needed for other things as well anyway).
	 */
	public static class Style {
		public static final Style JOIN = new Style( "join" );
		public static final Style SELECT = new Style( "select" );

		private final String name;

		private Style(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public static Style parse(String name) {
			if ( SELECT.name.equals( name ) ) {
				return SELECT;
			}
			else {
				// the default...
				return JOIN;
			}
		}
	}

	public String toString() {
		return "Fetch[" + style + "{" + association.getRole() + "}]";
	}
}
