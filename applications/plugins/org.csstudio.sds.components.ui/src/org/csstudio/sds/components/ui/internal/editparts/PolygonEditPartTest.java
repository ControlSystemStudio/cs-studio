package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.model.PolygonElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.eclipse.draw2d.IFigure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * 
 * @version $Revision$
 *
 */
public final class PolygonEditPartTest {

	/**
	 * The instance to test.
	 */
	private PolygonEditPart _editPart;

	/**
	 * Test set up.
	 */
	@Before
	public void setUp() {
		_editPart = new PolygonEditPart();
		_editPart.setModel(new PolygonElement());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.PolygonEditPart#doRefreshFigure(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testDoRefreshFigure() {
		RefreshablePolygonFigure polygonFigure = (RefreshablePolygonFigure) _editPart.getFigure();
		
		final double oldFillValue = polygonFigure.getFill();
		final double newFillValue = oldFillValue+1;
		
		_editPart.doRefreshFigure(AbstractPolyElement.PROP_FILL, newFillValue, _editPart.getFigure());
		assertEquals(newFillValue, polygonFigure.getFill());
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.PolygonEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.doCreateFigure();
		assertTrue(figure instanceof RefreshablePolygonFigure);
	}

}
