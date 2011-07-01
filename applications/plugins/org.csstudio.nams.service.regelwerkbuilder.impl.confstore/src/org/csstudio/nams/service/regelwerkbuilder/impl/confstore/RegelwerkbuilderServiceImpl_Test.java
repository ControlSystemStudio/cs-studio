
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.NichtVersandRegel;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTOPK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.junit.Assert;
import org.junit.Test;

public class RegelwerkbuilderServiceImpl_Test extends TestCase {

	private RegelwerkBuilderServiceImpl regelwerkBuilderService;
	private StringRegel childRegel;
	private StringFilterConditionDTO childDTO;
	private StringFilterConditionDTO childDTO2;
	private StringRegel childRegel2;

	@Override
	protected void setUp() throws Exception {
		regelwerkBuilderService = (RegelwerkBuilderServiceImpl) new RegelwerkBuilderServiceFactoryImpl()
				.createService();
		RegelwerkBuilderServiceImpl
				.staticInject(new IProcessVariableConnectionService() {

					@Override
                    public List<IConnector> getConnectors() {
						return null;
					}

					@Override
                    public SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv) {
						return null;
					}

					@Override
					public void readValueAsynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType,
							IProcessVariableValueListener listener) {
						
					}

					@Override
					public <E> E readValueSynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType)
							throws ConnectionException {
						return null;
					}

					@Override
					public void register(IProcessVariableValueListener listener, IProcessVariableAddress pv, ValueType valueType) {
						
					}

					@Override
					public void unregister(IProcessVariableValueListener listener) {
						
					}

					@Override
					public void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value,
							ValueType expectedValueType, IProcessVariableWriteListener listener) {
						
					}

					@Override
					public boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType) {
						Assert.fail("unexpected call of method.");
						return false;
					}

					@Override
					public int getNumberOfActiveConnectors() {
						// TODO Auto-generated method stub
						return 0;
					}

				
				});
		RegelwerkBuilderServiceImpl
				.staticInject(new LocalStoreConfigurationService() {

					@Override
                    public void deleteDTO(NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {

					}

					@Override
                    public ReplicationStateDTO getCurrentReplicationState()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					@Override
                    public Configuration getEntireConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					@Override
                    public FilterConfiguration getEntireFilterConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						return null;
					}

					@Override
                    public void prepareSynchonization() throws StorageError,
							StorageException,
							InconsistentConfigurationException {
					}

					@Override
                    public void saveCurrentReplicationState(
							ReplicationStateDTO currentState)
							throws StorageError, StorageException,
							UnknownConfigurationElementError {
					}

					@Override
                    public void saveDTO(NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
					}

					@Override
                    public void saveHistoryDTO(HistoryDTO historyDTO)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
					}
				});
		childRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf");
		childRegel2 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf2");

		childDTO = new StringFilterConditionDTO();
		childDTO.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		childDTO.setKeyValue(MessageKeyEnum.HOST);
		childDTO.setCompValue("gnarf");

		childDTO2 = new StringFilterConditionDTO();
		childDTO2.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		childDTO2.setKeyValue(MessageKeyEnum.HOST);
		childDTO2.setCompValue("gnarf2");
	}

	@Test
	public void testBuildStringCondition() {
		assertEquals(childRegel, regelwerkBuilderService
				.createVersandRegel(childDTO));
	}

	@Test
	public void testBuildStringArrayCondition() {
		StringArFilterConditionDTO arrayDTO = new StringArFilterConditionDTO();
		arrayDTO.setKeyValue(MessageKeyEnum.HOST);
		arrayDTO.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		ArrayList<StrgArFiltCondCompValDTO> arrayList = new ArrayList<StrgArFiltCondCompValDTO>();

		StrgArFiltCondCompValDTO compareValuesDTO = new StrgArFiltCondCompValDTO();
		StrgArFiltCondCompValDTOPK valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
		valuesDTO_PK.setCompValue("gnarf");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);

		compareValuesDTO = new StrgArFiltCondCompValDTO();
		valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
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

	@Test
	public void testBuildJunctorConditionOr() {
		JunctorConditionDTO junctorDTO = new JunctorConditionDTO();
		junctorDTO.setJunctor(JunctorConditionType.OR);
		junctorDTO.setFirstFilterCondition(childDTO);

		StringFilterConditionDTO childDTO2 = new StringFilterConditionDTO();
		childDTO2.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		childDTO2.setKeyValue(MessageKeyEnum.HOST);
		childDTO2.setCompValue("gnarf2");
		junctorDTO.setSecondFilterCondition(childDTO2);

		VersandRegel[] regeln = new VersandRegel[2];
		regeln[0] = childRegel;
		regeln[1] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf2");
		VersandRegel zielRegel = new OderVersandRegel(regeln);

		assertEquals(zielRegel, regelwerkBuilderService
				.createVersandRegel(junctorDTO));
	}

	@Test
	public void testBuildJunctorConditionAnd() {
		JunctorConditionDTO junctorDTO = new JunctorConditionDTO();
		junctorDTO.setJunctor(JunctorConditionType.AND);
		junctorDTO.setFirstFilterCondition(childDTO);
		junctorDTO.setSecondFilterCondition(childDTO2);

		VersandRegel[] regeln = new VersandRegel[2];
		regeln[0] = childRegel;
		regeln[1] = childRegel2;
		VersandRegel zielRegel = new UndVersandRegel(regeln);

		assertEquals(zielRegel, regelwerkBuilderService
				.createVersandRegel(junctorDTO));
	}

	@Test
	public void testBuildJunctorCondtionTreeNegation() {
		NegationCondForFilterTreeDTO negationDTO = new NegationCondForFilterTreeDTO();
		negationDTO.setNegatedFilterCondition(childDTO);
		VersandRegel zielRegel = new NichtVersandRegel(childRegel);
		assertEquals(zielRegel, regelwerkBuilderService
				.createVersandRegel(negationDTO));
	}

	@Test
	public void testBuildJunctorConditionTreeAnd() {
		JunctorCondForFilterTreeDTO junctorDTO = new JunctorCondForFilterTreeDTO();
		junctorDTO.setOperator(JunctorConditionType.AND);
		Set<FilterConditionDTO> childConditions = new HashSet<FilterConditionDTO>();
		childConditions.add(childDTO);
		childConditions.add(childDTO2);
		junctorDTO.setOperands(childConditions);
		
		VersandRegel[] regeln = new VersandRegel[2];
		regeln[0] = childRegel;
		regeln[1] = childRegel2;
		VersandRegel zielRegel = new UndVersandRegel(regeln);
		
		assertEquals(zielRegel, regelwerkBuilderService.createVersandRegel(junctorDTO));
	}

	@Test
	public void testBuildJunctorConditionTreeOr() {
		JunctorCondForFilterTreeDTO junctorDTO = new JunctorCondForFilterTreeDTO();
		junctorDTO.setOperator(JunctorConditionType.OR);
		Set<FilterConditionDTO> childConditions = new HashSet<FilterConditionDTO>();
		childConditions.add(childDTO);
		childConditions.add(childDTO2);
		junctorDTO.setOperands(childConditions);
		
		VersandRegel[] regeln = new VersandRegel[2];
		regeln[0] = childRegel;
		regeln[1] = childRegel2;
		VersandRegel zielRegel = new OderVersandRegel(regeln);
		
		assertEquals(zielRegel, regelwerkBuilderService.createVersandRegel(junctorDTO));
	}
	
}
