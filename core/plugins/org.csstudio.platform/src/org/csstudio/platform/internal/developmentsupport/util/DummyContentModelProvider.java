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
package org.csstudio.platform.internal.developmentsupport.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Maintains a dummy model for the resources view. The initial model is a simple
 * list, that contains some workspace projects as well as some process
 * variables.
 * 
 * Additional model objects can be contributed.
 * 
 * @TODO: it might be useful to develop a listener mechanism, which informs
 *        users of the dummy model, if the model changes
 * 
 * @author Sven Wende
 * 
 */
public final class DummyContentModelProvider {
	/**
	 * The singleton instance.
	 */
	private static DummyContentModelProvider _instance;

	/**
	 * The dummy model.
	 */
	private List<IAdaptable> _model;

	/**
	 * Private singleton constructor.
	 */
	private DummyContentModelProvider() {
		_model = new ArrayList<IAdaptable>();

		// add some workspace resources
		// try {
		// IResource[] projects = ResourcesPlugin.getWorkspace().getRoot()
		// .members();
		// for (int i = 0; i < projects.length; i++) {
		// _model.add(projects[i]);
		// }
		// } catch (CoreException e) {
		// CentralLogger.getInstance().error("", e);
		// }

		// add some process variables
		_model.add(CentralItemFactory.createProcessVariable("/kryo/temperature")); //$NON-NLS-1$
		_model.add(CentralItemFactory.createProcessVariable("/kryo/pressure1")); //$NON-NLS-1$
		_model.add(CentralItemFactory.createProcessVariable("/kryo/pressure2")); //$NON-NLS-1$
		_model.add(CentralItemFactory.createProcessVariable("/kryo/pressure3")); //$NON-NLS-1$
		_model.add(CentralItemFactory.createProcessVariable("/kryo/pressure4")); //$NON-NLS-1$
		_model.add(CentralItemFactory.createProcessVariable("/kryo/pressure5")); //$NON-NLS-1$
		
		// add some archive data source
		_model.add(CentralItemFactory.createArchiveDataSource("url1", 1, "url1Name")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(CentralItemFactory.createArchiveDataSource("url2", 2, "url2Name")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(CentralItemFactory.createArchiveDataSource("url3", 3, "url3Name")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(CentralItemFactory.createArchiveDataSource("url4", 4, "url4Name")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(CentralItemFactory.createArchiveDataSource("url5", 5, "url5Name")); //$NON-NLS-1$ //$NON-NLS-2$

		// add some text containers
		_model.add(new TextContainer("TextContainer 1", "This is Text 1!!")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(new TextContainer("TextContainer 2", "This is Text 2!!")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(new TextContainer("TextContainer 3", "This is Text 3!!")); //$NON-NLS-1$ //$NON-NLS-2$
		_model.add(new TextContainer("TextContainer 4", "This is Text 4!!")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static DummyContentModelProvider getInstance() {
		if (_instance == null) {
			_instance = new DummyContentModelProvider();
		}

		return _instance;
	}

	/**
	 * Adds the specified object to the model.
	 * 
	 * @param modelObject
	 *            a model object
	 */
	public void addModelObject(final IAdaptable modelObject) {
		assert modelObject != null;

		_model.add(modelObject);
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public List<IAdaptable> getModel() {
		return _model;
	}
}
