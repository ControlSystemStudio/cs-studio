package org.csstudio.config.kryonamebrowser.ui.provider;

import java.util.List;

import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoPlantResolved;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class KryoNameLabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		KryoNameResolved resolved = (KryoNameResolved) element;

		switch (columnIndex) {
		case 0:
			return resolved.getName();
		case 1:
			return getPlantName(resolved.getPlants(), 0);
		case 2:
			return getPlantNo(resolved.getPlants(), 0);
		case 3:
			return getPlantName(resolved.getPlants(), 1);
		case 4:
			return getPlantNo(resolved.getPlants(), 1);
		case 5:
			return getPlantName(resolved.getPlants(), 2);
		case 6:
			return getPlantNo(resolved.getPlants(), 2);
		case 7:
			return getPlantName(resolved.getPlants(), 3);
		case 8:
			return getPlantNo(resolved.getPlants(), 3);
		case 9:
			return getObjectName(resolved.getObjects(), 0);
		case 10:
			return getObjectName(resolved.getObjects(), 1);
		case 11:
			return getObjectName(resolved.getObjects(), 2);
		case 12:
			return resolved.getProcess().getName();
		case 13:
			return "" + resolved.getSeqKryoNumber();
		case 14:
			String label = resolved.getLabel();
			if (label == null || (label.length() == 0)) {
				return "";
			}
			return label.substring(0, Math.min(30, label.length()));
		}

		return "Should not see this";
	}

	private String getPlantName(List<KryoPlantResolved> entry, int index) {

		if (entry.size() > index) {
			return entry.get(index).getName();
		}

		return "";

	}

	private String getPlantNo(List<KryoPlantResolved> entry, int index) {

		if (entry.size() > index) {
			int number = entry.get(index).getNumberOfPlants();
			return number < 0 ? "" : "" + number;
		}

		return "";

	}

	private String getObjectName(List<KryoObjectEntry> entry, int index) {

		if (entry.size() > index) {
			return entry.get(index).getName();
		}

		return "";

	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

}
