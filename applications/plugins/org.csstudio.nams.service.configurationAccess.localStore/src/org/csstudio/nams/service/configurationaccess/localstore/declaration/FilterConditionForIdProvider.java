/**
 * 
 */
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

public interface FilterConditionForIdProvider {
	FilterConditionDTO getFilterConditionForId(int iD);
}