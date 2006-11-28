package org.csstudio.platform.ui.internal.dnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.swt.dnd.Transfer;

/**
 * This registry defines the default mapping between control system item types
 * and their according transfer handlers.
 * 
 * @author Alexander Will
 * 
 */
public final class TransferRegistry {
	/**
	 * Map that stores the mappings.
	 */
	private static Map<Class, Transfer> _transfers = new HashMap<Class, Transfer>();

	/**
	 * Hidden default constructor.
	 */
	private TransferRegistry() {
	}

	static {
		_transfers.put(IProcessVariable.class, ProcessVariableTransfer
				.getInstance());
		_transfers.put(IArchiveDataSource.class, ArchiveDataSourceTransfer
				.getInstance());
		_transfers.put(IProcessVariableWithArchive.class,
				ProcessVariableWithArchiveTransfer.getInstance());
		_transfers.put(IControlSystemItem.class, ControlSystemItemTransfer
				.getInstance());

	}

	/**
	 * Return the transfer handlers for the given types.
	 * 
	 * @param forTypes
	 *            Control system items types.
	 * @return The transfer handlers for the given types.
	 */
	public static List<Transfer> getTransfers(final Class[] forTypes) {
		List<Transfer> resultList = new ArrayList<Transfer>();

		for (Class c : forTypes) {
			if (_transfers.containsKey(c)) {
				Transfer t = _transfers.get(c);
				resultList.add(t);
			}
		}

		return resultList;
	}

}
