package org.csstudio.dal.simple.impl;

import org.apache.log4j.Logger;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.Response;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.Severity;

public abstract class AbstractAnyDataImpl<T> implements AnyData {
	
	private final DynamicValueProperty<T> property;
	protected final Response<T> response;
	private MetaData metaData;
	private long beamID;
	
	public AbstractAnyDataImpl(DynamicValueProperty<T> property, long beamID) {
		this.property = property;
		//Response<T> r= property.getLatestValueResponse();
		response= new ResponseImpl<T>(property, null, confirmValue(this.property.getLatestReceivedValue()), "value", false, null, property.getCondition(), null, true);
		
		if (property.isMetaDataInitialized()) {
			metaData = extractMetaData();
		} else {
			 metaData = MetaDataImpl.createUninitializedMetaData();
		}
		
		this.beamID=beamID;
	}
	
	protected abstract T confirmValue(T value);
	
	public long getBeamID() {
		return beamID;
	}
	
	public AnyDataChannel getParentChannel() {
		return property;
	}

	public DynamicValueProperty<?> getParentProperty() {
		return property;
	}
	
	public MetaData getMetaData() {
		// TODO if MetaData changes this is how it could be reset
//		if (metaData == null) {
//			metaData = extractMetaData();
//		}
		return metaData;
	}
	
	public Quality getQuality() {
		return Quality.Original;
	}

	public Severity getSeverity() {
		return response.getCondition();
	}

	public String getStatus() {
		return DynamicValueConditionConverterUtil.extractStatusInfo(response.getCondition());
	}

	public Timestamp getTimestamp() {
		return DynamicValueConditionConverterUtil.extractTimestampInfo(response.getCondition());
	}
	
	public boolean isValid() {
		// TODO other option: response.getCondition().hasValue()
		return response.getError() == null;
	}
	
	private MetaData extractMetaData() {
		try {
			return (MetaData) property.getCharacteristic(CharacteristicInfo.C_META_DATA.getName());
		} catch (DataExchangeException e) {
			Logger.getLogger(this.getClass()).error("Metadata extraction failed.", e);
			return null;
		}
	}
	
}
