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
package org.hibernate.tool.instrument.javassist;

import org.hibernate.bytecode.buildtime.Instrumenter;
import org.hibernate.bytecode.buildtime.JavassistInstrumenter;
import org.hibernate.bytecode.buildtime.Logger;
import org.hibernate.tool.instrument.BasicInstrumentationTask;

/**
 * An Ant task for instrumenting persistent classes in order to enable
 * field-level interception using Javassist.
 * <p/>
 * In order to use this task, typically you would define a a taskdef
 * similiar to:<pre>
 * <taskdef name="instrument" classname="org.hibernate.tool.instrument.javassist.InstrumentTask">
 *     <classpath refid="lib.class.path"/>
 * </taskdef>
 * </pre>
 * where <tt>lib.class.path</tt> is an ANT path reference containing all the
 * required Hibernate and Javassist libraries.
 * <p/>
 * And then use it like:<pre>
 * <instrument verbose="true">
 *     <fileset dir="${testclasses.dir}/org/hibernate/test">
 *         <include name="yadda/yadda/**"/>
 *         ...
 *     </fileset>
 * </instrument>
 * </pre>
 * where the nested ANT fileset includes the class you would like to have
 * instrumented.
 * <p/>
 * Optionally you can chose to enable "Extended Instrumentation" if desired
 * by specifying the extended attriubute on the task:<pre>
 * <instrument verbose="true" extended="true">
 *     ...
 * </instrument>
 * </pre>
 * See the Hibernate manual regarding this option.
 *
 * @author Muga Nishizawa
 * @author Steve Ebersole
 */
public class InstrumentTask extends BasicInstrumentationTask {
	protected Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options) {
		return new JavassistInstrumenter( logger, options );
	}
}
