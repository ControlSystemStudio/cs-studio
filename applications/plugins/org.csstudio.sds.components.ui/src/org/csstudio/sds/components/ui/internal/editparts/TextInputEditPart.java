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

import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * EditPart controller for <code>TextInputModel</code> elements with support
 * for direct editing.
 * 
 * EditPart controller for the TextInput widget. The controller mediates between
 * {@link TextInputModel} and {@link RefreshableLabelFigure}.
 * 
 * @author Alexander Will
 * 
 */
public final class TextInputEditPart extends AbstractWidgetEditPart {
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
		TextInputModel model = (TextInputModel) getCastedModel();

		RefreshableLabelFigure label = new RefreshableLabelFigure();

		label.setText(model.getInputText());
		label.setFont(CustomMediaFactory.getInstance().getFont(
						model.getFont()));
		label.setTextAlignment(model.getTextAlignment());

		label.addMouseListener(new MouseListener() {
			public void mouseDoubleClicked(final MouseEvent me) {
				performDirectEdit();
			}

			public void mousePressed(final MouseEvent me) {
			}

			public void mouseReleased(final MouseEvent me) {
			}

		});
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPoliciesHook() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new LabelDirectEditPolicy());
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
		CellEditor cellEditor = createCellEditor();
		locateCellEditor(cellEditor);

		cellEditor.activate();
		cellEditor.setFocus();
	}

	/**
	 * Create the cell editor for direct editing.
	 * 
	 * @return The cell editor for direct editing.
	 */
	private CellEditor createCellEditor() {
		final CellEditor result = new TextCellEditor((Composite) getViewer()
				.getControl());

		// init cell editor...
		String currentValue = "N/A"; //$NON-NLS-1$
		WidgetProperty inputTextProperty = getCastedModel().getProperty(
				TextInputModel.PROP_INPUT_TEXT);

		if (inputTextProperty != null) {
			currentValue = inputTextProperty.getPropertyValue().toString();
		}

		result.setValue(currentValue);
		final Text text = (Text) result.getControl();
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					DirectEditCommand cmd = new DirectEditCommand(text
							.getText());
					cmd.execute();
				} else if (e.keyCode == SWT.ESC) {
					result.deactivate();
				}
			}

		});

		// get the chosen font
		FontData fontData = (FontData) getCastedModel().getProperty(
				TextInputModel.PROP_FONT).getPropertyValue();
		Font font = CustomMediaFactory.getInstance().getFont(
				new FontData[] { fontData });

		// get the chosen foreground color
		RGB foregroundRgb = (RGB) getCastedModel().getProperty(
				AbstractWidgetModel.PROP_COLOR_FOREGROUND).getPropertyValue();
		Color foregroundColor = CustomMediaFactory.getInstance().getColor(
				foregroundRgb);

		// get the chosen background color
		RGB backgroundRgb = (RGB) getCastedModel().getProperty(
				AbstractWidgetModel.PROP_COLOR_BACKGROUND).getPropertyValue();

		int red = Math.min(backgroundRgb.red + INPUT_FIELD_BRIGHTNESS, 255);
		int green = Math.min(backgroundRgb.green + INPUT_FIELD_BRIGHTNESS, 255);
		int blue = Math.min(backgroundRgb.blue + INPUT_FIELD_BRIGHTNESS, 255);

		Color backgroundColor = CustomMediaFactory.getInstance().getColor(
				new RGB(red, green, blue));

		text.setForeground(foregroundColor);
		text.setBackground(backgroundColor);
		text.setFont(font);
		text.selectAll();

		return result;
	}

	/**
	 * Locate the given cell editor .
	 * 
	 * @param cellEditor
	 *            A cell editor.
	 */
	private void locateCellEditor(final CellEditor cellEditor) {
		Rectangle rect = TextInputEditPart.this.figure.getBounds().getCopy();

		rect.x = rect.x + FRAME_WIDTH;
		rect.y = rect.y + FRAME_WIDTH;
		rect.height = rect.height - (FRAME_WIDTH * 2);
		rect.width = rect.width - (FRAME_WIDTH * 2);

		getFigure().translateToAbsolute(rect);

		cellEditor.getControl().setBounds(rect.x, rect.y, rect.width,
				rect.height);
		cellEditor.getControl().setVisible(true);
	}

	/**
	 * The direct edit command that changes the input text.
	 */
	private class DirectEditCommand extends Command {
		/**
		 * The entered input text.
		 */
		private String _text;

		/**
		 * Standard constructor.
		 * 
		 * @param text
		 *            The entered input text.
		 */
		public DirectEditCommand(final String text) {
			_text = text;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute() {
			WidgetProperty inputTextProperty = getCastedModel().getProperty(
					TextInputModel.PROP_INPUT_TEXT);

			if (inputTextProperty != null) {
				inputTextProperty.setManualValue(_text);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean canUndo() {
			return false;
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
			DirectEditCommand command = new DirectEditCommand((String) request
					.getCellEditor().getValue());
			return command;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void showCurrentEditValue(final DirectEditRequest request) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// input text
		IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setText((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_INPUT_TEXT, textHandler);

		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				FontData fontData = (FontData) newValue;
				label.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_FONT, fontHandler);
		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setTextAlignment((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT_ALIGNMENT, alignmentHandler);
	}
}
