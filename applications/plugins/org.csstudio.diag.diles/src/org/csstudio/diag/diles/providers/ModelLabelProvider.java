package org.csstudio.diag.diles.providers;

import java.util.List;

import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.CommandTrueFalse;
import org.csstudio.diag.diles.model.HardwareOut;
import org.csstudio.diag.diles.model.HardwareTrueFalse;
import org.csstudio.diag.diles.model.Status;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ModelLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private static int i = 0;
	private static int j1 = 0;
	private static int k = 0;
	private static int l = 0;
	private static int m = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Very ugly hack to refresh table viewer content after 'save' in editor.
	 * But it is the easiest way to use this implementation of the provider.
	 * TODO: rewrite LabelProvider!!!
	 *
	 */
	public static void resetStrangeNumbers() {
		i = 0;
		j1 = 0;
		k = 0;
		l = 0;
		m = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		List activity = (List) element;
		if (j1 > i) {
			i = j1 - 1;
		}
		if (k > i) {
			i = k - 1;
		}
		if (l > i) {
			i = l - 1;
		}
		if (m > i) {
			i = m - 1;
		}

		System.out.println("c: " + columnIndex + " j: " + j1 + " i: " + i);

		switch (columnIndex) {
		case 0:
			return String.valueOf(i);
		case 1:
			j1++;
			for (int j = 0; j < activity.size(); j++) {
				if (activity.get(j) instanceof CommandTrueFalse) {
					if (((Activity) activity.get(j)).getNumberId() == i) {
						return String.valueOf(((Activity) activity.get(j))
								.getResult())
								+ " " + ((Activity) activity.get(j)).getName();
					}
				}
			}
			return null;
		case 2:
			k++;
			for (int j = 0; j < activity.size(); j++) {
				if (activity.get(j) instanceof Status) {
					if (((Activity) activity.get(j)).getNumberId() == i) {
						return String.valueOf(((Activity) activity.get(j))
								.getResult())
								+ " " + ((Activity) activity.get(j)).getName();
					}
				}
			}
			return null;
		case 3:
			l++;
			for (int j = 0; j < activity.size(); j++) {
				if (activity.get(j) instanceof HardwareTrueFalse) {
					if (((Activity) activity.get(j)).getNumberId() == i) {
						return String.valueOf(((Activity) activity.get(j))
								.getResult())
								+ " " + ((Activity) activity.get(j)).getName();
					}
				}
			}
			return null;
		case 4:
			m++;
			for (int j = 0; j < activity.size(); j++) {
				if (activity.get(j) instanceof HardwareOut) {
					if (((Activity) activity.get(j)).getNumberId() == i) {
						return String.valueOf(((Activity) activity.get(j))
								.getResult())
								+ " " + ((Activity) activity.get(j)).getName();
					}
				}
			}
			return null;
		default:
			throw new RuntimeException("This should not happen!");
		}
	}

}
