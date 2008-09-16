package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO_PK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IConnectorStatistic;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;
import org.junit.Test;

public class RegelwerkbuilderServiceImpl_Test extends TestCase {

	private RegelwerkBuilderServiceImpl regelwerkBuilderService;

	@Override
	protected void setUp() throws Exception {
		regelwerkBuilderService = (RegelwerkBuilderServiceImpl) new RegelwerkBuilderServiceFactoryImpl()
				.createService();
		RegelwerkBuilderServiceImpl
				.staticInject(new IProcessVariableConnectionService() {

					public int getConnectorCount() {
						return 0;
					}

					public List<IConnectorStatistic> getConnectorStatistic() {
						return null;
					}

					public Object getValue(
							IProcessVariableAddress processVariableAddress,
							ValueType valueType) throws ConnectionException {
						return null;
					}

					public double getValueAsDouble(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return 0;
					}

					public double[] getValueAsDoubleSequence(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public Enum getValueAsEnum(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public long getValueAsLong(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return 0;
					}

					public long[] getValueAsLongSequence(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public Object getValueAsObject(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public Object[] getValueAsObjectSequence(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public String getValueAsString(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public String[] getValueAsStringSequence(
							IProcessVariableAddress processVariableAddress)
							throws ConnectionException {
						return null;
					}

					public void getValueAsync(
							IProcessVariableAddress processVariableAddress,
							ValueType valueType,
							IProcessVariableValueListener<Double> listener) {
					}

					public void getValueAsyncAsDouble(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<Double> listener) {
					}

					public void getValueAsyncAsDoubleSequence(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<double[]> listener) {
					}

					public void getValueAsyncAsEnum(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<Enum> listener) {
					}

					public void getValueAsyncAsLong(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<Long> listener) {
					}

					public void getValueAsyncAsLongSequence(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<long[]> listener) {
					}

					public void getValueAsyncAsObject(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<Object> listener) {
					}

					public void getValueAsyncAsObjectSequence(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<Object[]> listener) {
					}

					public void getValueAsyncAsString(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<String> listener) {
					}

					public void getValueAsyncAsStringSequence(
							IProcessVariableAddress processVariableAddress,
							IProcessVariableValueListener<String[]> listener) {
					}

					public SettableState isSettable(IProcessVariableAddress pv) {
						return null;
					}

					public void register(
							IProcessVariableValueListener listener,
							IProcessVariableAddress pv, ValueType valueType) {
					}

					public void registerForDoubleSequenceValues(
							IProcessVariableValueListener<double[]> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForDoubleValues(
							IProcessVariableValueListener<Double> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForEnumValues(
							IProcessVariableValueListener<Enum> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForLongSequenceValues(
							IProcessVariableValueListener<long[]> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForLongValues(
							IProcessVariableValueListener<Long> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForObjectSequenceValues(
							IProcessVariableValueListener<Object[]> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForObjectValues(
							IProcessVariableValueListener<Object> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForStringSequenceValues(
							IProcessVariableValueListener<String[]> listener,
							IProcessVariableAddress pv) {
					}

					public void registerForStringValues(
							IProcessVariableValueListener<String> listener,
							IProcessVariableAddress pv) {
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							long value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							long[] value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							double value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							double[] value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							String value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							String[] value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							Object value, ValueType expectedValueType) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							Object[] value) {
						return false;
					}

					public boolean setValue(
							IProcessVariableAddress processVariableAddress,
							Enum value) {
						return false;
					}

					public void unregister(
							IProcessVariableValueListener listener) {

					}
				});
		RegelwerkBuilderServiceImpl
				.staticInject(new LocalStoreConfigurationService() {

					public void deleteDTO(NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {

					}

					public ReplicationStateDTO getCurrentReplicationState()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					public Configuration getEntireConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					public FilterConfiguration getEntireFilterConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					public void prepareSynchonization() throws StorageError,
							StorageException,
							InconsistentConfigurationException {
					}

					public void saveCurrentReplicationState(
							ReplicationStateDTO currentState)
							throws StorageError, StorageException,
							UnknownConfigurationElementError {
					}

					public void saveDTO(NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
					}

					public void saveHistoryDTO(HistoryDTO historyDTO)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
					}
				});
	}

	@Test
	public void testBuildStringCondition() {
		StringFilterConditionDTO stringDTO = new StringFilterConditionDTO();
		stringDTO.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		stringDTO.setKeyValue(MessageKeyEnum.HOST);
		stringDTO.setCompValue("gnarf");
		StringRegel stringRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.HOST,
				"gnarf");
		assertEquals(stringRegel, regelwerkBuilderService
				.createVersandRegel(stringDTO));
	}

	@Test
	public void testBuildStringArrayCondition() {
		StringArrayFilterConditionDTO arrayDTO = new StringArrayFilterConditionDTO();
		arrayDTO.setKeyValue(MessageKeyEnum.HOST);
		arrayDTO.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		
		ArrayList<StringArrayFilterConditionCompareValuesDTO> arrayList = new ArrayList<StringArrayFilterConditionCompareValuesDTO>();

		StringArrayFilterConditionCompareValuesDTO compareValuesDTO = new StringArrayFilterConditionCompareValuesDTO();
		StringArrayFilterConditionCompareValuesDTO_PK valuesDTO_PK = new StringArrayFilterConditionCompareValuesDTO_PK();
		valuesDTO_PK.setCompValue("gnarf");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);
		
		compareValuesDTO = new StringArrayFilterConditionCompareValuesDTO();
		valuesDTO_PK = new StringArrayFilterConditionCompareValuesDTO_PK();
		valuesDTO_PK.setCompValue("gnarf2");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);
		
		arrayDTO.setCompareValues(arrayList);
		
		
		VersandRegel[] regeln = new VersandRegel[2];
		regeln[0] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf");
		regeln[1] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf2");
		VersandRegel zielRegel = new OderVersandRegel(regeln);
		
		assertEquals(zielRegel, regelwerkBuilderService
				.createVersandRegel(arrayDTO));
	}

}
