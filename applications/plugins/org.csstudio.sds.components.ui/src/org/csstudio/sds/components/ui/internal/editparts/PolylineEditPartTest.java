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
	 * Test method for
	 * {@link org.csstudio.sds.components.ui.internal.editparts.PolylineEditPart#createFigure()}.
	 */
	@Test
	public void testCreateFigure() {
		IFigure figure = _editPart.doCreateFigure();
		assertTrue(figure instanceof RefreshablePolylineFigure);
	}

}
