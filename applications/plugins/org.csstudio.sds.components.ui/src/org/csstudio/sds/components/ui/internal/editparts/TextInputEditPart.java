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

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EditPart controller for <code>TextInputModel</code> elements with support for
 * direct editing.
 *
 * EditPart controller for the TextInput widget. The controller mediates between
 * {@link TextInputModel} and {@link RefreshableLabelFigure}.
 *
 * @author Alexander Will
 *
 */
public final class TextInputEditPart extends AbstractTextTypeWidgetEditPart {

	private static final Logger LOG = LoggerFactory.getLogger(TextInputEditPart.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		TextInputModel model = (TextInputModel) getWidgetModel();

		RefreshableLabelFigure label = new RefreshableLabelFigure();

		label.setTextValue(determineLabel(null));
		label.setFont(getModelFont(TextInputModel.PROP_FONT));
		label.setTextAlignment(model.getTextAlignment());
		label.setTransparent(model.getTransparent());

		label.setEnabled(model.isAccesible() && getExecutionMode().equals(ExecutionMode.RUN_MODE));

		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPoliciesHook() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(final Request req) {
		Object type = req.getType();

		// entering a value is only allowed in run mode and when the widget is
		// enabled
		if ((type != null) && (type.equals(RequestConstants.REQ_OPEN) || type.equals(RequestConstants.REQ_DIRECT_EDIT))) {
			performDirectEdit();
		}
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
		final CellEditor result = new TextCellEditor((Composite) getViewer().getControl());

		// init cell editor...
		String currentValue = "N/A"; //$NON-NLS-1$
		currentValue = getWidgetModel().getStringProperty(TextInputModel.PROP_INPUT_TEXT);

		result.setValue(currentValue);
		final Text text = (Text) result.getControl();
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)) {
					int option = getWidgetModel().getArrayOptionProperty(TextInputModel.PROP_TEXT_TYPE);

					TextTypeEnum propertyValue = TextTypeEnum.values()[option];
					if (!propertyValue.isValidFormat(text.getText())) {
						InvalidFormatDialog dialog = new InvalidFormatDialog(Display.getCurrent().getActiveShell());
						dialog.setText(text.getText());
						dialog.open();
						LOG.warn("Invalid value format: " + text.getText());
						return;
					}
					DirectEditCommand cmd = new DirectEditCommand(text.getText(), getExecutionMode());
					// In EDIT mode use the CommandStack provided by the DisplayEditor to execute the command.
					if (getExecutionMode() == ExecutionMode.EDIT_MODE) {
						getViewer().getEditDomain().getCommandStack().execute(cmd);
					} else {
						cmd.execute();
					}
				} else if (e.keyCode == SWT.ESC) {
					result.deactivate();
				}
			}

		});
		text.addVerifyListener(new VerifyListener() {

			public void verifyText(final VerifyEvent e) {
				e.doit = true;
				int option = getWidgetModel().getArrayOptionProperty(TextInputModel.PROP_TEXT_TYPE);
				TextTypeEnum propertyValue = TextTypeEnum.values()[option];
				e.doit = propertyValue.isValidChars(e.character, e.text, e.start);

			}
		});

		text.setForeground(getModelColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
		text.setFont(getModelFont(TextInputModel.PROP_FONT));

		// calculate background color
		RGB backgroundRgb = getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND).getRGB();

		int red = Math.min(backgroundRgb.red + INPUT_FIELD_BRIGHTNESS, 255);
		int green = Math.min(backgroundRgb.green + INPUT_FIELD_BRIGHTNESS, 255);
		int blue = Math.min(backgroundRgb.blue + INPUT_FIELD_BRIGHTNESS, 255);

		Color backgroundColor = CustomMediaFactory.getInstance().getColor(new RGB(red, green, blue));

		text.setBackground(backgroundColor);
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
		rect.height = rect.height - (FRAME_WIDTH * 1);
		rect.width = rect.width - (FRAME_WIDTH * 1);
		getFigure().translateToAbsolute(rect);

		cellEditor.getControl().setBounds(rect.x, rect.y, rect.width, rect.height);
		cellEditor.getControl().setLayoutData(new GridData(SWT.CENTER));
		cellEditor.getControl().setVisible(true);
	}

	private final class InvalidFormatDialog extends Dialog {
		private String _text;

		private InvalidFormatDialog(final Shell parentShell) {
			super(parentShell);
			setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE);
			this.setText("Titel");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			shell.setText("Invalid format");
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			composite.setLayout(new GridLayout(2, false));
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			label.setText("Invalid format of new value: ");
			Text text = new Text(composite, SWT.NONE);
			text.setText(_text);
			text.setEditable(false);
			text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			return composite;
		}

		@Override
		protected Control createButtonBar(final Composite parent) {
			Control createButtonBar = super.createButtonBar(parent);
			getButton(IDialogConstants.OK_ID).setText("Copy");
			return createButtonBar;
		}

		@Override
		protected void okPressed() {
			Clipboard clipboard = new Clipboard(Display.getCurrent());
			TextTransfer textTransfer = TextTransfer.getInstance();
			Transfer[] transfers = new Transfer[] { textTransfer };
			Object[] data = new Object[] { _text };
			clipboard.setContents(data, transfers);
			clipboard.dispose();
			super.okPressed();
		}

		public void setText(final String text) {
			_text = text;
		}

	}

	/**
	 * The direct edit command that changes the input text.
	 */
	private class DirectEditCommand extends Command {
		/**
		 * The entered input text.
		 */
		private final String _text;
		private final ExecutionMode _executionMode;
		private String _oldValue;

		/**
		 * Standard constructor.
		 *
		 * @param text
		 *            The entered input text.
		 */
		public DirectEditCommand(final String text, final ExecutionMode executionMode) {
			_text = text;
			_executionMode = executionMode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute() {
			execute(_text);
		}

		private void execute(final String newText) {
			_oldValue = getWidgetModel().getStringProperty(TextInputModel.PROP_INPUT_TEXT);

			if (_executionMode == ExecutionMode.RUN_MODE) {
				// In RUN mode set the manual value, because the connected
				// channel sets the
				// property value.
				getCastedModel().setPropertyManualValue(TextInputModel.PROP_INPUT_TEXT, newText);
			} else {
				// In EDIT mode we can set the property value directly.
				getCastedModel().setPropertyValue(TextInputModel.PROP_INPUT_TEXT, newText);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean canUndo() {
			// Only in EDIT mode the command can be undone.
			return _executionMode == ExecutionMode.EDIT_MODE;
		}

		@Override
		public void undo() {
			execute(_oldValue);
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
			DirectEditCommand command = new DirectEditCommand((String) request.getCellEditor().getValue(), getExecutionMode());
			return command;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void showCurrentEditValue(final DirectEditRequest request) {
		    // do nothing
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
	    super.registerPropertyChangeHandlers();
		// input text
		IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setTextValue(determineLabel(TextInputModel.PROP_INPUT_TEXT));
				return true;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_INPUT_TEXT, textHandler);

		// font
		setPropertyChangeHandler(TextInputModel.PROP_FONT, new FontChangeHandler<RefreshableLabelFigure>() {
			@Override
			protected void doHandle(final RefreshableLabelFigure refreshLableFigure, final Font font) {
				refreshLableFigure.setFont(font);
			}
		});

		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setTextAlignment((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_TEXT_ALIGNMENT, alignmentHandler);

		// transparent background
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_TRANSPARENT, transparentHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	public IValue getSample(final int index) {
		if (index != 0) {
			throw new IndexOutOfBoundsException(index + " is not a valid sample index");
		}

		TextInputModel model = (TextInputModel) getWidgetModel();
		ITimestamp timestamp = TimestampFactory.now();

		// Note: the IValue implementations require a Severity, otherwise the
		// format() method will throw a NullPointerException. We don't really
		// have a severity here, so we fake one. This may cause problems for
		// clients who rely on getting a meaningful severity from the IValue.
		ISeverity severity = ValueFactory.createOKSeverity();

		IValue result;
		switch (model.getValueType()) {
		case DOUBLE:
			// try to convert the input text to a double
			double value = 0.0;
			try {
				value = Double.parseDouble(model.getInputText());
			} catch (NumberFormatException e) {
				// ProcessVariableWithSamples doesn't define
				// what to do in case of error and there aren't any declared
				// checked exceptions for this method. So, the best we can
				// do is to rethrow an unchecked exception and hope that the
				// caller will handle it.
				throw new IllegalStateException("Text input type is Double," + " but text is not a floating point value.", e);
			}
			// Have to create a meta data object because otherwise DoubleValue's
			// format() method might throw a NullPointerException :(
			int precision = model.getPrecision();
			INumericMetaData md = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, precision, "");

			result = ValueFactory.createDoubleValue(timestamp, severity, null, md, Quality.Original, new double[] { value });
			break;
		case TEXT:
		case HEX: // hex and alias are undocumented, so treating them
		case ALIAS: // like text for now
			result = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original, new String[] { model.getInputText() });
			break;
		default:
			throw new AssertionError("Never get here");
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		// we always have one sample
		return 1;
	}

}
