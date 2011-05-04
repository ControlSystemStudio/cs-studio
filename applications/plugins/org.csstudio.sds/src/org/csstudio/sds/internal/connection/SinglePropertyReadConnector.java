package org.csstudio.sds.internal.connection;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ValueType;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;

public class SinglePropertyReadConnector implements ChannelListener {
	private final ChannelInputProcessor channelInputProcessor;
	private final ValueType valueType;
	private org.epics.css.dal.context.ConnectionState latestConnectionState;
	private final String characteristic;

	public SinglePropertyReadConnector(final ChannelInputProcessor channelInputProcessor, final ValueType valueType, final String characteristic) {
		assert channelInputProcessor != null;
		assert valueType != null;
		this.channelInputProcessor = channelInputProcessor;
		this.valueType = valueType;
		this.characteristic = characteristic;
	}

	/**
	 *{@inheritDoc}
	 */
	public void channelDataUpdate(final AnyDataChannel channel) {
		if (characteristic != null) {
			try {
				// FIXME: 26.03.2010: swende: asynchrones Abgreifen der Characteristics bzw. anderen Weg bei Igor erfragen!
				Object cc = channel.getProperty().getCharacteristic(characteristic);
				if (cc instanceof DynamicValueCondition) {
                    DynamicValueCondition dvc = (DynamicValueCondition) cc;
                    channelInputProcessor.valueChanged(dvc.descriptionToString());

                }else {
                    channelInputProcessor.valueChanged(cc);
                }
			} catch (DataExchangeException e) {
				CentralLogger.getInstance().error(this, e);
			}
		} else {
			// .. handle value
			AnyData data = channel.getData();

			if (data.isValid()) {
				channelInputProcessor.valueChanged(getTypesafeValue(data));
			}
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public void channelStateUpdate(final AnyDataChannel channel) {
		org.epics.css.dal.context.ConnectionState state = channel.getProperty().getConnectionState();

		// .. handle connection state
		if (latestConnectionState != state) {
			latestConnectionState = state;
			channelInputProcessor.connectionStateChanged(latestConnectionState);
		}
	}

	private Object getTypesafeValue(final AnyData data) {
		assert data != null;
		assert data.isValid();

		/*
		 *  TODO 30.06.2010 (hrickens) Der Workaround wird jetzt in der Klassse
		 *  {@link ConnectionUtilNew#connectDynamizedProperties} behandelt.
		 *  TODO workaround until Enum data type is introduced
		 */

		switch (valueType) {
		case ENUM:
			return data.anyValue();
		case DOUBLE:
			return data.doubleValue();
		case DOUBLE_SEQUENCE:
			return data.doubleSeqValue();
		case LONG:
			return data.longValue();
		case LONG_SEQUENCE:
			return data.longSeqValue();
		case OBJECT:
			return data.anyValue();
		case OBJECT_SEQUENCE:
			return data.anySeqValue();
		case STRING:
			return data.stringValue();
		case STRING_SEQUENCE:
			return data.stringSeqValue();
		default:
			throw new IllegalArgumentException("Unsupported value type. Please complete switch statement.");
		}
	}
}
