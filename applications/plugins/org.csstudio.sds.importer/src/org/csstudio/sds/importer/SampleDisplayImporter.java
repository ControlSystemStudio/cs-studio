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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * Sample display importer. The purpose of this importer is to simply
 * demonstrate the creation of displays with code.
 *
 * @author Alexander Will
 * @version $Revision: 1.10 $
 *
 */
public class SampleDisplayImporter extends AbstractDisplayImporter {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean importDisplay(final String sourceFile,
            final IPath targetProject, final String targetFileName)
            throws Exception {

        // this is the target display model
        DisplayModel displayModel = new DisplayModel();

        // read the source file
        BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
        while (reader.ready()) {
            // the file contents should be processed...
            reader.readLine();
        }

        displayModel.addWidget(createActionButton());
        displayModel.addWidget(createBargraph());
        displayModel.addWidget(createEllipse());
        displayModel.addWidget(createLabel());
        displayModel.addWidget(createMeter());
        displayModel.addWidget(createRectangle());
        displayModel.addWidget(createSimpleSlider());
        displayModel.addWidget(createTextInput());
        displayModel.addWidget(createWaveform());
        displayModel.addWidget(createPolygon());
        displayModel.addWidget(createPolyline());

        InputStream modelInputStream = PersistenceUtil.createStream(displayModel);

        // create the target file in the workspace
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath filePath = targetProject.append(targetFileName);
        IFile file = workspaceRoot.getFile(filePath);
        file.create(modelInputStream, true, null);

        reader.close();

        return true;
    }

    /**
     * Create a sample action button model.
     *
     * @return A sample action button model.
     */
    private AbstractWidgetModel createActionButton() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.ActionButton");
        result.setLocation(0, 0);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("border.color", ColorConstants.black.getRGB());
        result.setPropertyValue("border.width", 1);

        result.setPropertyValue("action", 1);
        result.setPropertyValue("label", "Click me!");
        result.setPropertyValue("resource", "/SDS/test.css-sds");
        result.setPropertyValue("font", new FontData("Arial", 12, SWT.BOLD));

