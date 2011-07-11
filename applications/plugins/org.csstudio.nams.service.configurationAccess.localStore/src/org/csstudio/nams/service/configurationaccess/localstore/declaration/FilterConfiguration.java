
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

public class FilterConfiguration {

	private Collection<FilterDTO> allaFilter;
	
	public FilterConfiguration(Collection<FilterDTO> allaFilter) {
		this.allaFilter = allaFilter;
	}

	public Collection<FilterDTO> gibAlleFilter() {
		return this.allaFilter;
	}
}
