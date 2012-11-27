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
package org.csstudio.sds.ui.internal.editparts;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory which creates controllers (aka GEF Editparts).
 * 
 * The factory can be used for the run mode AND the edit mode.
 * 
 * @author Sven Wende
 * 
 */
public class WidgetEditPartFactory implements EditPartFactory {
    private static final Logger LOG = LoggerFactory.getLogger(WidgetEditPartFactory.class);

	/**
	 * The execution mode.
	 */
	private ExecutionMode _executionMode;

	/**
	 * Constructor.
	 * 
	 * @param executionMode
	 *            the execution mode
	 */
	public WidgetEditPartFactory(ExecutionMode executionMode) {
		assert executionMode != null;
		_executionMode = executionMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public EditPart createEditPart(final EditPart context,
			final Object modelElement) {
		EditPart part = getPartForModel(modelElement);
		// store widget model in EditPart
		if (part != null) {
			part.setModel(modelElement);
		}

		return part;
	}

	/**
	 * Gets the right EditPart for the specified model. If the model is unknown,
	 * a default edit part is returned ("null object pattern").
	 * 
	 * @param model
	 *            the model
	 * @return the according EditPart
	 */
	private EditPart getPartForModel(final Object model) {
		assert model != null;

		EditPart result = null;

		if (model instanceof DisplayModel) {
			result = new DisplayEditPart();
		} else if (model instanceof AbstractWidgetModel) {
			AbstractWidgetModel widgetModel = (AbstractWidgetModel) model;
			String typeID = widgetModel.getTypeID();
			EditPartService editPartService = EditPartService.getInstance();

			if (editPartService.canCreateEditPart(typeID)) {
				result = editPartService.createEditPart(typeID);
			} else {
				LOG.info("No controller registered for widgets of type: "
										+ typeID
										+ "! We are using a fallback controller instead.");
				result = new FallbackEditpart();
			}
		}

		if (result == null) {
			LOG.info("Could not create controller for model object: " + model);
		} else {
			// setup the mode on SDS controllers
			if (result instanceof AbstractBaseEditPart) {
				((AbstractBaseEditPart) result)
						.setExecutionMode(_executionMode);
			}
		}

		return result;
	}
}
