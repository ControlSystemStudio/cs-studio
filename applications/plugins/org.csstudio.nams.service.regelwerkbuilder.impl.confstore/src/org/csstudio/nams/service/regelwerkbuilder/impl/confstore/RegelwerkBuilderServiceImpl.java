package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationId;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationId.IdType;
import org.csstudio.ams.configurationStoreService.knownTObjects.AggrFilterConditionTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.AggrFilterTObject;
import org.csstudio.ams.configurationStoreService.knownTObjects.FilterConditionStringTObject;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	@Override
	public List<Regelwerk> gibAlleRegelwerke() {
		// hole alle Filter TObject aus dem confstore

		ConfigurationStoreService confStoreService = Activator.getDefault()
				.getConfigurationStoreService();

		List<AggrFilterTObject> listOfFilters = confStoreService
				.getListOfConfigurations(AggrFilterTObject.class);

		for (AggrFilterTObject filterTObject : listOfFilters) {

			List<AggrFilterConditionTObject> filterConditions = filterTObject
					.getFilterConditions();

			List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();

			for (AggrFilterConditionTObject aggrFilterConditionTObject : filterConditions) {
				FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper
						.valueOf(aggrFilterConditionTObject
								.getFilterConditionTypeRef());
				switch (fctr) {
				case STRING: {
					FilterConditionStringTObject stringCondition = confStoreService
							.getConfiguration(ConfigurationId.valueOf(
									aggrFilterConditionTObject
											.getFilterConditionID(),
									IdType.STRING_FILTER_CONDITION),
									FilterConditionStringTObject.class);
					 versandRegels.add(new
					 StringRegel(StringRegelOperator.valueOf(stringCondition.getOperator()), stringCondition.getKeyValue(), stringCondition.getCompValue()));
				} //TODO add the other FilterConditions here
				
				}
			}

			// VersandRegel hauptRegel = new
			// UndVersandRegel(filterTObject.getFilterConditions());
			//			
			// // new StandardRegelwerk();
		}

		return null;
	}
}
