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

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.model.EllipseModel;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.components.model.PolygonModel;
import org.csstudio.sds.components.model.PolylineModel;
import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.DisplayModelInputStream;
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
 * @version $Revision$
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

		DisplayModelInputStream modelInputStream = new DisplayModelInputStream(
				displayModel);

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
	private ActionButtonModel createActionButton() {
		ActionButtonModel result = new ActionButtonModel();
		result.setLocation(0, 0);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
				ColorConstants.black.getRGB());
		result.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 1);

		result.setPropertyValue(ActionButtonModel.PROP_ACTION, 1);
		result.setPropertyValue(ActionButtonModel.PROP_LABEL, "Click me!");
		result.setPropertyValue(ActionButtonModel.PROP_RESOURCE,
				"/SDS/test.css-sds");
		result.setPropertyValue(ActionButtonModel.PROP_FONT, new FontData(
				"Arial", 12, SWT.BOLD));

		return result;
	}

	/**
	 * Create a sample bargraph model.
	 * 
	 * @return A sample bargraph model.
	 */
	private BargraphModel createBargraph() {
		BargraphModel result = new BargraphModel();
		result.setLocation(150, 0);
		result.setSize(150, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
				ColorConstants.black.getRGB());
		result.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 1);

		result.setPropertyValue(BargraphModel.PROP_DEFAULT_FILL_COLOR,
				ColorConstants.darkGreen.getRGB());
		result.setPropertyValue(BargraphModel.PROP_FILL, 50.0d);
		result.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,
				ColorConstants.darkGray.getRGB());
		result.setPropertyValue(BargraphModel.PROP_HIHI_COLOR,
				ColorConstants.red.getRGB());
		result.setPropertyValue(BargraphModel.PROP_HI_COLOR,
				ColorConstants.yellow.getRGB());
		result.setPropertyValue(BargraphModel.PROP_M_COLOR,
				ColorConstants.green.getRGB());
		result.setPropertyValue(BargraphModel.PROP_LO_COLOR,
				ColorConstants.yellow.getRGB());
		result.setPropertyValue(BargraphModel.PROP_LOLO_COLOR,
				ColorConstants.red.getRGB());
		result.setPropertyValue(BargraphModel.PROP_MIN, 0.0d);
		result.setPropertyValue(BargraphModel.PROP_MAX, 100.0d);
		result.setPropertyValue(BargraphModel.PROP_HIHI_LEVEL, 90.0d);
		result.setPropertyValue(BargraphModel.PROP_HI_LEVEL, 75.0d);
		result.setPropertyValue(BargraphModel.PROP_M_LEVEL, 50.0d);
		result.setPropertyValue(BargraphModel.PROP_LO_LEVEL, 25.0d);
		result.setPropertyValue(BargraphModel.PROP_LOLO_LEVEL, 10.0d);
		result.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, Boolean.TRUE);
		result.setPropertyValue(BargraphModel.PROP_SHOW_MARKS, 1);
		result.setPropertyValue(BargraphModel.PROP_SHOW_SCALE, 2);
		result.setPropertyValue(BargraphModel.PROP_SCALE_SECTION_COUNT, 100);

		result.addAlias("record", "recordName");

		connectToSingleInputChannel(result, BargraphModel.PROP_FILL,
				"$record$.VALUE", Double.class);

		return result;
	}

	/**
	 * Create a sample ellipse model.
	 * 
	 * @return A sample ellipse model.
	 */
	private EllipseModel createEllipse() {
		EllipseModel result = new EllipseModel();
		result.setLocation(400, 0);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(EllipseModel.PROP_FILL, 75.0d);
		result.setPropertyValue(EllipseModel.PROP_ORIENTATION, Boolean.FALSE);

		return result;
	}

	/**
	 * Create a sample label model.
	 * 
	 * @return A sample label model.
	 */
	private LabelModel createLabel() {
		LabelModel result = new LabelModel();
		result.setLocation(0, 100);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(LabelModel.PROP_LABEL, "Hello SDS");
		result.setPropertyValue(LabelModel.PROP_FONT, new FontData("Arial", 12,
				SWT.BOLD));
		result.setPropertyValue(LabelModel.PROP_TEXT_ALIGNMENT, 4);

		return result;
	}

	/**
	 * Create a sample meter model.
	 * 
	 * @return A sample meter model.
	 */
	private MeterModel createMeter() {
		MeterModel result = new MeterModel();
		result.setLocation(150, 100);
		result.setSize(50, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(MeterModel.PROP_VALUE, 10.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL1_LOWER_BORDER, 0.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL1_UPPER_BORDER, 120.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL2_LOWER_BORDER, 120.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL2_UPPER_BORDER, 240.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL3_LOWER_BORDER, 240.0d);
		result.setPropertyValue(MeterModel.PROP_INTERVAL3_UPPER_BORDER, 360.0d);

		return result;
	}

	/**
	 * Create a sample rectangle model.
	 * 
	 * @return A sample rectangle model.
	 */
	private RectangleModel createRectangle() {
		RectangleModel result = new RectangleModel();
		result.setLocation(400, 100);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(RectangleModel.PROP_FILL, 75.0d);
		result.setPropertyValue(RectangleModel.PROP_ORIENTATION, Boolean.FALSE);

		return result;
	}

	/**
	 * Create a sample simple slider model.
	 * 
	 * @return A sample simple slider model.
	 */
	private SimpleSliderModel createSimpleSlider() {
		SimpleSliderModel result = new SimpleSliderModel();

		result.setLocation(0, 200);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(SimpleSliderModel.PROP_VALUE, 10.0d);
		result.setPropertyValue(SimpleSliderModel.PROP_SHOW_VALUE_AS_TEXT,
				Boolean.TRUE);
		result.setPropertyValue(SimpleSliderModel.PROP_MIN, 0.0d);
		result.setPropertyValue(SimpleSliderModel.PROP_MAX, 100.0d);
		result.setPropertyValue(SimpleSliderModel.PROP_INCREMENT, 5.0d);
		result.setPropertyValue(SimpleSliderModel.PROP_ORIENTATION,
				Boolean.TRUE);
		result.setPropertyValue(SimpleSliderModel.PROP_PRECISION, 1);
		result.setPropertyValue(SimpleSliderModel.PROP_SLIDER_WIDTH, 10);

		return result;
	}

	/**
	 * Create a sample text input model.
	 * 
	 * @return A sample text input model.
	 */
	private TextInputModel createTextInput() {
		TextInputModel result = new TextInputModel();
		result.setLocation(150, 200);
		result.setSize(100, 50);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(TextInputModel.PROP_INPUT_TEXT, "120.0");
		result.setPropertyValue(TextInputModel.PROP_FONT, new FontData("Arial",
				12, SWT.BOLD));
		result.setPropertyValue(LabelModel.PROP_TEXT_ALIGNMENT, 0);

		connectToOutputChannel(result, TextInputModel.PROP_INPUT_TEXT,
				"channelName.VALUE", Double.class);

		return result;
	}

	/**
	 * Create a sample waveform model.
	 * 
	 * @return A sample waveform model.
	 */
	private WaveformModel createWaveform() {
		WaveformModel result = new WaveformModel();
		result.setLocation(0, 300);
		result.setSize(200, 200);
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(WaveformModel.PROP_AUTO_SCALE, Boolean.TRUE);
		result.setPropertyValue(WaveformModel.PROP_CONNECTION_LINE_COLOR,
				ColorConstants.black.getRGB());
		result.setPropertyValue(WaveformModel.PROP_GRAPH_COLOR,
				ColorConstants.black.getRGB());
		result.setPropertyValue(WaveformModel.PROP_GRAPH_LINE_WIDTH, 3);
		result.setPropertyValue(WaveformModel.PROP_LEDGER_LINE_COLOR,
				ColorConstants.black.getRGB());
		result.setPropertyValue(WaveformModel.PROP_MIN, 0.0d);
		result.setPropertyValue(WaveformModel.PROP_MAX, 100.0d);
		result.setPropertyValue(WaveformModel.PROP_SHOW_CONNECTION_LINES,
				Boolean.TRUE);
		result.setPropertyValue(WaveformModel.PROP_SHOW_LEDGER_LINES,
				Boolean.TRUE);
		result.setPropertyValue(WaveformModel.PROP_SHOW_SCALE, Boolean.TRUE);
		result.setPropertyValue(WaveformModel.PROP_SHOW_VALUES, Boolean.TRUE);
		result.setPropertyValue(WaveformModel.PROP_WAVE_FORM, new double[] {
				0.0d, 10.0d, 20.0d, 30.0d, 40.0d, 50.0d, 60.0d, 70.0d, 80.0d,
				90.0d });
		result.setPropertyValue(WaveformModel.PROP_X_SCALE_SECTION_COUNT, 25);
		result.setPropertyValue(WaveformModel.PROP_Y_SCALE_SECTION_COUNT, 25);

		return result;
	}

	/**
	 * Create a sample polygon model.
	 * 
	 * @return A sample polygon model.
	 */
	private PolygonModel createPolygon() {
		PolygonModel result = new PolygonModel();
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(PolygonModel.PROP_FILL, 50.0d);
		result.setPropertyValue(PolygonModel.PROP_POINTS, new PointList(
				new int[] { 1, 10, 40 }));

		result.setLocation(600, 300);
		result.setSize(200, 200);

		return result;
	}

	/**
	 * Create a sample polyline model.
	 * 
	 * @return A sample polyline model.
	 */
	private PolylineModel createPolyline() {
		PolylineModel result = new PolylineModel();
		result.setForegroundColor(ColorConstants.black.getRGB());
		result.setBackgroundColor(ColorConstants.lightGreen.getRGB());
		result.setVisible(true);

		result.setPropertyValue(PolylineModel.PROP_FILL, 50.0d);
		result.setPropertyValue(PolylineModel.PROP_LINE_WIDTH, 10);
		result.setPropertyValue(PolylineModel.PROP_POINTS, new PointList(
				new int[] { 1, 10, 2, 20, 3, 30, 4, 40, 5, 50 }));

		result.setLocation(0, 600);
		result.setSize(200, 200);

		return result;
	}

}
