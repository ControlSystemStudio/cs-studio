package org.csstudio.dct.nameresolution.internal;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.platform.IoNameService;

/**
 * Implementation for the ioname() function.
 * 
 * @author Sven Wende
 * 
 */
public final class IoNameFieldFunction implements IFieldFunction {
	private IoNameService ioNameService;

	/**
	 * Constructor.
	 * 
	 * @param ioNameService
	 *            an IO name service
	 */
	public IoNameFieldFunction(IoNameService ioNameService) {
		this.ioNameService = ioNameService;
	}

	/**
	 *{@inheritDoc}
	 */
	public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
		String result = ioNameService.getIoName(parameters[0], fieldName);
		return result;
	}

}
