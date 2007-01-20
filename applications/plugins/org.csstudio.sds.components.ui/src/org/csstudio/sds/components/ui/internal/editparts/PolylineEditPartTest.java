package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.model.PolylineElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * 
 * @version $Revision$
 *
 */
public final class PolylineEditPartTest {

	/**
	 * The instance to test.
	 */
	private PolylineEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new PolylineEditPart();
		_editPart.setModel(new PolylineElement());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.PolylineEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshablePolylineFigure polygonFigure = (RefreshablePolylineFigure) _editPart.getFigure();
		
		final double oldFillValue = polygonFigure.getFill();
		final double newFillValue = oldFillValue+1;
		
		_editPart.doRefreshFigure(AbstractPolyElement.PROP_FILL_GRADE, newFillValue);
		assertEquals(newFillValue, polygonFigure.getFill());
		
		final PointList oldPointList = polygonFigure.getPoints();
		final PointList newPointList = new PointList(oldPointList.size()+2);
		newPointList.addPoint(17, 4);
		_editPart.doRefreshFigure(AbstractPolyElement.PROP_POINTS, newPointList);
		assertEquals(newPointList, polygonFigure.getPoints());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.PolylineEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.createFigure();
		assertTrue(figure instanceof RefreshablePolylineFigure);
	}

}
