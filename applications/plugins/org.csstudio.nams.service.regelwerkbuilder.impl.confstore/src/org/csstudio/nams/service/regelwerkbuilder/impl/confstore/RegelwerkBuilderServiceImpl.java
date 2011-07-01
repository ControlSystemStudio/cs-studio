
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVarFiltCondDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedType;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerksBuilderException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	private static IProcessVariableConnectionService pvConnectionService;
	private static LocalStoreConfigurationService configurationStoreService;
	private static Logger logger;

	public static void staticInject(
			final IProcessVariableConnectionService pvConnectionService) {
		RegelwerkBuilderServiceImpl.pvConnectionService = pvConnectionService;
	}

	public static void staticInject(
			final LocalStoreConfigurationService configurationStoreService) {
		RegelwerkBuilderServiceImpl.configurationStoreService = configurationStoreService;
	}

	public static void staticInject(final Logger logger) {
		RegelwerkBuilderServiceImpl.logger = logger;
	}

	@Override
    public List<Regelwerk> gibAlleRegelwerke()
			throws RegelwerksBuilderException {
		final List<Regelwerk> results = new LinkedList<Regelwerk>();
		try {

			final LocalStoreConfigurationService confStoreService = RegelwerkBuilderServiceImpl.configurationStoreService;
			// get all filters
			Collection<FilterDTO> listOfFilters = null;
			listOfFilters = confStoreService.getEntireFilterConfiguration()
					.gibAlleFilter();
			// TODO Auto-generated catch blocks

			// we do assume, that the first level filtercondition are conjugated
			for (final FilterDTO filterDTO : listOfFilters) {

				final List<FilterConditionDTO> filterConditions = filterDTO
						.getFilterConditions();

				// create a list of first level filterconditions
				final List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();
				for (final FilterConditionDTO filterConditionDTO : filterConditions) {
					try {
						versandRegels.add(this
								.createVersandRegel(filterConditionDTO));
					} catch (Throwable t) {
						RegelwerkBuilderServiceImpl.logger.logErrorMessage(
								this,
								"Failed to create Versand-Regel from DTO: "
										+ filterConditionDTO + " for Filter: "
										+ filterDTO, t);
					}
				}
				final VersandRegel hauptRegel = new UndVersandRegel(
						versandRegels.toArray(new VersandRegel[0]));
				results.add(new StandardRegelwerk(Regelwerkskennung.valueOf(
						filterDTO.getIFilterID(), filterDTO.getName()),
						hauptRegel));
			}

		} catch (final Throwable t) {
			RegelwerkBuilderServiceImpl.logger.logErrorMessage(this,
					"failed to load Regelwerke!", t);
			throw new RegelwerksBuilderException("failed to load Regelwerke!",
					t);
		}
		return results;
	}

	protected VersandRegel createVersandRegel(
			final FilterConditionDTO filterConditionDTO) {
		// mapping the type information in the aggrFilterConditionTObject to a
		// VersandRegel

		// FIXME (gs) hier knallt es bei JCFF oder NCFF Bedingungen

		final FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper
				.valueOf(filterConditionDTO.getClass());
		switch (fctr) {
		//
		case STRING: {
			final StringFilterConditionDTO stringCondition = (StringFilterConditionDTO) filterConditionDTO;
			return new StringRegel(stringCondition.getOperatorEnum(),
					stringCondition.getKeyValueEnum(), stringCondition
							.getCompValue());
		}
		case TIMEBASED: {
			final TimeBasedFilterConditionDTO timeBasedCondition = (TimeBasedFilterConditionDTO) filterConditionDTO;
			final VersandRegel startRegel = new StringRegel(timeBasedCondition
					.getTBStartOperator(), timeBasedCondition
					.getStartKeyValue(), timeBasedCondition
					.getCStartCompValue());
			final VersandRegel confirmCancelRegel = new StringRegel(
					timeBasedCondition.getTBConfirmOperator(),
					timeBasedCondition.getConfirmKeyValue(), timeBasedCondition
							.getCConfirmCompValue());

			final Millisekunden delayUntilAlarm = timeBasedCondition
					.getTimePeriod();
			final TimeBasedType timeBehaviorAlarm = timeBasedCondition
					.getTimeBehavior();

			VersandRegel timeBasedRegel = null;
			if (timeBehaviorAlarm == TimeBasedType.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM) {
				timeBasedRegel = new TimeBasedAlarmBeiBestaetigungRegel(
						startRegel, confirmCancelRegel, delayUntilAlarm);
			} else if (timeBehaviorAlarm == TimeBasedType.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM) {
				timeBasedRegel = new TimeBasedRegel(startRegel,
						confirmCancelRegel, null, delayUntilAlarm);
			} else {
				throw new IllegalArgumentException("Unsupported Timebehavior");
			}
			return timeBasedRegel;
		}
		case JUNCTOR: {
			final VersandRegel[] versandRegels = new VersandRegel[2];

			final JunctorConditionDTO junctorCondition = (JunctorConditionDTO) filterConditionDTO;
			final FilterConditionDTO firstFilterCondition = junctorCondition
					.getFirstFilterCondition();
			final FilterConditionDTO secondFilterCondition = junctorCondition
					.getSecondFilterCondition();

			versandRegels[0] = this.createVersandRegel(firstFilterCondition);
			versandRegels[1] = this.createVersandRegel(secondFilterCondition);
			switch (junctorCondition.getJunctor()) {
			case OR:
				return new OderVersandRegel(versandRegels);
			case AND:
				return new UndVersandRegel(versandRegels);
			default:
				throw new IllegalArgumentException("Unsupported Junctor.");
			}
		}
			// oder verknüpfte Stringregeln
		case STRING_ARRAY: {
			final List<VersandRegel> versandRegels = new LinkedList<VersandRegel>();

			final StringArFilterConditionDTO stringArayCondition = (StringArFilterConditionDTO) filterConditionDTO;

			final List<String> compareValueList = stringArayCondition
					.getCompareValueStringList();

			final MessageKeyEnum keyValue = stringArayCondition
					.getKeyValueEnum();
			final StringRegelOperator operatorEnum = stringArayCondition
					.getOperatorEnum();
			for (final String string : compareValueList) {
				versandRegels.add(new StringRegel(operatorEnum, keyValue,
						string));
			}
			return new OderVersandRegel(versandRegels
					.toArray(new VersandRegel[versandRegels.size()]));
		}
		case PV: {
			final ProcessVarFiltCondDTO pvCondition = (ProcessVarFiltCondDTO) filterConditionDTO;
			return new ProcessVariableRegel(
					RegelwerkBuilderServiceImpl.pvConnectionService,
					pvCondition.getPVAddress(), pvCondition.getPVOperator(),
					pvCondition.getSuggestedPVType(), pvCondition
							.getCCompValue());
		}
		case NEGATION: {
			final NegationCondForFilterTreeDTO notCondition = (NegationCondForFilterTreeDTO) filterConditionDTO;
			return new NichtVersandRegel(this.createVersandRegel(notCondition
					.getNegatedFilterCondition()));
		}
		case JUNCTOR_FOR_TREE: {
			final JunctorCondForFilterTreeDTO junctorCondition = (JunctorCondForFilterTreeDTO) filterConditionDTO;

			final Set<FilterConditionDTO> operands = junctorCondition
					.getOperands();
			final VersandRegel[] versandRegels = new VersandRegel[operands
					.size()];
			final FilterConditionDTO[] conditions = operands
					.toArray(new FilterConditionDTO[operands.size()]);

			for (int i = 0; i < versandRegels.length; i++) {
				versandRegels[i] = this.createVersandRegel(conditions[i]);
			}
			if (junctorCondition.getOperator() == JunctorConditionType.AND) {
				return new UndVersandRegel(versandRegels);
			} else if (junctorCondition.getOperator() == JunctorConditionType.OR) {
				return new OderVersandRegel(versandRegels);
			} else {
				throw new IllegalArgumentException(
						"Unsupported FilterType, see "
								+ this.getClass().getPackage() + "."
								+ this.getClass().getName());
			}
		}
		default:
			throw new IllegalArgumentException("Unsupported FilterType, see "
					+ this.getClass().getPackage() + "."
					+ this.getClass().getName());
		}
	}
}
