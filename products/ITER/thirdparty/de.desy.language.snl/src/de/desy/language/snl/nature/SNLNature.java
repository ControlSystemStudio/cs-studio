/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.snl.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Project nature for SNL projects.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public class SNLNature implements IProjectNature {

	/**
	 * Returns the id of this nature used by eclipse to identify the nature of a
	 * project. Exactly this id is to be used as Id on registering this nature
	 * at the extension point "org.eclipse.core.resources.natures".
	 * 
	 * @return The nature id, not null.
	 */
	final static public String getNatureId() {
		return "de.desy.language.snl.nature.SNLNature";
	}

	/**
	 * The name of this nature registered on the extension point described at
	 * {@link #getNatureId()}.
	 */
	final static public String getNatureName() {
		return "State Notation Languages Nature";
	}

	/**
	 * The project this nature instance assigned to.
	 */
	private IProject _project;

	/**
	 * {@inheritDoc}
	 */
	public void configure() throws CoreException {
		// TODO Add snl builder to commands of projectdescription,
		// see: SNLNature2
	}

	/**
	 * {@inheritDoc}
	 */
	public void deconfigure() throws CoreException {
		// TODO Add snl builder to commands of projectdescription,
		// see: SNLNature2
	}

	/**
	 * Gets the project currently used in this nature configurator.
	 * 
	 * @return The project or null if no project has been set before.
	 */
	public IProject getProject() {
		return this._project;
	}

	/**
	 * Sets the project to use in this nature configurator.
	 * 
	 * @param project
	 *            The project to use or null if no project to configure.
	 */
	public void setProject(final IProject project) {
		this._project = project;
	}
}
