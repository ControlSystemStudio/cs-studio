
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

package org.csstudio.ams.filter.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

import org.csstudio.ams.Log;
import org.csstudio.ams.Messages;
import org.csstudio.ams.MyRunnable;
import org.csstudio.ams.dbAccess.ItemInterface;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class FilterConditionUI implements IFilterConditionUI {
	public final GridData getGridData(int width, int height, int cellWith,
			int cellHeight, int hAlign, int vAlign, boolean hGrab, boolean vGrab) {
		GridData gd = new GridData(width, height);

		gd.horizontalSpan = cellWith;
		gd.verticalSpan = cellHeight;
		gd.horizontalAlignment = hAlign;
		gd.verticalAlignment = vAlign;
		gd.grabExcessHorizontalSpace = hGrab;
		gd.grabExcessVerticalSpace = vGrab;

		return gd;
	}

	public int showMessageDialog(Shell shell, String text, String title,
			int style) {
		MyRunnable myRun = new MyRunnable(shell, text, title,
				new Integer(style)) {
			@Override
            public void run() {
				MessageBox msg = new MessageBox((Shell) objGui,
						((Integer) obj4).intValue());
				msg.setMessage((String) objData);
				msg.setText((String) obj3);
				objRet = new Integer(msg.open());
			}
		};
		shell.getDisplay().syncExec(myRun);
		return ((Integer) myRun.objRet).intValue();
	}
	
	/**
	 * Shows a message box with a list of given messages.
	 * 
	 * For further details see {@link MessageBox}.
	 */
	public static int showMessageDialog(Shell shell, List<String> texts, String title,
			int style) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(Messages.FilterConditionUI_Error_Dialog_Message_Prefix);
		
		for(String text : texts) {
			buffer.append("\n");
			buffer.append(text);
		}
		
		MyRunnable myRun = new MyRunnable(shell, buffer.toString(), title,
				new Integer(style)) {
			@Override
            public void run() {
				Shell internalShell = (Shell) objGui;
				if (internalShell==null) {
					internalShell = Display.getCurrent().getActiveShell();
				}
				MessageBox msg = new MessageBox(internalShell,
						((Integer) obj4).intValue());
				msg.setMessage((String) objData);
				msg.setText((String) obj3);
				objRet = new Integer(msg.open());
			}
		};
		Display.getDefault().syncExec(myRun);
		return ((Integer) myRun.objRet).intValue();
	}

	/**
	 * Internal realization of method: {@link #initComboBoxUI(Display, Combo, List)}.
	 */
	private static void initComboBoxIntern(Combo cbo, List<?> list) {
		try {
			cbo.removeAll();
			cbo.setData(list);

			for (int i = 0; i < list.size(); i++)
				if (list.get(i) != null)
					cbo.add(list.get(i).toString());
		} catch (Exception ex) {
			Log.log(Log.FATAL, ex);
		}
	}

	/**
	 * Initialize the given combo box with given {@link ItemInterface}
	 * instances.
	 * 
	 * @param display
	 *            Current Display.
	 * @param cbo
	 *            Combo to be initialized.
	 * @param list
	 *            Values of the Combo (Have to contain instances of
	 *            ItemInterface).
	 * 
	 * TODO Param list should be instance of List<ItemInterface>, change was
	 * not performed cause of inconsistency of API (for example see
	 * {@link #getItem(Display, Combo, int)} which is required a String to be present).
	 */
	public static void initComboBoxUI(Display display, Combo cbo, List<?> list) {
		display.syncExec(new MyRunnable(cbo, list) {
			@Override
            public void run() {
				initComboBoxIntern(((Combo) objGui), (List<?>) objData);
			}
		});
	}

	private static Object setComboBoxValue(Combo cbo, int id) {
		List list = (List) cbo.getData();
		ItemInterface item = null;
		int idx = 0;

		for (; idx < list.size(); idx++) {
			item = (ItemInterface) list.get(idx);
			if (item.getID() == id)
				break;
			item = null;
		}

		if (item != null)
			cbo.select(idx);
		else
			cbo.deselectAll();
		return item;
	}

	public static void setComboBoxValueUI(Display display, Combo cbo, int id) {
		display.syncExec(new MyRunnable(cbo, new Integer(id)) {
			@Override
            public void run() {
				setComboBoxValue((Combo) objGui, ((Integer) objData).intValue());
			}
		});
	}

	private static Object setComboBoxValue(Combo cbo, String value) {
		List list = (List) cbo.getData();

		String item = null;
		int idx = 0;

		for (; idx < list.size(); idx++) {
			item = (String) cbo.getItem(idx);
			if (item.compareTo(value) == 0)
				break;
			item = null;
		}

		if (item != null)
			cbo.select(idx);
		else
			cbo.deselectAll();
		return item;
	}

	public static void setComboBoxValueUI(Display display, Combo cbo,
			String value) {
		display.syncExec(new MyRunnable(cbo, value) {
			@Override
            public void run() {
				setComboBoxValue((Combo) objGui, (String) objData);
			}
		});
	}

	/**
	 * @return The id value of the contained ItemInterface.
	 */
	private static int getComboBoxValueIntern(Combo cbo) {
		int idx = cbo.getSelectionIndex();

		if (idx < 0 || cbo.getData() == null)
			return -1;

		return ((ItemInterface) ((List) cbo.getData()).get(idx)).getID();
	}

	/**
	 * Gets the id of current selected item. Runns in synchronized ui thread.
	 * 
	 * @return -1 if no selection is avail, >= 0 else.
	 */
	public int getSelectedComboBoxIdValueUI(Display display, Combo cbo) {
		MyRunnable myRun = new MyRunnable(cbo) {
			@Override
            public void run() {
				int iRet = getComboBoxValueIntern(((Combo) objGui));
				objRet = new Integer(iRet);
			}
		};
		display.syncExec(myRun);
		return ((Integer) myRun.objRet).intValue();
	}

	private static Object getSelectedComboBoxItem(Combo cbo) {
		int idx = cbo.getSelectionIndex();

		if (idx < 0 || cbo.getData() == null)
			return null;

		return ((List) cbo.getData()).get(idx);
	}

	public Object getSelectedComboBoxItemUI(Display display, Combo cbo) {
		MyRunnable myRun = new MyRunnable(cbo) {
			@Override
            public void run() {
				objRet = getSelectedComboBoxItem(((Combo) objGui));
			}
		};
		display.syncExec(myRun);
		return myRun.objRet;
	}

	private static ItemInterface[] getSelectedValues(
			org.eclipse.swt.widgets.List lst) {
		Vector<ItemInterface> vec = new Vector<ItemInterface>();

		int[] idx = lst.getSelectionIndices();

		List array = (List) lst.getData();

		for (int i = 0; i < idx.length; i++)
			vec.add((ItemInterface) array.get(idx[i]));

		return vec.toArray(new ItemInterface[0]);
	}

	public static ItemInterface[] getSelectedValuesUI(Display display,
			org.eclipse.swt.widgets.List lst) {
		MyRunnable myRun = new MyRunnable(lst) {
			@Override
            public void run() {
				objRetAr = getSelectedValues((org.eclipse.swt.widgets.List) objGui);
			}
		};
		display.syncExec(myRun);
		return (ItemInterface[]) myRun.objRetAr;
	}

	public boolean isEmpty(Display display, Text txt) {
		MyRunnable myRun = new MyRunnable(txt) {
			@Override
            public void run() {
				String text = ((Text) objGui).getText();
				if (text == null || text.trim().length() == 0) {
					objRet = new Boolean(true);
					return;
				}
				objRet = new Boolean(false);
			}
		};
		display.syncExec(myRun);
		return ((Boolean) myRun.objRet).booleanValue();
	}

	public static Object cloneObject(Object o) {
		ObjectOutputStream os = null;
		ObjectInputStream is = null;
		Object result = null;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			os = new ObjectOutputStream(out);
			os.writeObject(o);
			os.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out
					.toByteArray());
			is = new ObjectInputStream(in);
			result = is.readObject();
		} catch (Exception ex) {
			Log.log(Log.FATAL, ex);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			    // Can be ignored
			}
			try {
				if (os != null)
					os.close();
			} catch (Exception ex) {
                // Can be ignored
			}
		}
		return result;
	}

	public void refresh(Display display, TableViewer tblv) {
		display.syncExec(new MyRunnable(tblv) {
			@Override
            public void run() {
				((TableViewer) objGui).refresh();
			}
		});
	}

	public void setText(Display display, Text txt, String strtext) {
		display.syncExec(new MyRunnable(txt, strtext) {
			@Override
            public void run() {
				((Text) objGui).setText((String) objData);
			}
		});
	}

	public void setEnabled(Display display, Text txt, boolean bEn) {
		display.syncExec(new MyRunnable(txt, new Boolean(bEn)) {
			@Override
            public void run() {
				((Text) objGui).setEnabled(((Boolean) objData).booleanValue());
			}
		});
	}

	public void setEnabled(Display display, Combo cbo, boolean bEn) {
		display.syncExec(new MyRunnable(cbo, new Boolean(bEn)) {
			@Override
            public void run() {
				((Combo) objGui).setEnabled(((Boolean) objData).booleanValue());
			}
		});
	}

	public void deselectAll(Display display, Combo cbo) {
		display.syncExec(new MyRunnable(cbo) {
			@Override
            public void run() {
				((Combo) objGui).deselectAll();
			}
		});
	}

	public void addVerifyListener(Display display, Text txt, VerifyListener obj) {
		display.syncExec(new MyRunnable(txt, obj) {
			@Override
            public void run() {
				((Text) objGui).addVerifyListener((VerifyListener) objData);
			}
		});
	}

	public void removeVerifyListener(Display display, Text txt,
			VerifyListener obj) {
		display.syncExec(new MyRunnable(txt, obj) {
			@Override
            public void run() {
				((Text) objGui).removeVerifyListener((VerifyListener) objData);
			}
		});
	}

	public void setSelection(Display display, Button bto, Boolean b) {
		display.syncExec(new MyRunnable(bto, b) {
			@Override
            public void run() {
				((Button) objGui).setSelection(((Boolean) objData)
						.booleanValue());
			}
		});
	}

	public String getText(Display display, Label lbl) {
		MyRunnable myRun = new MyRunnable(lbl) {
			@Override
            public void run() {
				objRet = ((Label) objGui).getText();
			}
		};
		display.syncExec(myRun);
		return (String) myRun.objRet;
	}

	public String getText(Display display, Text txt) {
		MyRunnable myRun = new MyRunnable(txt) {
			@Override
            public void run() {
				objRet = ((Text) objGui).getText();
			}
		};
		display.syncExec(myRun);
		return (String) myRun.objRet;
	}

	public int getSelectionIndex(Display display, Combo cbo) {
		MyRunnable myRun = new MyRunnable(cbo) {
			@Override
            public void run() {
				objRet = new Integer(((Combo) objGui).getSelectionIndex());
			}
		};
		display.syncExec(myRun);
		return ((Integer) myRun.objRet).intValue();
	}

	/**
	 * Pay attention: This method require a String-value as combo content.
	 */
	public String getItem(Display display, Combo cbo, int idx) {
		MyRunnable myRun = new MyRunnable(cbo, new Integer(idx)) {
			@Override
            public void run() {
				objRet = ((Combo) objGui).getItem(((Integer) objData)
						.intValue());
			}
		};
		display.syncExec(myRun);
		return (String) myRun.objRet;
	}

	public boolean getSelection(Display display, Button bto) {
		MyRunnable myRun = new MyRunnable(bto) {
			@Override
            public void run() {
				objRet = new Boolean(((Button) objGui).getSelection());
			}
		};
		display.syncExec(myRun);
		return ((Boolean) myRun.objRet).booleanValue();
	}

}
