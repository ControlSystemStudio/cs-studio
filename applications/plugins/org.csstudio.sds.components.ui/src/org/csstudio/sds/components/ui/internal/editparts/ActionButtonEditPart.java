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
package org.csstudio.sds.components.ui.internal.editparts;

import org.eclipse.draw2d.ButtonModel;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableActionButtonFigure;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.internal.runmode.AbstractRunModeBox;
import org.csstudio.sds.ui.widgetactionhandler.WidgetActionHandlerService;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link RefreshableActionButtonFigure}.
 * 
 * @author Sven Wende
 * 
 */
public final class ActionButtonEditPart extends AbstractWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ActionButtonModel model = (ActionButtonModel) getWidgetModel();

		final RefreshableActionButtonFigure buttonFigure = new RefreshableActionButtonFigure();
		buttonFigure.setText(model.getLabel());
		buttonFigure.setFont(CustomMediaFactory.getInstance().getFont(
				model.getFont()));
		buttonFigure.setTextAlignment(model.getTextAlignment());
		buttonFigure.setEnabled(getExecutionMode().equals(
				ExecutionMode.RUN_MODE)
				&& model.isEnabled());
		buttonFigure.setStyle(model.getButtonStyle());
		return buttonFigure;
	}

	/**
	 * Returns the Figure of this EditPart.
	 * 
	 * @return RefreshableActionButtonFigure The RefreshableActionButtonFigure
	 *         of this EditPart
	 */
	protected RefreshableActionButtonFigure getCastedFigure() {
		return (RefreshableActionButtonFigure) getFigure();
	}

	/**
	 * Configures a listener for performing a {@link WidgetAction}.
	 * 
	 * @param figure
	 *            The figure of the widget
	 */
	private void configureButtonListener(
			final RefreshableActionButtonFigure figure) {
		// FIXME: diesen Listener im Runmode gar nicht erst anmelden
		// FIXME: Action synchron ausführen

		// FIXME: Warum wird hier kein normaler ActionListener verwendet? Siehe nächste Zeilen
		figure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				// FIXME: Einkommentieren, um Parent-Fenster zu schließen
//				AbstractRunModeBox box = (AbstractRunModeBox) getRuntimeContext().getWindowHandle();
//				box.dispose();
			}
		});
		
		figure.addChangeListener(new ChangeListener() {
			public void handleStateChanged(final ChangeEvent event) {
				final String propertyName = event.getPropertyName();
				if (ButtonModel.PRESSED_PROPERTY.equals(propertyName)
						&& getExecutionMode()==ExecutionMode.RUN_MODE
						&& figure.getModel().isArmed()) {
					
					// FIXME: 2008-10-07: swende: Bug. Alles ab hier passiert pro Klick 2x!!
					
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							ActionButtonModel model = (ActionButtonModel) getWidgetModel();
							
							// FIXME: Was ist der Usecase für Aktionen bei Press vs. Release Mousebutton?
							int index;
							if (figure.getModel().isPressed()) {
								index = model.getChoosenPressedActionIndex();
							} else {
								index = model.getChoosenReleasedActionIndex();
							}
							int size = model.getActionData().getWidgetActions().size();
							if (index >= 0 && size == 1) {
								index = 0;
							}
							if (index >= 0 && index < size) {
								WidgetAction type = model.getActionData()
										.getWidgetActions().get(index);
								
								WidgetActionHandlerService.getInstance()
										.performAction(model,type);
							}
							figure.getModel().setArmed(figure.getModel().isPressed());
						}
					});
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		//
		RefreshableActionButtonFigure figure = getCastedFigure();
		this.configureButtonListener(figure);

		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = getCastedFigure();
				figure.setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_LABEL, labelHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = getCastedFigure();
				FontData fontData = (FontData) newValue;
				figure.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, fontHandler);

		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = (RefreshableActionButtonFigure) refreshableFigure;
				figure.setTextAlignment((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TEXT_ALIGNMENT,
				alignmentHandler);

		// button style
		IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = (RefreshableActionButtonFigure) refreshableFigure;
				figure.setStyle((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TOGGLE_BUTTON,
				buttonStyleHandler);
	}

	// /**
	// * Opens a shell in RunMode.
	// * @param path
	// * The Ipath to the Display, which should be opened.
	// * @param newAlias
	// * The Map of new Alias for the opened Display
	// */
	// private void openDisplayShellInRunMode(final IPath path, final
	// Map<String, String> newAlias) {
	// if (path!=null && !path.isEmpty()) {
	// RunModeService.getInstance().openDisplayShellInRunMode(path, newAlias);
	// }
	// }
	//	
	// /**
	// * Opens a view in RunMode.
	// * @param path
	// * The IPath to the Display, which should be opened.
	// * @param newAlias
	// * The Map of new Alias for the opened Display
	// */
	// private void openDisplayViewInRunMode(final IPath path, final Map<String,
	// String> newAlias) {
	// if (path!=null && !path.isEmpty()) {
	// RunModeService.getInstance().openDisplayViewInRunMode(path, newAlias);
	// }
	// }

}
