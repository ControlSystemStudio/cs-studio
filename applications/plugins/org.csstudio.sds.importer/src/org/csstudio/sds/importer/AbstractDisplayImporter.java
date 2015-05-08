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
package org.csstudio.sds.importer;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.eclipse.core.runtime.IPath;

/**
 * Abstract super class for display importers. A display importer is responsible
 * for the conversion of a legacy display model (e.g. EDM or MEDM) into a SDS
 * display.
 *
 * @author Alexander Will
 * @version $Revision: 1.8 $
 *
 */
public abstract class AbstractDisplayImporter {
    /**
     * Import the display that is stored in the given source file into a SDS
     * diaplay in the workspace.
     *
     * @param sourceFile
     *            The source file's full path.
     * @param targetProject
     *            The target workspace project.
     * @param targetFileName
     *            The target SDS display name.
     * @return true if the import was successful, or false otherwise.
     * @throws Exception
     *             if an error occurred during the import.
     */
    public abstract boolean importDisplay(String sourceFile,
            IPath targetProject, String targetFileName) throws Exception;

    /**
     * Create a widget element with the given type ID.
     *
     * @param typeId
     *            The type ID of the widget element.
     * @return A new widget element with the given type ID.
     */
    protected final AbstractWidgetModel createWidgetModel(String typeId) {
        AbstractWidgetModel result =  WidgetModelFactoryService.getInstance()
                .getWidgetModel(typeId);

        // .. update model to ensure invariants that have been declared by {@link SdsPlugin#EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS}
        SdsPlugin.getDefault().getWidgetPropertyPostProcessingService().applyForAllProperties(result, EventType.ON_DISPLAY_MODEL_LOADED);

        return result;
    }

    /**
     * Connect a property of the given model element to one single input
     * channel.
     *
     * @param model
     *            The model element.
     * @param propertyId
     *            The Id if the property that should be connected.
     * @param channelName
     *            The target channel name.
     * @param channelType
     *            The target channel type.
     */
    protected final void connectToSingleInputChannel(
            final AbstractWidgetModel model, final String propertyId,
            final String channelName) {
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor(channelName));
        model.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
    }

    /**
     * Connect a property of the given model element to one single input
     * channel.
     *
     * @param model
     *            The model element.
     * @param propertyId
     *            The Id if the property that should be connected.
     * @param channelName
     *            The target channel name.
     * @param channelType
     *            The target channel type.
     * @param ruleId
     *            The ID of the used logic rule.
     */
    protected final void connectToSingleInputChannel(
            final AbstractWidgetModel model, final String propertyId,
            final String channelName,
            final String ruleId) {
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(ruleId);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor(channelName));
        model.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
    }

    /**
     * Connect a property of the given model element to an output channel.
     *
     * @param model
     *            The model element.
     * @param propertyId
     *            The Id if the property that should be connected.
     * @param channelName
     *            The target channel name.
     * @param channelType
     *            The target channel type.
     */
    protected final void connectToOutputChannel(
            final AbstractWidgetModel model, final String propertyId,
            final String channelName) {
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
        ParameterDescriptor channel = new ParameterDescriptor(channelName);
        dynamicsDescriptor.addInputChannel(channel);
        dynamicsDescriptor.setOutputChannel(channel);
        model.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
    }
}