        return result;
    }

    /**
     * Create a sample bargraph model.
     *
     * @return A sample bargraph model.
     */
    private AbstractWidgetModel createBargraph() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Bargraph");
        result.setLocation(150, 0);
        result.setSize(150, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setColor(AbstractWidgetModel.PROP_BORDER_COLOR, "#000000");
        result.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 1);

        result.setPropertyValue("defaultFillColor", ColorConstants.darkGreen
                .getRGB());
        result.setPropertyValue("fill", 50.0d);
        result.setPropertyValue("fillbackgroundColor", ColorConstants.darkGray
                .getRGB());
        result.setPropertyValue("hihiColor", ColorConstants.red.getRGB());
        result.setPropertyValue("hiColor", ColorConstants.yellow.getRGB());
        result.setPropertyValue("mColor", ColorConstants.green.getRGB());
        result.setPropertyValue("loColor", ColorConstants.yellow.getRGB());
        result.setPropertyValue("loloColor", ColorConstants.red.getRGB());
        result.setPropertyValue("minimum", 0.0d);
        result.setPropertyValue("maximum", 100.0d);
        result.setPropertyValue("hihiLevel", 90.0d);
        result.setPropertyValue("hiLevel", 75.0d);
        result.setPropertyValue("mLevel", 50.0d);
        result.setPropertyValue("loLevel", 25.0d);
        result.setPropertyValue("loloLevel", 10.0d);
        result.setPropertyValue("showValues", Boolean.TRUE);
        result.setPropertyValue("marksShowStatus", 1);
        result.setPropertyValue("scaleShowStatus", 2);
        result.setPropertyValue("sectionCount", 100);

        result.addAlias("channel", "channelName");

        connectToSingleInputChannel(result, "fill", "$channel$.VALUE");

        return result;
    }

    /**
     * Create a sample ellipse model.
     *
     * @return A sample ellipse model.
     */
    private AbstractWidgetModel createEllipse() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Ellipse");
        result.setLocation(400, 0);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("fill", 75.0d);
        result.setPropertyValue("orientation", Boolean.FALSE);

        return result;
    }

    /**
     * Create a sample label model.
     *
     * @return A sample label model.
     */
    private AbstractWidgetModel createLabel() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Label");
        result.setLocation(0, 100);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("label", "Hello SDS");
        result.setPropertyValue("font", new FontData("Arial", 12, SWT.BOLD));
        result.setPropertyValue("textAlignment", 4);

        return result;
    }

    /**
     * Create a sample meter model.
     *
     * @return A sample meter model.
     */
    private AbstractWidgetModel createMeter() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Meter");
        result.setLocation(150, 100);
        result.setSize(50, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("value", 10.0d);
        result.setPropertyValue("interval1.lower", 0.0d);
        result.setPropertyValue("interval1.upper", 120.0d);
        result.setPropertyValue("interval2.lower", 120.0d);
        result.setPropertyValue("interval2.upper", 240.0d);
        result.setPropertyValue("interval3.lower", 240.0d);
        result.setPropertyValue("interval3.uppper", 360.0d);

        return result;
    }

    /**
     * Create a sample rectangle model.
     *
     * @return A sample rectangle model.
     */
    private AbstractWidgetModel createRectangle() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Rectangle");
        result.setLocation(400, 100);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("fill", 75.0d);
        result.setPropertyValue("orientation", Boolean.FALSE);

        return result;
    }

    /**
     * Create a sample simple slider model.
     *
     * @return A sample simple slider model.
     */
    private AbstractWidgetModel createSimpleSlider() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.SimpleSlider");

        result.setLocation(0, 200);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("value", 10.0d);
        result.setPropertyValue("showValueAsText", Boolean.TRUE);
        result.setPropertyValue("min", 0.0d);
        result.setPropertyValue("max", 100.0d);
        result.setPropertyValue("increment", 5.0d);
        result.setPropertyValue("orientation", Boolean.TRUE);
        result.setPropertyValue("precision", 1);
        result.setPropertyValue("sliderWidth", 10);

        return result;
    }

    /**
     * Create a sample text input model.
     *
     * @return A sample text input model.
     */
    private AbstractWidgetModel createTextInput() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Textinput");
        result.setLocation(150, 200);
        result.setSize(100, 50);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("inputText", "120.0");
        result.setPropertyValue("font", new FontData("Arial", 12, SWT.BOLD));
        result.setPropertyValue("textAlignment", 0);

        connectToOutputChannel(result, "inputText", "channelName.VALUE");

        return result;
    }

    /**
     * Create a sample waveform model.
     *
     * @return A sample waveform model.
     */
    private AbstractWidgetModel createWaveform() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Waveform");
        result.setLocation(0, 300);
        result.setSize(200, 200);
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("autoscale", Boolean.TRUE);
        result.setPropertyValue("connection_lines_color", ColorConstants.black
                .getRGB());
        result.setPropertyValue("graph_color", ColorConstants.black.getRGB());
        result.setPropertyValue("connection_line_width", 3);
        result.setPropertyValue("ledger_lines_color", ColorConstants.black
                .getRGB());
        result.setPropertyValue("min", 0.0d);
        result.setPropertyValue("max", 100.0d);
        result.setPropertyValue("show_connection_lines", Boolean.TRUE);
        result.setPropertyValue("show_ledger_lines", Boolean.TRUE);
        result.setPropertyValue("show_scale", Boolean.TRUE);
        result.setPropertyValue("show_values", Boolean.TRUE);
        result.setPropertyValue("wave", new double[] { 0.0d, 10.0d, 20.0d,
                30.0d, 40.0d, 50.0d, 60.0d, 70.0d, 80.0d, 90.0d });
        result.setPropertyValue("x_scale_section_count", 25);
        result.setPropertyValue("y_scale_section_count", 25);

        return result;
    }

    /**
     * Create a sample polygon model.
     *
     * @return A sample polygon model.
     */
    private AbstractWidgetModel createPolygon() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Polygon");
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("fill", 50.0d);
        result.setPropertyValue("points",
                new PointList(new int[] { 1, 10, 40 }));

        result.setLocation(600, 300);
        result.setSize(200, 200);

        return result;
    }

    /**
     * Create a sample polyline model.
     *
     * @return A sample polyline model.
     */
    private AbstractWidgetModel createPolyline() {
        AbstractWidgetModel result = createWidgetModel("org.csstudio.sds.components.Polyline");
        result.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
        result.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "#00FF00");
        result.setVisible(true);

        result.setPropertyValue("fill", 50.0d);
        result.setPropertyValue("linewidth", 10);
        result.setPropertyValue("points", new PointList(new int[] { 1, 10, 2,
                20, 3, 30, 4, 40, 5, 50 }));

        result.setLocation(0, 600);
        result.setSize(200, 200);

        return result;
    }

}
