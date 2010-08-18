/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Stefan Hofer
 * @version $Revision: 1.5 $
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
	 * Performance test for contributed EditPart implementations, which tries to
	 * draw the figures used in the EditParts and measure their refresh
	 * performance. The test fails if the average refresh duration of a figure
	 * is more than one millisecond.
	 */
	@Ignore @Test
	public void testPerformanceOfContributedFigures() {
		final Collection<AbstractBaseEditPart> editParts = new ArrayList<AbstractBaseEditPart>();
		final WidgetModelFactoryService modelFactoryService = WidgetModelFactoryService
				.getInstance();
		final WidgetEditPartFactory editPartFactory = new WidgetEditPartFactory(ExecutionMode.EDIT_MODE);

		final IExtensionRegistry extReg = Platform.getExtensionRegistry();
		final String id = SdsUiPlugin.EXTPOINT_WIDGET_EDITPARTS;
		final IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (final IConfigurationElement element : confElements) {
			final String typeId = element.getAttribute("typeId"); //$NON-NLS-1$
			editParts.add((AbstractBaseEditPart) editPartFactory
					.createEditPart(null, modelFactoryService
							.getWidgetModel(typeId)));
		}

		_display.syncExec(new Runnable() {
			public void run() {
				final Shell shell = new Shell();
				final LightweightSystem lws = new LightweightSystem(shell);
				shell.open();

				shell.setFocus();
				for (final AbstractBaseEditPart editPart : editParts) {
					long duration = 0; // counted in milliseconds

					shell.setSize(1, 1);
					final IFigure figure = editPart.getFigure();
					lws.setContents(figure);

					final int iterations = 500;

					for (int i = 0; i < iterations; i++) {
						shell.setSize(i, i);
						final long before = new Date().getTime();
						figure.repaint();
						duration += (new Date().getTime() - before);
					}

					final double avgRefreshDuration = (double) duration / iterations;

					assertTrue("Refresh for Figure " //$NON-NLS-1$
									+ figure.getClass().getName()
									+ " is too slow (" //$NON-NLS-1$
									+ duration
									+ " for " + iterations //$NON-NLS-1$
									+ " refresh operations = " + avgRefreshDuration + " avg)",
									avgRefreshDuration < MAX_REFRESH_DURATION); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
	}
}
