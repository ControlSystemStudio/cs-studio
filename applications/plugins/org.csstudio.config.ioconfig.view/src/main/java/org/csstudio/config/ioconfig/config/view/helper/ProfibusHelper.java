/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view.helper;

import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges.Value;
import org.csstudio.config.ioconfig.view.Activator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This Class help handel the Unsign Datatyp from Profibus.
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 26.06.2007
 */
public final class ProfibusHelper {

	/** Verify Listener Type ID VL_TYP_U08. */
	public static final int VL_TYP_U08 = 8;
	/** Verify Listener Type ID VL_TYP_U16. */
	public static final int VL_TYP_U16 = 16;
	/** Verify Listener Type ID VL_TYP_U32. */
	public static final int VL_TYP_U32 = 32;
	/** A VerifyListener to check U8 type. */
	private static VerifyListener _checkOfU8;
	/** A VerifyListener to check U16 type. */
	private static VerifyListener _checkOfU16;
	/** A VerifyListener to check U32 type. */
	private static VerifyListener _checkOfU32;
	/** A TraverseListener to jump per enter to the next field. */
	private static TraverseListener _nETL = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				e.detail = SWT.TRAVERSE_TAB_NEXT;
			}
		}
	};

	/** The default Constructor. */
	private ProfibusHelper() {
	}

	/**
	 * Verify input at Text field is confirm with a Profibus U8 (0-255).
	 * 
	 * @param toolTipPos
	 *            Position for the Tool tip.
	 * @return a VerifyListener to check a Text field is confirm with a Profibus
	 *         U8 (0-255)
	 */
	public static VerifyListener getVerifyListenerCheckOfU8(
			final Point toolTipPos) {
		if (_checkOfU8 == null) {
			_checkOfU8 = getNumberVerifyListener(0, 255, toolTipPos);
		}
		return _checkOfU8;
	}

	/**
	 * Verify input at Text field is confirm with a Profibus U16 (0-65535).
	 * 
	 * @param toolTipPos
	 *            Position for the Tool tip.
	 * @return a VerifyListener to check a Text field is confirm with a Profibus
	 *         U16 (0-65535)
	 */

	public static VerifyListener getVerifyListenerCheckOfU16(
			final Point toolTipPos) {
		if (_checkOfU16 == null) {
			_checkOfU16 = getNumberVerifyListener(0, 65535, toolTipPos);
		}
		return _checkOfU16;
	}

	/**
	 * Verify input at Text field is confirm with a Profibus U16 (0-2^32).
	 * 
	 * @param toolTipPos
	 *            Position for the Tool tip.
	 * @return a VerifyListener to check a Text field is confirm with a Profibus
	 *         U16 (0-2^32)
	 */

	public static VerifyListener getVerifyListenerCheckOfU32(
			final Point toolTipPos) {
		if (_checkOfU32 == null) {
			long max = (long) Math.pow(2, 31);
			_checkOfU32 = getNumberVerifyListener(0, max, toolTipPos);
		}
		return _checkOfU32;
	}

	/**
	 * Verify input at Text field is confirm with a Number that is in range Min
	 * - Max.
	 * 
	 * @param min
	 *            the min Value was accept at Text field.
	 * @param max
	 *            the max Value was accept at Text field.
	 * @param toolTipPos
	 *            Position for the Tool tip.
	 * @return a VerifyListener to check a Text field is confirm with a Number
	 *         that is in range Min - Max.
	 */

	public static VerifyListener getNumberVerifyListener(final long min,
			final long max, final Point toolTipPos) {
		return new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				// check != Digit
				boolean b1 = e.text.matches("\\D+");
				// check is first char
				boolean b2 = e.start == 0;
				// check is an '-'
				boolean b3;
				if (min < 0) {
					b3 = e.text.matches("^[\\-]?$");
				} else {
					b3 = false;
				}

				// check is a Digit or is first char an '-'
				if (b1 && !(b2 && b3)) {
					e.doit = false;
				}
				// check ob value more then one Digit for range check (see
				// below)
				if (e.start > 0 || !b3) {
					// e.display.getCurrent().
					String text = ((Text) e.getSource()).getText();
					String temp = text.substring(0, e.start) + e.text;
					if (text.length() >= e.end) {
						temp = temp.concat(text.substring(e.end));
					}
					// long s =0;
					// if(temp.trim().length()>0){
					// s = Long.valueOf(temp);
					// }
					// Es gibt konflikte mit dem Verifiy Listernr
					// // check ob Value in range (min-max) is !
					// if((max<s||s<min)&&temp.trim().length()>0) {
					// Shell shell = Display.getCurrent().getActiveShell();
					// MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					// String msg =
					// String.format("Min(%d) oder Max(%d) Range Überschritten!",min,max);
					// box.setMessage(msg);
					// box.open();
					// e.doit = false;
					// }
				}
			}
		};
	}

	public static VerifyListener getDigitVerifyListener() {
		return new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				e.doit = !e.text.matches("\\D+");
			}
		};
	}

	public static Text getTextField(Composite parent, String text) {
		return getTextField(parent, false, text, null, 0);
	}

	/**
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param edit
	 *            is true the Text field is editable.
	 * @param value
	 *            the Value for the Textfield.
	 * @param ranges
	 * @param verifyListenerTyp
	 *            set The Verification Type.
	 * @return a Text field whit ranges and verify listener.
	 */
	public static Text getTextField(final Composite parent, final boolean edit,
			final String value, final Value ranges, final int verifyListenerTyp) {
		if (parent == null) {
			return null;
		}
		Text textField = new Text(parent, SWT.SINGLE | SWT.RIGHT | SWT.BORDER);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, edit, false,
				1, 1));
		textField.addTraverseListener(_nETL);
		textField.setEditable(edit);
		if (value != null) {
			textField.setText(value);
			textField.setData(value);
		}
		// textField.addKeyListener(_switchFocusAtEnter);
		if (edit) {
			switch (verifyListenerTyp) {
			case VL_TYP_U08:
				textField.addVerifyListener(ProfibusHelper
						.getVerifyListenerCheckOfU8(textField.getLocation()));
				textField.setTextLimit(3);
				break;
			case VL_TYP_U16:
				textField.addVerifyListener(ProfibusHelper
						.getVerifyListenerCheckOfU16(textField.getLocation()));
				textField.setTextLimit(5);
				break;
			case VL_TYP_U32:
				textField.addVerifyListener(ProfibusHelper
						.getVerifyListenerCheckOfU32(textField.getLocation()));
				textField.setTextLimit(10);
				break;
			default:
				break;
			}
			textField.addFocusListener(new CheckNumFocusListener(ranges));
			textField.setToolTipText("The Range is between " + ranges.getMin()
					+ " and " + ranges.getMax());
		}
		return textField;
	}

	/**
	 * 
	 * @return A TraverseListener to jump per enter to the next field.
	 */
	public static TraverseListener getNETL() {
		return _nETL;
	}

	/**
	 * Open a Error Dialog for a access error to a {@link DBClass}.<br>
	 * The Error Msg String for {@link DBClass} get two Parameters (Class Name
	 * and DB Id).<br>
	 * If the {@link DBClass} a {@link NamedDBClass} then give three Parameters
	 * (Class Name, Object name and DB Id).<br>
	 * 
	 * @param shell
	 *            the parent Shell.
	 * @param title
	 *            the Dialog title.
	 * @param errMsg
	 *            the error message.
	 * @param node
	 *            the DBClass with error.
	 * @param e
	 *            the thrown Exception.
	 */
	public static void openErrorDialog(Shell shell, String title,
			String errMsg, DBClass node, Exception e) {
		String format;
		CentralLogger.getInstance().error(ProfibusHelper.class.getSimpleName(), e);
		if (node instanceof NamedDBClass) {
			NamedDBClass nameNode = (NamedDBClass) node;
			format = String.format(errMsg, nameNode.getClass().getSimpleName(),
					nameNode.getName(), nameNode.getId());

		} else {
			format = String.format(errMsg, node.getClass().getSimpleName(),
					"N/A", node.getId());
		}
		OperationStatus status = new OperationStatus(OperationStatus.ERROR,
				Activator.PLUGIN_ID, 3, format, e);
		ErrorDialog.openError(shell, title, null, status);
	}

}
