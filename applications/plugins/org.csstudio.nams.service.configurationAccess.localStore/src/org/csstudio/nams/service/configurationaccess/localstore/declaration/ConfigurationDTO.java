package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

@Entity
public class ConfigurationDTO {
	//TODO implement methods(move methods in the corresponding DTO's)
	/**
	 * Returns a list of all FilterDTO's
	 * 
	 * @return
	 */
//	public Collection<FilterDTO> getAllFilters(){
//		return null;
//	}
	
	/**
	 * Returns all conjunctive connected FilterConditions of a Filter.
	 * 
	 * @param filter
	 * @return
	 */
//	public Collection<FilterConditionDTO> getFilterConditions(FilterDTO filter){
//		return null;
//	}

	/**
	 * Returns all child FilterConditions of a given FilterCondition
	 * 
	 * @param filterCondition
	 * @return
	 */
	public Collection<FilterConditionDTO> getFilterConditions(FilterConditionDTO filterCondition){
		return null;
	}
	
	
	// String Regel
//	public StringRegelOperator getOperator(){
//		return null;
//	}
//	public MessageKeyEnum getMessageKey(){
//		return null;
//	}
//	public String getCompareString(){
//		return null;
//	}
//	
//	// ArrayString Regel
//	/**
//	 * @return the keyValue
//	 */
//	public String getKeyValue() {
//		return null;
//	}
//
//	/**
//	 * @return the operator
//	 */
//	public short getOperator() {
//		return null;
//	}
//
//	/**
//	 * @return the compareValues
//	 */
//	public List<StringArrayFilterConditionCompareValuesDTO> getCompareValues() {
//		return null;
//	}
//	
//	// TimeBased
//	public MessageKeyEnum getStartKeyValue(){
//		return null;
//	}
//	public StringOperator getStartOperator(){
//		return null;
//	}
//	public String getStartCompareValue(){
//		return null;
//	}
//	public MessageKeyEnum getBestaetigungsAufhebungsKeyValue(){
//		return null;
//	}
//	public StringOperator getBestaetigungsAufhebungsOperator(){
//		return null;
//	}
//	public String getBestaetigungsAufhebungsCompareValue(){
//		return null;
//	}
//	public StringRegel getBestaetigungsAufhebungsRegel(){
//		return null;
//	}
//	public TimeBasedType getTimeBasedType(){
//		return null;
//	}
//	public Millisekunden getDelay(){
//		return null;
//	}
//	//Junktor
//	public CommonConjunctionJunctorMapper getJunctor(){
//		return null;
//	}
//	public FilterCondition getFirstCondition(){
//		return null;
//	}
//	public FilterCondition getSecondCondition(){
//		return null;
//	}
//	//PV
//	public ProcessVariableAdress getProcessVariableAdress(){
//		return null;
//	}
//	public Operator getOperator(){
//		return null;
//	}
//	public SuggestedType getSuggestedType(){
//		return null;
//	}
//	public Object getCompValue(){
//		return null;
//	}
	}
	
