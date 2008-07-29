package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterAction2FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration eines Filters dar
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * Create table AMS_Filter
 * 
 * iFilterID		INT,
 * iGroupRef		INT default -1 NOT NULL,
 * cName			VARCHAR(128),
 * cDefaultMessage	VARCHAR(1024),
 * PRIMARY KEY (iFilterID)
 * ;
 * </pre>
 */
@Entity
@Table(name = "AMS_Filter")
public class FilterDTO implements NewAMSConfigurationElementDTO,
		HasManuallyJoinedElements {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "iFilterID")
	private int iFilterID; // INT,

	@Column(name = "iGroupRef", nullable = false)
	private int iGroupRef = -1; // INT default -1 NOT NULL,

	@Column(name = "cName", length = 128)
	private String name; // VARCHAR(128),

	@Column(name = "cDefaultMessage", length = 1024)
	private String defaultMessage; // VARCHAR(1024),

	@Transient
	private List<FilterConditionDTO> filterConditons = new LinkedList<FilterConditionDTO>();

	@Transient
	private List<FilterActionDTO> filterActions = new LinkedList<FilterActionDTO>();

	public int getIFilterID() {
		return iFilterID;
	}

	@SuppressWarnings("unused")
	private void setIFilterID(int filterID) {
		iFilterID = filterID;
	}

	/**
	 * Kategorie
	 */
	public int getIGroupRef() {
		return iGroupRef;
	}

	@SuppressWarnings("unused")
	public void setIGroupRef(int groupRef) {
		iGroupRef = groupRef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": ");
		builder.append("iFilterID: ");
		builder.append(iFilterID);
		builder.append(", iGroupRef: ");
		builder.append(iGroupRef);
		builder.append(", cName: ");
		builder.append(name);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((defaultMessage == null) ? 0 : defaultMessage.hashCode());
		result = prime * result + iFilterID;
		result = prime * result + iGroupRef;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterDTO))
			return false;
		final FilterDTO other = (FilterDTO) obj;
		if (defaultMessage == null) {
			if (other.defaultMessage != null)
				return false;
		} else if (!defaultMessage.equals(other.defaultMessage))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (iFilterID != other.iFilterID)
			return false;
		if (iGroupRef != other.iGroupRef)
			return false;
		return true;
	}

	public List<FilterConditionDTO> getFilterConditions() {
		return filterConditons;
	}

	public void setFilterConditions(List<FilterConditionDTO> filterConditonDTOs) {
		filterConditons = filterConditonDTOs;
	}

	public String getUniqueHumanReadableName() {
		return toString();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		List<FilterConditionDTO> alleFilterConditions = mapper.loadAll(FilterConditionDTO.class, true);
		
		List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
				FilterConditionsToFilterDTO.class, true);
		
		for (FilterConditionsToFilterDTO fctf : joins) {
			if (fctf.getIFilterRef() == this.iFilterID) {
				mapper.delete(fctf);
			}
		}
		

		for (FilterConditionDTO condition : getFilterConditions()) {
			if (condition instanceof JunctorConditionForFilterTreeDTO || condition instanceof NegationConditionForFilterTreeDTO) {
				FilterConditionDTO foundFC = findForId(condition.getIFilterConditionID(), alleFilterConditions);
					((HasManuallyJoinedElements) foundFC)
							.deleteJoinLinkData(mapper);
				
				mapper.delete(foundFC);
			}
		}
	}
	
	private <T extends FilterConditionDTO> T findForId(int id, Collection<T> fcs) {
		for (T t : fcs) {
			if( t.getIFilterConditionID() == id ) {
				return t;
			}
		}
		return null;
	}
	
	private FilterConditionsToFilterDTO findForId(int id, Collection<FilterConditionsToFilterDTO> fcs) {
		for (FilterConditionsToFilterDTO t : fcs) {
			if( t.getIFilterConditionRef() == id ) {
				return t;
			}
		}
		return null;
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		List<FilterConditionDTO> allFC = mapper.loadAll(FilterConditionDTO.class, true);
		List<FilterConditionsToFilterDTO> joins = mapper.loadAll(FilterConditionsToFilterDTO.class, true);
		
		
		List<FilterConditionDTO> ehemalsReferenziert = new LinkedList<FilterConditionDTO>();
		
		for (FilterConditionsToFilterDTO join : joins) {
			if (join.getIFilterRef() == this.getIFilterID()) {
				FilterConditionDTO found = findForId(join.getIFilterConditionRef(), allFC);
				ehemalsReferenziert.add(found);
			}
		}
		
		List<FilterConditionDTO> operands = this.getFilterConditions();
		
		for (FilterConditionDTO operand : operands) {
			FilterConditionDTO fc = findForId(operand.getIFilterConditionID(), allFC);
			
			if (fc != null) {
				if (!ehemalsReferenziert.remove(fc)) {
					FilterConditionsToFilterDTO newJoin = new FilterConditionsToFilterDTO(this.getIFilterID(), fc.getIFilterConditionID());
					mapper.save(newJoin);
				}
				if (operand instanceof JunctorConditionForFilterTreeDTO || operand instanceof NegationConditionForFilterTreeDTO) {
					((HasManuallyJoinedElements)operand).storeJoinLinkData(mapper);
				}
			} else {
				mapper.save(operand);
				FilterConditionsToFilterDTO newJoin = new FilterConditionsToFilterDTO(this.getIFilterID(), operand.getIFilterConditionID());
				mapper.save(newJoin);
			}
		}
		
		for (FilterConditionDTO toRemove : ehemalsReferenziert) {
			FilterConditionsToFilterDTO found = findForId(toRemove.getIFilterConditionID(), joins);
			mapper.delete(found);
			if (toRemove instanceof JunctorConditionForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
			if (toRemove instanceof NegationConditionForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
		}
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<FilterConditionDTO> alleFilterConditions = mapper.loadAll(FilterConditionDTO.class, true);
		List<FilterConditionsToFilterDTO> joins = mapper.loadAll(FilterConditionsToFilterDTO.class, true);
		
		List<FilterActionDTO> alleFilterActions = mapper.loadAll(FilterActionDTO.class, true);
		List<FilterAction2FilterDTO> actionJoins = mapper.loadAll(FilterAction2FilterDTO.class, true);
		Collections.sort(actionJoins, new Comparator<FilterAction2FilterDTO>() {
			public int compare(FilterAction2FilterDTO o1,
					FilterAction2FilterDTO o2) {
				return o2.getIPos()-o1.getIPos();
			}
		});
		
		Map<Integer, FilterConditionDTO> filterConditionNachSchluessel = new HashMap<Integer, FilterConditionDTO>();
		for (FilterConditionDTO filterCondition : alleFilterConditions) {
			filterConditionNachSchluessel.put(filterCondition.getIFilterConditionID(), filterCondition);
		}
		Map<Integer, FilterActionDTO> filterActionMap = new HashMap<Integer, FilterActionDTO>();
		for (FilterActionDTO filterAction : alleFilterActions) {
			filterActionMap.put(filterAction.getIFilterActionID(), filterAction);
		}
		
		
		filterConditons.clear();
		
		for (FilterConditionsToFilterDTO join : joins) {
			if( join.getIFilterRef() == this.getIFilterID() ) {
				FilterConditionDTO gefunden = filterConditionNachSchluessel.get(join.getIFilterConditionRef());
				assert gefunden != null : "Es existiert eine FC mit der ID " + join.getIFilterConditionRef();
				
				filterConditons.add(gefunden);
			}
		}
		
		filterActions.clear();
		
		for (FilterAction2FilterDTO actionJoin : actionJoins) {
			if ( actionJoin.getId().getIFilterRef() == this.getIFilterID() ) {
				FilterActionDTO foundAction = filterActionMap.get(actionJoin.getId().getIFilterActionRef());
				assert foundAction != null : "Es existiert eine Action mit der ID " + actionJoin.getId().getIFilterActionRef();
				
				filterActions.add(foundAction);
			}
		}
	}

}
