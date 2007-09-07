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

import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableActionButtonFigure;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

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

		RefreshableActionButtonFigure button = new RefreshableActionButtonFigure();
		button.setText(model.getLabel());
		button.setFont(CustomMediaFactory.getInstance()
				.getFont(model.getFont()));
		button.setTextAlignment(model.getTextAlignment());
		button.setEnabled(getExecutionMode().equals(ExecutionMode.RUN_MODE) && model.getEnabled());
		return button;
	}

	/**
	 * Returns the Figure of this EditPart.
	 * @return RefreshableActionButtonFigure
	 * 			The RefreshableActionButtonFigure of this EditPart
	 */
	protected RefreshableActionButtonFigure getCastedFigure() {
		return (RefreshableActionButtonFigure) getFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		//
		RefreshableActionButtonFigure figure = getCastedFigure();
		figure.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					CentralLogger.getInstance().info(this, "KLICK");
					ActionButtonModel model = (ActionButtonModel) getWidgetModel();
					
					ActionData data = model.getActionData();
					data.getType().performAction();
//					switch(data.getType()) {
//					case ActionData.OPEN_SHELL :
//						openDisplayShellInRunMode(data.getDisplayPath(), data.getAliases());
//						break;
//					case ActionData.OPEN_VIEW:
//						openDisplayViewInRunMode(data.getDisplayPath(), data.getAliases());
//						break;
//					case ActionData.COMMIT_VALUE:
//						model.getProperty(ActionButtonModel.PROP_ACTIONDATA).setManualValue(data.getCommitValue());
//						break;
//					default:
//						// do nothing
//						CentralLogger.getInstance().info(this, "Clicked!");
//					}	
				} else {
					CentralLogger.getInstance().info(this, "ActionButton activated!");
				}
					
			}
		});

		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = getCastedFigure();
				figure.setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_LABEL, labelHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
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
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableActionButtonFigure figure = (RefreshableActionButtonFigure) refreshableFigure;
				figure.setTextAlignment((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TEXT_ALIGNMENT, alignmentHandler);
	}
	
	/**
	 * Opens a shell in RunMode.
	 * @param path
	 * 			The Ipath to the Display, which should be opened.
	 * @param newAlias
	 * 			The Map of new Alias for the opened Display			
	 */
	private void openDisplayShellInRunMode(final IPath path, final Map<String, String> newAlias) {
		if (path!=null && !path.isEmpty()) {
			RunModeService.getInstance().openDisplayShellInRunMode(path, newAlias);
		}
	}
	
	/**
	 * Opens a view in RunMode.
	 * @param path
	 * 			The IPath to the Display, which should be opened.
	 * @param newAlias
	 * 			The Map of new Alias for the opened Display			
	 */
	private void openDisplayViewInRunMode(final IPath path, final Map<String, String> newAlias) {
		if (path!=null && !path.isEmpty()) {
			RunModeService.getInstance().openDisplayViewInRunMode(path, newAlias);
		}
	}
	
}
