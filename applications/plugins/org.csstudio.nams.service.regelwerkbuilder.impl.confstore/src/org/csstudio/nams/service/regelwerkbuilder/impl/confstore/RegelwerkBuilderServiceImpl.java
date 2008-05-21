package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationId;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationId.IdType;
import org.csstudio.ams.configurationStoreService.knownTObjects.AggrFilterConditionTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.AggrFilterTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.FilterConditionStringTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.FilterConditionTimeBasedTObject;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	public List<Regelwerk> gibAlleRegelwerke() {
		// hole alle Filter TObject aus dem confstore

		List<Regelwerk> results = new LinkedList<Regelwerk>();

		ConfigurationStoreService confStoreService = Activator.getDefault()
				.getConfigurationStoreService();

		List<AggrFilterTObject> listOfFilters = confStoreService
				.getListOfConfigurations(AggrFilterTObject.class);

		for (AggrFilterTObject filterTObject : listOfFilters) {

			List<AggrFilterConditionTObject> filterConditions = filterTObject
					.getFilterConditions();

			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();

			for (AggrFilterConditionTObject aggrFilterConditionTObject : filterConditions) {
				versandRegels.add(createVersandRegel(
						aggrFilterConditionTObject, confStoreService));
			}
			VersandRegel hauptRegel = new UndVersandRegel(versandRegels
					.toArray(new VersandRegel[0]));
			results.add(new StandardRegelwerk(hauptRegel));
		}

		return results;
	}

	private VersandRegel createVersandRegel(
			AggrFilterConditionTObject aggrFilterConditionTObject,
			ConfigurationStoreService confStoreService) {
		FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper
				.valueOf(aggrFilterConditionTObject.getFilterConditionTypeRef());
		switch (fctr) {
		case STRING: {
			FilterConditionStringTObject stringCondition = confStoreService
					.getConfiguration(ConfigurationId.valueOf(
							aggrFilterConditionTObject.getFilterConditionID(),
							IdType.STRING_FILTER_CONDITION),
							FilterConditionStringTObject.class);
			return new StringRegel(StringRegelOperator.valueOf(stringCondition
					.getOperator()), stringCondition.getKeyValue(),
					stringCondition.getCompValue());
		}
		case TIMEBASED: {
			FilterConditionTimeBasedTObject timeBasedCondition = confStoreService
					.getConfiguration(ConfigurationId.valueOf(
							aggrFilterConditionTObject.getFilterConditionID(),
							IdType.TIME_BASED_FILTER_CONDITION),
							FilterConditionTimeBasedTObject.class);
			VersandRegel startRegel = new StringRegel(StringRegelOperator
					.valueOf(timeBasedCondition.getStartOperator()),
					timeBasedCondition.getStartKeyValue(), timeBasedCondition
							.getStartCompValue());
			VersandRegel confirmRegel = new StringRegel(StringRegelOperator
					.valueOf(timeBasedCondition.getConfirmOperator()),
					timeBasedCondition.getConfirmKeyValue(), timeBasedCondition
							.getConfirmCompValue());

			// FIXME short erscheint mir als eine zu kleine Domäne, was
			// für eine Zeiteinheit ist das?
			short delayUntilAlarm = timeBasedCondition.getTimePeriod();

			VersandRegel timeBasedRegel = new TimeBasedRegel(startRegel, null,
					confirmRegel, Millisekunden.valueOf(delayUntilAlarm));
			return timeBasedRegel;
		}
			// case ODER: {
			// //in der DB sollten nur oder mit 2 argumenten vorkommen
			// }
			// case STRING_ARRAY: {
			//		
			// }
			// case PV: {
			//		
			// }
			// case UND: {
			// CommonConjunctionFilterConditionTObject undCondition =
			// confStoreService.getConfiguration(ConfigurationId.valueOf(aggrFilterConditionTObject.getFilterConditionID(),
			// IdType.COMMON_CONJUNCTION_FILTER_CONDITION),
			// CommonConjunctionFilterConditionTObject.class);
			// // undCondition.getFirstFilterConditionReference()
			// }
		default:
			throw new IllegalArgumentException("Unsupported FilterType, see "
					+ this.getClass().getPackage() + "."
					+ this.getClass().getName());
		}
	}
}
