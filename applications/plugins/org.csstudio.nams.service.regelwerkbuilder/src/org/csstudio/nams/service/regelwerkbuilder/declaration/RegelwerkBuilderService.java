
package org.csstudio.nams.service.regelwerkbuilder.declaration;

import java.util.List;

import org.csstudio.nams.common.material.regelwerk.Regelwerk;

public interface RegelwerkBuilderService {

	/**
	 * Loads all {@link Regelwerk}-elements from configured configuration.
	 * 
	 * @return A unmodifyable list of {@link Regelwerk}, not null.
	 * @throws RegelwerksBuilderException
	 *             If an error occurs on loading or creating {@link Regelwerk}-elements.
	 */
	public List<Regelwerk> gibAlleRegelwerke()
			throws RegelwerksBuilderException;
	
	/**
	 * Gib alle {@link Regelwerk}-elemente der Konfiguration, außer denen, deren Filter 
	 * genau eine Filterbedingung hat, die vom Typ "StringFilter" ist und dessen 
	 * Filter-Operator "OPERATOR_TEXT_EQUAL" ist.
	 */ 
	public List<Regelwerk> gibKomplexeRegelwerke()
			throws RegelwerksBuilderException;
}
