package org.csstudio.dct.util;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;

/**
 * Utility class that helps with the resolution of names.
 * 
 * @author Sven Wende
 * 
 */
public final class ResolutionUtil {
	private ResolutionUtil() {
	}

	/**
	 * Resolves all variables and functions in the specified source String.
	 * 
	 * @param source
	 *            the source String
	 * @param element
	 *            the model element
	 * @return a resolved String
	 * 
	 * @throws AliasResolutionException
	 */
	public static String resolve(String source, IElement element) throws AliasResolutionException {
		Map<String, String> aliases = new HashMap<String, String>();

		if (element instanceof IRecord) {
			IRecord record = (IRecord) element;
			aliases = AliasResolutionUtil.getFinalAliases(record.getContainer());
		} else if (element instanceof IContainer) {
			aliases = AliasResolutionUtil.getFinalAliases((IContainer) element);
		}

		String result = DctActivator.getDefault().getFieldFunctionService().resolve(source, aliases);

		return result;
	}

}
