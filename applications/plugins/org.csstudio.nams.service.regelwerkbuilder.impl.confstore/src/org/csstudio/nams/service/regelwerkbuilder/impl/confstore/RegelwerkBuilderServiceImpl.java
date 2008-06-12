package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.Collections;
import java.util.List;

import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	public List<Regelwerk> gibAlleRegelwerke() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

//	// bestätigungsalarm bei timeout kein alarm?
//	private static final short TIMEBEHAVIOR_CONFIRMED_THEN_ALARM = 0;
//	// aufhebungsalarm und bei timeout alarm?
//	private static final short TIMEBEHAVIOR_TIMEOUT_THEN_ALARM = 1;
//	private static IProcessVariableConnectionService pvConnectionService;
//	private static LocalStoreConfigurationService configurationStoreService;
//
//	public List<Regelwerk> gibAlleRegelwerke() {
//		// hole alle Filter TObject aus dem confstore
//
//		List<Regelwerk> results = new LinkedList<Regelwerk>();
//
//		LocalStoreConfigurationService confStoreService = configurationStoreService;
//		// get all filters
//		List<AggrFilterTObject> listOfFilters = confStoreService
//				.getEntireConfiguration().;
//
//		// we do assume, that the first level filtercondition are conjugated
//		for (AggrFilterTObject filterTObject : listOfFilters) {
//
//			List<AggrFilterConditionTObject> filterConditions = filterTObject
//					.getFilterConditions();
//
//			// create a list of first level filterconditions
//			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();
//			for (AggrFilterConditionTObject aggrFilterConditionTObject : filterConditions) {
//				versandRegels.add(createVersandRegel(
//						aggrFilterConditionTObject, confStoreService));
//			}
//			VersandRegel hauptRegel = new UndVersandRegel(versandRegels
//					.toArray(new VersandRegel[0]));
//			results.add(new StandardRegelwerk(hauptRegel));
//		}
//
//		return results;
//	}
//
//	private VersandRegel createVersandRegel(
//			FilterConditionDTO filterConditionDTO,
//			LocalStoreConfigurationService confStoreService) {
//		// mapping the type information in the aggrFilterConditionTObject to a
//		// VersandRegel
//		FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper
//				.valueOf(filterConditionDTO.getClass());
//		switch (fctr) {
//		//
//		case STRING: {
//			FilterConditionStringTObject stringCondition = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							filterConditionDTO.getFilterConditionID(),
//							IdType.STRING_FILTER_CONDITION),
//							FilterConditionStringTObject.class);
//			return new StringRegel(StringRegelOperator.valueOf(stringCondition
//					.getOperator()), MessageKeyEnum.valueOf(stringCondition
//					.getKeyValue()), stringCondition.getCompValue());
//			// FIXME DTO Use real MessageKeyEnum for MessageKey instead of
//			// .valueOf(stringCondition.getKeyValue()).
//			// return new
//			// StringRegel(StringRegelOperator.valueOf(stringCondition
//			// .getOperator()), stringCondition.getKeyValue(),
//			// stringCondition.getCompValue());
//		}
//		case TIMEBASED: {
//			FilterConditionTimeBasedTObject timeBasedCondition = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							filterConditionDTO.getFilterConditionID(),
//							IdType.TIME_BASED_FILTER_CONDITION),
//							FilterConditionTimeBasedTObject.class);
//			VersandRegel startRegel = new StringRegel(StringRegelOperator
//					.valueOf(timeBasedCondition.getStartOperator()),
//			// FIXME DTO Use real MessageKeyEnum for MessageKey instead of
//					// .valueOf(timeBasedCondition.getStartKeyValue()).
//					MessageKeyEnum.valueOf(timeBasedCondition
//							.getStartKeyValue()), timeBasedCondition
//							.getStartCompValue());
//			VersandRegel confirmCancelRegel = new StringRegel(
//					StringRegelOperator.valueOf(timeBasedCondition
//							.getConfirmOperator()), MessageKeyEnum
//							.valueOf(timeBasedCondition
//							// FIXME DTO Use real MessageKeyEnum for MessageKey
//									// instead of
//									// .valueOf(timeBasedCondition..getConfirmKeyValue()).
//									.getConfirmKeyValue()), timeBasedCondition
//							.getConfirmCompValue());
//
//			Millisekunden delayUntilAlarm = Millisekunden
//					.valueOf(timeBasedCondition.getTimePeriod() * 1000);
//			short timeBehaviorAlarm = timeBasedCondition.getTimeBehavior();
//
//			VersandRegel timeBasedRegel = null;
//			if (timeBehaviorAlarm == TIMEBEHAVIOR_CONFIRMED_THEN_ALARM)
//				timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(
//						startRegel, confirmCancelRegel, delayUntilAlarm);
//			else if (timeBehaviorAlarm == TIMEBEHAVIOR_TIMEOUT_THEN_ALARM)
//				timeBasedRegel = new TimeBasedRegel(startRegel,
//						confirmCancelRegel, null, delayUntilAlarm);
//			else
//				throw new IllegalArgumentException("Unsupported Timebehavior");
//			return timeBasedRegel;
//		}
//		case JUNCTOR: {
//			// FIXME merkwüdigeBenamung CommonConjunctionFilterCondition
//			// realsiert derzeit nur die Disjunktion
//			VersandRegel[] versandRegels = new VersandRegel[2];
//
//			CommonConjunctionFilterConditionTObject junctorCondition = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							filterConditionDTO.getFilterConditionID(),
//							IdType.COMMON_CONJUNCTION_FILTER_CONDITION),
//							CommonConjunctionFilterConditionTObject.class);
//			int firstFilterConditionReference = junctorCondition
//					.getFirstFilterConditionReference();
//
//			AggrFilterConditionTObject firstFilterConditionTObject = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							firstFilterConditionReference,
//							IdType.FILTER_CONDITIONS),
//							AggrFilterConditionTObject.class);
//
//			int secondFilterConditionReference = junctorCondition
//					.getSecondFilterConditionReference();
//
//			versandRegels[0] = createVersandRegel(firstFilterConditionTObject,
//					confStoreService);
//
//			AggrFilterConditionTObject secondFilterConditionTObject = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							secondFilterConditionReference,
//							IdType.FILTER_CONDITIONS),
//							AggrFilterConditionTObject.class);
//
//			versandRegels[1] = createVersandRegel(secondFilterConditionTObject,
//					confStoreService);
////TODO reimplement with adapted getOperand method
////			switch (junctorCondition.getOperand()) {
////			case OR:
////				return new OderVersandRegel(versandRegels);
////			case AND:
////				return new UndVersandRegel(versandRegels);
////			case NOT:
////				return new NichtVersandRegel(versandRegels[0]);
////			default:
////				throw new IllegalArgumentException("Unsupported Junctor.");
////			}
//		}
//			// oder verknüpfte Stringregeln
//		case STRING_ARRAY: {
//			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();
//
//			FilterConditionArrayStringTObject arrayStringCondition = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							filterConditionDTO.getFilterConditionID(),
//							IdType.AGGR_FILTER_CONDITION_ARRAY_STRING),
//							FilterConditionArrayStringTObject.class);
//
//			return new OderVersandRegel(versandRegels
//					.toArray(new VersandRegel[0]));
//		}
//		case PV: {
//			FilterConditionProcessVariableTObject pvCondition = confStoreService
//					.getConfiguration(ConfigurationId.valueOf(
//							filterConditionDTO.getFilterConditionID(),
//							IdType.PROCESS_VARIABLE_FILTER_CONDITION),
//							FilterConditionProcessVariableTObject.class);
//			return new ProcessVariableRegel(
//					pvConnectionService,
//					ProcessVariableAdressFactory
//							.getInstance()
//							.createProcessVariableAdress(
//									pvCondition.getProcessVariableChannelName()),
//					pvCondition.getOperator(), pvCondition.getSuggestedType(),
//					pvCondition.getCompValue());
//		}
//		default:
//			throw new IllegalArgumentException("Unsupported FilterType, see "
//					+ this.getClass().getPackage() + "."
//					+ this.getClass().getName());
//		}
//	}
//
	public static void staticInject(
			IProcessVariableConnectionService pvConnectionService) {
//		RegelwerkBuilderServiceImpl.pvConnectionService = pvConnectionService;
	}

//	public static void staticInject(
//			ConfigurationStoreService configurationStoreService) {
////		RegelwerkBuilderServiceImpl.configurationStoreService = configurationStoreService;
//	}
}
