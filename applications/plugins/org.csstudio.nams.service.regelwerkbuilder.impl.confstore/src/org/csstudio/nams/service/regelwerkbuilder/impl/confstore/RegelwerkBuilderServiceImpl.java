package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.NichtVersandRegel;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.TimeBasedAlarmBeiBestaetigungRegel;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	private static IProcessVariableConnectionService pvConnectionService;
	private static LocalStoreConfigurationService configurationStoreService;

	public List<Regelwerk> gibAlleRegelwerke() {
		// hole alle Filter TObject aus dem confstore

		List<Regelwerk> results = new LinkedList<Regelwerk>();

		LocalStoreConfigurationService confStoreService = configurationStoreService;
		// get all filters
		Collection<FilterDTO> listOfFilters = null;
		try {
			listOfFilters = confStoreService.getEntireConfiguration()
					.gibAlleFilter();
			// TODO Auto-generated catch blocks
		} catch (StorageError e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (InconsistentConfiguration e) {
			e.printStackTrace();
		}

		// we do assume, that the first level filtercondition are conjugated
		for (FilterDTO filterDTO : listOfFilters) {

			List<FilterConditionDTO> filterConditions = 
				filterDTO.getFilterConditions();

			// create a list of first level filterconditions
			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();
			for (FilterConditionDTO filterConditionDTO : filterConditions) {
				versandRegels.add(createVersandRegel(filterConditionDTO,
						confStoreService));
			}
			VersandRegel hauptRegel = new UndVersandRegel(versandRegels
					.toArray(new VersandRegel[0]));
			results.add(new StandardRegelwerk(Regelwerkskennung.valueOf(filterDTO.getIFilterID(), filterDTO.getName()), hauptRegel));
		}

		return results;
	}

	private VersandRegel createVersandRegel(
			FilterConditionDTO filterConditionDTO,
			LocalStoreConfigurationService confStoreService) {
		// mapping the type information in the aggrFilterConditionTObject to a
		// VersandRegel
		FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper
				.valueOf(filterConditionDTO.getClass());
		switch (fctr) {
		//
		case STRING: {
			StringFilterConditionDTO stringCondition = (StringFilterConditionDTO) filterConditionDTO;
			return new StringRegel(StringRegelOperator.valueOf(stringCondition
					.getOperator()), stringCondition.getKeyValueEnum(),
					stringCondition.getCompValue());
		}
		case TIMEBASED: {
			TimeBasedFilterConditionDTO timeBasedCondition = (TimeBasedFilterConditionDTO) filterConditionDTO;
			VersandRegel startRegel = new StringRegel(timeBasedCondition
					.getTBStartOperator(),
					timeBasedCondition.getStartKeyValue(), timeBasedCondition
							.getCStartCompValue());
			VersandRegel confirmCancelRegel = new StringRegel(
					timeBasedCondition.getTBConfirmOperator(),
					timeBasedCondition.getConfirmKeyValue(), timeBasedCondition
							.getCConfirmCompValue());

			Millisekunden delayUntilAlarm = timeBasedCondition.getTimePeriod();
			TimeBasedType timeBehaviorAlarm = timeBasedCondition.getTimeBehavior();

			VersandRegel timeBasedRegel = null;
			if (timeBehaviorAlarm == TimeBasedType.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM)
				timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(
						startRegel, confirmCancelRegel, delayUntilAlarm);
			else if (timeBehaviorAlarm == TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM)
				timeBasedRegel = new TimeBasedRegel(startRegel,
						confirmCancelRegel, null, delayUntilAlarm);
			else
				throw new IllegalArgumentException("Unsupported Timebehavior");
			return timeBasedRegel;
		}
		case JUNCTOR: {
			VersandRegel[] versandRegels = new VersandRegel[2];

			JunctorConditionDTO junctorCondition = (JunctorConditionDTO) filterConditionDTO;
			FilterConditionDTO firstFilterCondition = junctorCondition
					.getFirstFilterCondition();
			FilterConditionDTO secondFilterCondition = junctorCondition
					.getSecondFilterCondition();

			versandRegels[0] = createVersandRegel(firstFilterCondition,
					confStoreService);
			versandRegels[1] = createVersandRegel(secondFilterCondition,
					confStoreService);
			switch (junctorCondition.getJunctor()) {
			case OR:
				return new OderVersandRegel(versandRegels);
			case AND:
				return new UndVersandRegel(versandRegels);
			case NOT:
				return new NichtVersandRegel(versandRegels[0]);
			default:
				throw new IllegalArgumentException("Unsupported Junctor.");
			}
		}
			// oder verknüpfte Stringregeln
		case STRING_ARRAY: {
			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();

			StringArrayFilterConditionDTO stringArayCondition = (StringArrayFilterConditionDTO) filterConditionDTO;
			
			List<String> compareValueList = stringArayCondition.getCompareValueList();
			
			MessageKeyEnum keyValue = stringArayCondition.getKeyValueEnum();
			StringRegelOperator operatorEnum = stringArayCondition.getOperatorEnum();
			for (String string : compareValueList) {
				versandRegels.add(new StringRegel(operatorEnum, keyValue, string));
			}
			return new OderVersandRegel(versandRegels
					.toArray(new VersandRegel[versandRegels.size()]));
		}
		case PV: {
			ProcessVariableFilterConditionDTO pvCondition = (ProcessVariableFilterConditionDTO) filterConditionDTO;
			return new ProcessVariableRegel(
					pvConnectionService,
					pvCondition.getPVAddress(),
					pvCondition.getPVOperator(), pvCondition.getSuggestedPVType(),
					pvCondition.getCCompValue());
		}
		default:
			throw new IllegalArgumentException("Unsupported FilterType, see "
					+ this.getClass().getPackage() + "."
					+ this.getClass().getName());
		}
	}

	public static void staticInject(
			IProcessVariableConnectionService pvConnectionService) {
		RegelwerkBuilderServiceImpl.pvConnectionService = pvConnectionService;
	}

	public static void staticInject(
			LocalStoreConfigurationService configurationStoreService) {
		 RegelwerkBuilderServiceImpl.configurationStoreService =
		 configurationStoreService;
	}
}
