package org.csstudio.sds.ui.internal.editparts;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.csstudio.sds.model.IWidgetModelFactory;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class WidgetEditPartFactoryTest {

	/**
	 * Maximum allowed refresh duration in milliseconds.
	 */
	private static final int MAX_REFRESH_DURATION = 1;

	/**
	 * A display instance.
	 */
	private final Display _display = Display.getCurrent();

	/**
	 * @throws java.lang.Exception
	 *             If an exception occurs.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 *             If an exception occurs.
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Performance test for contributed EditPart implementations, which tries to
	 * draw the figures used in the EditParts and measure their refresh
	 * performance. The test fails if the average refresh duration of a figure
	 * is more than one millisecond.
	 */
	@Test
	public void testPerformanceOfContributedFigures() {
		final Collection<AbstractWidgetEditPart> editParts = new ArrayList<AbstractWidgetEditPart>();
		final WidgetModelFactoryService modelFactoryService = WidgetModelFactoryService
				.getInstance();
		final WidgetEditPartFactory editPartFactory = new WidgetEditPartFactory(ExecutionMode.EDIT_MODE);

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsUiPlugin.EXTPOINT_WIDGET_EDITPARTS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			String typeId = element.getAttribute("typeId"); //$NON-NLS-1$
			final IWidgetModelFactory modelFactory = modelFactoryService
					.getWidgetModelFactory(typeId);
			editParts.add((AbstractWidgetEditPart) editPartFactory
					.createEditPart(null, modelFactory.createWidgetModel()));
		}

		_display.syncExec(new Runnable() {
			public void run() {
				final Shell shell = new Shell();
				LightweightSystem lws = new LightweightSystem(shell);
				shell.open();

				shell.setFocus();
				for (AbstractWidgetEditPart editPart : editParts) {
					long duration = 0; // counted in milliseconds

					shell.setSize(1, 1);
					IFigure figure = editPart.getFigure();
					lws.setContents(figure);

					int iterations = 500;

					for (int i = 0; i < iterations; i++) {
						shell.setSize(i, i);
						long before = new Date().getTime();
						figure.repaint();
						duration += (new Date().getTime() - before);
					}

					double avgRefreshDuration = (double) duration / iterations;

					assertTrue(
							"Refresh for Figure " //$NON-NLS-1$
									+ figure.getClass().getName()
									+ " is too slow (" //$NON-NLS-1$
									+ duration
									+ " for " + iterations //$NON-NLS-1$
									+ " refresh operations = " + avgRefreshDuration + " avg)", avgRefreshDuration < MAX_REFRESH_DURATION); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
	}
}
