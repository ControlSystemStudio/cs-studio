
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCond_ArrStr.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_FilterCond_ArrStr
 *  (
 *  iFilterConditionRef	INT NOT NULL,
 *  cKeyValue		VARCHAR(16),
 *  sOperator		SMALLINT
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_ArrStr")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
public class StringArFilterConditionDTO extends FilterConditionDTO implements
		HasManuallyJoinedElements {

	/**
	 * Die Compare-Values. Werden manuell zugeordnet.
	 */
	@Transient
	private List<StrgArFiltCondCompValDTO> compareValues = new LinkedList<StrgArFiltCondCompValDTO>();

	@Column(name = "cKeyValue", length = 16)
	private String keyValue;

	@Column(name = "sOperator")
	private short operator;

	@Override
    public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		List<StrgArFiltCondCompValDTO> allCompList = mapper
				.loadAll(StrgArFiltCondCompValDTO.class,
						false);
		for (StrgArFiltCondCompValDTO comp : allCompList) {
			if (comp.getFilterConditionRef() == this.getIFilterConditionID()) {
				mapper.delete(comp);
			}
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StringArFilterConditionDTO)) {
			return false;
		}
		final StringArFilterConditionDTO other = (StringArFilterConditionDTO) obj;
		if (this.compareValues == null) {
			if (other.compareValues != null) {
				return false;
			}
		} else if (!this.compareValues.equals(other.compareValues)) {
			return false;
		}
		if (this.keyValue == null) {
			if (other.keyValue != null) {
				return false;
			}
		} else if (!this.keyValue.equals(other.keyValue)) {
			return false;
		}
		if (this.operator != other.operator) {
			return false;
		}
		return true;
	}

	public List<StrgArFiltCondCompValDTO> getCompareValueList() {
		return new LinkedList<StrgArFiltCondCompValDTO>(
				this.compareValues);
	}

	public List<String> getCompareValueStringList() {
		final List<String> list = new LinkedList<String>();
		for (final StrgArFiltCondCompValDTO value : this
				.getCompareValueList()) {
			list.add(value.getCompValue());
		}
		return list;
	}

	public MessageKeyEnum getKeyValueEnum() {
		return MessageKeyEnum.getEnumFor(this.keyValue);
	}

	public StringRegelOperator getOperatorEnum() {
		return StringRegelOperator.valueOf(this.operator);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.compareValues == null) ? 0 : this.compareValues
						.hashCode());
		result = prime * result
				+ ((this.keyValue == null) ? 0 : this.keyValue.hashCode());
		result = prime * result + this.operator;
		return result;
	}

	@Override
    public void loadJoinData(final Mapper mapper) throws Throwable {
		final List<StrgArFiltCondCompValDTO> alleVergleichswerte = mapper
				.loadAll(StrgArFiltCondCompValDTO.class, true);

		this.compareValues.clear();

		for (final StrgArFiltCondCompValDTO vergleichswert : alleVergleichswerte) {
			if (vergleichswert.getFilterConditionRef() == this
					.getIFilterConditionID()) {
				this.compareValues.add(vergleichswert);
			}
		}
	}

	/**
	 * @param compareValues
	 *            the compareValues to set
	 */
	@SuppressWarnings("unused")
	public void setCompareValues(
			final List<StrgArFiltCondCompValDTO> compareValues) {
		this.compareValues = compareValues;
	}

	public void setKeyValue(final MessageKeyEnum keyValue2) {
		this.keyValue = keyValue2.getStringValue();
	}

	/**
	 * @param keyValue
	 *            the keyValue to set
	 */
	@SuppressWarnings("unused")
	public void setKeyValue(final String keyValue) {
		this.keyValue = keyValue;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	@SuppressWarnings("unused")
	public void setOperator(final short operator) {
		this.operator = operator;
	}

	public void setOperatorEnum(final StringRegelOperator op) {
		this.operator = op.databaseValue();
	}

	@Override
    public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		List<StrgArFiltCondCompValDTO> compList = new ArrayList<StrgArFiltCondCompValDTO>(
				this.compareValues.size());

		List<StrgArFiltCondCompValDTO> allCompList = mapper
				.loadAll(StrgArFiltCondCompValDTO.class,
						false);
		for (StrgArFiltCondCompValDTO comp : allCompList) {
			if (comp.getFilterConditionRef() == this.getIFilterConditionID()) {
				if (!this.compareValues.contains(comp)) {
					mapper.delete(comp);
				} else {
					compList.add(comp);
				}
			}
		}

		for (StrgArFiltCondCompValDTO element : this.compareValues) {
			if (!compList.contains(element)) {
				mapper.save(element);
			}
		}
	}

	@Override
	public String toString() {
		// final StringBuilder resultBuilder = new
		// StringBuilder(this.getClass().getSimpleName());
		final StringBuilder resultBuilder = new StringBuilder(super.toString());

		resultBuilder.append(": ");
		resultBuilder.append(this.compareValues.toString());
		resultBuilder.append(", ");
		resultBuilder.append(this.getKeyValue());
		resultBuilder.append(", ");
		resultBuilder.append(this.getOperatorEnum());
		return resultBuilder.toString();
	}

	/**
	 * @return the keyValue
	 */
	@SuppressWarnings("unused")
	private String getKeyValue() {
		return this.keyValue;
	}

	/**
	 * @return the operator
	 */
	@SuppressWarnings("unused")
	private short getOperator() {
		return this.operator;
	}

}
