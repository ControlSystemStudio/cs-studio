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

import java.beans.PropertyChangeEvent;

import org.csstudio.sds.components.model.TextInputElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.dataconnection.AbstractConnectionService;
import org.csstudio.sds.dataconnection.ConnectionUtil;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.ElementProperty;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * EditPart controller for <code>TextInputElement</code> elements with support
 * for direct editing.
 * 
 * @author Alexander Will
 * 
 */
public final class TextInputEditPart extends AbstractElementEditPart {
	/**
	 * The direct edit manager.
	 */
	private LabelDirectEditManager _directManager;

	/**
	 * Flag that indicates if the text input field is currently edited.
	 */
	private boolean _isEditing = false;

	/**
	 * The actual figure will be surrounded with a small frame that can be used
	 * to drag the figure around (even if the cell editor is activated).
	 */
	private static final int FRAME_WIDTH = 10;

	/**
	 * The input field will be slightly brighter than the actual figure so it
	 * can be easily recognized.
	 */
	private static final int INPUT_FIELD_BRIGHTNESS = 10;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		return new RefreshableLabelFigure();
	}

	/**
	 * Use the currently activated connection service to set the given value to
	 * the configured output channel.
	 * 
	 * @param value
	 *            The value that will be set to the configured output channel.
	 */
	protected void setDalPropertyValue(final Object value) {
		AbstractConnectionService connectionService = ConnectionUtil
				.getInstance().getActiveConncetionService();

		String outputChannel = ""; //$NON-NLS-1$

		ElementProperty outputChannelProperty = getCastedModel().getProperty(
				TextInputElement.PROP_OUTPUT_CHANNEL);

		if (outputChannelProperty != null) {
			outputChannel = outputChannelProperty.getPropertyValue().toString();
		}

		if (connectionService != null) {
			// TODO: A better error handling is needed here!
			connectionService.setPropertyValue(outputChannel, value,
					getCastedModel().getAliasDescriptors());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure f) {
		RefreshableLabelFigure label = (RefreshableLabelFigure) f;

		if (!_isEditing) {
			if (propertyName.equals(TextInputElement.PROP_INPUT_TEXT)) {
				label.setText(newValue.toString());
				setDalPropertyValue(newValue);

				return true;
			} else if (propertyName.equals(TextInputElement.PROP_FONT)) {
				FontData fontData = (FontData) newValue;
				label.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new LabelDirectEditPolicy());

		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new SelectionEditPolicy() {
					@Override
					protected void hideSelection() {
					}

					@Override
					protected void showSelection() {
						performDirectEdit();
					}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(final Request req) {
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)) {
			performDirectEdit();
			return;
		}
		super.performRequest(req);
	}

	/**
	 * Open the cell editor for direct editing.
	 */
	private void performDirectEdit() {
		if (_directManager == null) {
			_directManager = new LabelDirectEditManager(
					new LabelCellEditorLocator(getFigure()));
		}
		_isEditing = true;
		_directManager.show();
	}

	/**
	 * The direct edit manager.
	 */
	private class LabelDirectEditManager extends DirectEditManager {
		/**
		 * Standard constructor.
		 * 
		 * @param locator
		 *            The cell editor locator.
		 */
		public LabelDirectEditManager(final CellEditorLocator locator) {
			super(TextInputEditPart.this, TextCellEditor.class, locator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CellEditor createCellEditorOn(final Composite composite) {
			TextCellEditor result = new TextCellEditor(composite, SWT.CENTER);
			result.addListener(new ICellEditorListener() {
				public void applyEditorValue() {
					_isEditing = false;
				}

				public void cancelEditor() {
					_isEditing = false;
				}

				public void editorValueChanged(final boolean oldValidState,
						final boolean newValidState) {
				}
			});
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void initCellEditor() {
			String currentValue = "N/A"; //$NON-NLS-1$

			ElementProperty inputTextProperty = getCastedModel().getProperty(
					TextInputElement.PROP_INPUT_TEXT);

			if (inputTextProperty != null) {
				currentValue = inputTextProperty.getPropertyValue().toString();
			}

			getCellEditor().setValue(currentValue);
			Text text = (Text) getCellEditor().getControl();

			// get the chosen font
			FontData fontData = (FontData) getCastedModel().getProperty(
					TextInputElement.PROP_FONT).getPropertyValue();
			Font font = CustomMediaFactory.getInstance().getFont(
					new FontData[] { fontData });

			// get the chosen foreground color
			RGB foregroundRgb = (RGB) getCastedModel().getProperty(
					AbstractElementModel.PROP_COLOR_FOREGROUND)
					.getPropertyValue();
			Color foregroundColor = CustomMediaFactory.getInstance().getColor(
					foregroundRgb);

			// get the chosen background color
			RGB backgroundRgb = (RGB) getCastedModel().getProperty(
					AbstractElementModel.PROP_COLOR_BACKGROUND)
					.getPropertyValue();

			int red = Math.min(backgroundRgb.red + INPUT_FIELD_BRIGHTNESS, 255);
			int green = Math.min(backgroundRgb.green + INPUT_FIELD_BRIGHTNESS,
					255);
			int blue = Math.min(backgroundRgb.blue + INPUT_FIELD_BRIGHTNESS,
					255);

			Color backgroundColor = CustomMediaFactory.getInstance().getColor(
					new RGB(red, green, blue));

			text.setForeground(foregroundColor);
			text.setBackground(backgroundColor);
			text.setFont(font);
			text.selectAll();
		}
	}

	/**
	 * The cell editor locator.
	 */
	private class LabelCellEditorLocator implements CellEditorLocator {
		/**
		 * The underlying figure.
		 */
		private IFigure _figure;

		/**
		 * Standard constructor.
		 * 
		 * @param f
		 *            The underlying figure.
		 */
		public LabelCellEditorLocator(final IFigure f) {
			_figure = f;
		}

		/**
		 * {@inheritDoc}
		 */
		public void relocate(final CellEditor celleditor) {
			Text text = (Text) celleditor.getControl();
			Rectangle rect = TextInputEditPart.this.figure.getBounds()
					.getCopy();

			rect.x = rect.x + FRAME_WIDTH;
			rect.y = rect.y + FRAME_WIDTH;
			rect.height = rect.height - (FRAME_WIDTH * 2);
			rect.width = rect.width - (FRAME_WIDTH * 2);

			_figure.translateToAbsolute(rect);
			text.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	}

	/**
	 * The direct edit command that changes the input text.
	 */
	private class DirectEditCommand extends Command {
		/**
		 * The old input text.
		 */
		private String _oldInputText;

		/**
		 * The new input text.
		 */
		private String _newInputText;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute() {
			ElementProperty inputTextProperty = getCastedModel().getProperty(
					TextInputElement.PROP_INPUT_TEXT);

			if (inputTextProperty != null) {
				_oldInputText = inputTextProperty.getPropertyValue().toString();
				inputTextProperty.setPropertyValue(_newInputText);

//				TODO: Auskommentiert wg. Refactoring (swende)
//				propertyChange(new PropertyChangeEvent(this,
//						TextInputElement.PROP_INPUT_TEXT, _oldInputText,
//						_newInputText));
			}
		}

		/**
		 * Set the input text.
		 * 
		 * @param inputText
		 *            The input text.
		 */
		public void setInputText(final String inputText) {
			_newInputText = inputText;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void undo() {
			ElementProperty inputTextProperty = getCastedModel().getProperty(
					TextInputElement.PROP_INPUT_TEXT);

			if (inputTextProperty != null) {
				inputTextProperty.setPropertyValue(_oldInputText);

				//TODO: Auskommentiert wg. Refactoring (swende)
//				propertyChange(new PropertyChangeEvent(this,
//						TextInputElement.PROP_INPUT_TEXT, _newInputText,
//						_oldInputText));
			}
		}
	}

	/**
	 * The direct edit policy.
	 */
	private class LabelDirectEditPolicy extends DirectEditPolicy {
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Command getDirectEditCommand(final DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			command.setInputText((String) request.getCellEditor().getValue());
			return command;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void showCurrentEditValue(final DirectEditRequest request) {
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// TODO Auto-generated method stub
		
	}
}
