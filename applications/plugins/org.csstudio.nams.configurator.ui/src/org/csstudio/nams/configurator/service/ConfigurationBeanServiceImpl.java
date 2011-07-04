
package org.csstudio.nams.configurator.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmTopicFilterAction;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterFilterAction;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeitergruppenFilterAction;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterAction;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.IReceiverBean;
import org.csstudio.nams.configurator.beans.MessageTemplateBean;
import org.csstudio.nams.configurator.beans.User2GroupBean;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AbstAlarmbFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AbstAlarmbGruppenFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmTopicFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenEmailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenSMSBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenVMailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenVMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbVoiceMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.TopicFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVarFiltCondDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTOPK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.Logger;

public class ConfigurationBeanServiceImpl implements ConfigurationBeanService {

	private static Logger _logger;
	private static ConfigurationBeanService _previosInstance;

	public static void staticInject(final Logger logger) {
		ConfigurationBeanServiceImpl._logger = logger;
	}

	private LocalStoreConfigurationService configurationService;

	private Configuration entireConfiguration;
	private final List<ConfigurationBeanServiceListener> listeners = new LinkedList<ConfigurationBeanServiceListener>();
	private final Map<Integer, AlarmbearbeiterBean> alarmbearbeiterBeans = new HashMap<Integer, AlarmbearbeiterBean>();
	private final Map<Integer, AlarmbearbeiterGruppenBean> alarmbearbeitergruppenBeans = new HashMap<Integer, AlarmbearbeiterGruppenBean>();
	private final Map<Integer, AlarmtopicBean> alarmtopicBeans = new HashMap<Integer, AlarmtopicBean>();

	private final Map<Integer, FilterbedingungBean> filterbedingungBeans = new HashMap<Integer, FilterbedingungBean>();

	private final Map<Integer, FilterBean> filterBeans = new HashMap<Integer, FilterBean>();
	// Rubriks don't need to be beans.
	private Collection<RubrikDTO> rubrikDTOs = new LinkedList<RubrikDTO>();

	private MessageTemplateBean[] messageTemplateBeans;

	public ConfigurationBeanServiceImpl() {
		if (ConfigurationBeanServiceImpl._previosInstance != null) {
			throw new RuntimeException(
					"Could not use more than one bean service at this step of developement."); //$NON-NLS-1$
		}
		ConfigurationBeanServiceImpl._previosInstance = this;
	}

	public void addConfigurationBeanServiceListener(
			final ConfigurationBeanServiceListener listener) {
		this.listeners.add(listener);
	}

	public void delete(final IConfigurationBean bean) throws StorageError,
			StorageException, InconsistentConfigurationException {
		try {
			if (bean instanceof AlarmbearbeiterBean) {
				this.deleteAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
			}
			if (bean instanceof AlarmbearbeiterGruppenBean) {
				this
						.deleteAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean) bean);
			}
			if (bean instanceof AlarmtopicBean) {
				this.deleteAlarmtopicBean((AlarmtopicBean) bean);
			}
			if (bean instanceof FilterBean) {
				this.deleteFilterBean((FilterBean) bean);
			}
			if (bean instanceof FilterbedingungBean) {
				this.deleteFilterbedingungBean((FilterbedingungBean) bean);
			}
			this.loadConfiguration();
			this.notifyDeleteListeners(bean);
		} catch (final InconsistentConfigurationException e) {
			ConfigurationBeanServiceImpl._logger.logErrorMessage(this,
					"Could not Delete Entry. Entry-Type not recognized: " //$NON-NLS-1$
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterBeans()
	 */
	@Override
    public AlarmbearbeiterBean[] getAlarmBearbeiterBeans() {
		final Collection<AlarmbearbeiterBean> values = this.alarmbearbeiterBeans
				.values();
		return values.toArray(new AlarmbearbeiterBean[values.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterGruppenBeans()
	 */
	@Override
    public AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans() {
		final Collection<AlarmbearbeiterGruppenBean> values = this.alarmbearbeitergruppenBeans
				.values();
		return values.toArray(new AlarmbearbeiterGruppenBean[values.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmTopicBeans()
	 */
	@Override
    public AlarmtopicBean[] getAlarmTopicBeans() {
		final Collection<AlarmtopicBean> values = this.alarmtopicBeans.values();
		return values.toArray(new AlarmtopicBean[values.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterBeans()
	 */
	@Override
    public FilterBean[] getFilterBeans() {
		final Collection<FilterBean> values = this.filterBeans.values();
		return values.toArray(new FilterBean[values.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterConditionBeans()
	 */
	@Override
    public FilterbedingungBean[] getFilterConditionBeans() {
		final Collection<FilterbedingungBean> values = this.filterbedingungBeans
				.values();
		return values.toArray(new FilterbedingungBean[values.size()]);
	}

	@Override
    public FilterbedingungBean[] getFilterConditionsBeans() {
		// TODO may cache results
		final List<FilterbedingungBean> lists = new LinkedList<FilterbedingungBean>();
		for (final FilterbedingungBean bean : this.getFilterConditionBeans()) {
			if (!(bean.getFilterSpecificBean() instanceof JunctorConditionForFilterTreeBean)) {
				lists.add(bean);
			}
		}
		return lists.toArray(new FilterbedingungBean[lists.size()]);
	}

	@Override
    public MessageTemplateBean[] getMessageTemplates() {
		return this.messageTemplateBeans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterBeans()
	 */
	@Override
    public String[] getRubrikNamesForType(final RubrikTypeEnum type) {
		final Collection<String> specificRubriks = new ArrayList<String>();
		for (final RubrikDTO rubrikDTO : this.rubrikDTOs) {
			if (rubrikDTO.getType().equals(type)) {
				specificRubriks.add(rubrikDTO.getCGroupName());
			}
		}

		return specificRubriks.toArray(new String[specificRubriks.size()]);
	}

	@Override
    public void refreshData() {
		alarmbearbeiterBeans.clear();
		alarmbearbeitergruppenBeans.clear();
		alarmtopicBeans.clear();
		filterbedingungBeans.clear();
		filterBeans.clear();

		this.loadConfiguration();
		for (final ConfigurationBeanServiceListener listener : this.listeners) {
			listener.onConfigurationReload();
		}
	}

	@Override
    public void removeConfigurationBeanServiceListener(
			final ConfigurationBeanServiceListener listener) {
		this.listeners.remove(listener);
	}

	@Override
    @SuppressWarnings("unchecked")//$NON-NLS-1$
	public <T extends IConfigurationBean> T save(final T bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		if (bean instanceof AlarmbearbeiterBean) {
			return (T) this.saveAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
		}
		if (bean instanceof AlarmbearbeiterGruppenBean) {
			return (T) this
					.saveAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean) bean);
		}
		if (bean instanceof AlarmtopicBean) {
			return (T) this.saveAlarmtopicBean((AlarmtopicBean) bean);
		}
		if (bean instanceof FilterBean) {
			return (T) this.saveFilterBean((FilterBean) bean);
		}
		if (bean instanceof FilterbedingungBean) {
			return (T) this.saveFilterbedingungBean((FilterbedingungBean) bean);
		}
		throw new RuntimeException("Failed saving unsupported bean " //$NON-NLS-1$
				+ bean.getClass());
	}

	public void setNewConfigurationStore(
			final LocalStoreConfigurationService localStore) {
		this.configurationService = localStore;
	}

	AlarmbearbeiterBean DTO2Bean(final AlarmbearbeiterDTO alarmbearbeiter) {
		final AlarmbearbeiterBean bean = new AlarmbearbeiterBean();
		bean.setActive(alarmbearbeiter.isActive());
		bean.setConfirmCode(alarmbearbeiter.getConfirmCode());
		bean.setEmail(alarmbearbeiter.getEmail());
		bean.setMobilePhone(alarmbearbeiter.getMobilePhone());
		bean.setName(alarmbearbeiter.getUserName());
		bean.setPhone(alarmbearbeiter.getPhone());
		bean.setPreferedAlarmType(alarmbearbeiter.getPreferedAlarmType());
		bean.setStatusCode(alarmbearbeiter.getStatusCode());
		bean.setUserID(alarmbearbeiter.getUserId());
		bean.setRubrikName(this.getRubrikNameForId(alarmbearbeiter
				.getGroupRef())); // GUI-Group
		// =
		// Rubrik

		return bean;
	}

	AlarmbearbeiterGruppenBean DTO2Bean(final AlarmbearbeiterGruppenDTO gruppe) {

		final AlarmbearbeiterGruppenBean bean = new AlarmbearbeiterGruppenBean();
		bean.setActive(gruppe.isActive());
		bean.setGroupID(gruppe.getUserGroupId());
		bean.setMinGroupMember(gruppe.getMinGroupMember());
		bean.setName(gruppe.getUserGroupName());
		bean.setTimeOutSec(gruppe.getTimeOutSec());
		bean.setRubrikName(this.getRubrikNameForId(gruppe.getGroupRef())); // GUI-Group
		// = Rubrik

		final List<User2GroupBean> list = new LinkedList<User2GroupBean>();
		final Map<User2GroupBean, User2UserGroupDTO> beanDTOMap = new HashMap<User2GroupBean, User2UserGroupDTO>();
		for (final User2UserGroupDTO map : gruppe
				.gibZugehoerigeAlarmbearbeiterMapping()) {
			final User2GroupBean bean2 = this.DTO2Bean(map, bean);
			list.add(bean2);
			beanDTOMap.put(bean2, map);
		}
		Collections.sort(list, new Comparator<User2GroupBean>() {
			
		    @Override
            public int compare(final User2GroupBean o1, final User2GroupBean o2) {
				return beanDTOMap.get(o1).getPosition()
						- beanDTOMap.get(o2).getPosition();
			}
		});
		bean.setUsers(list);
		return bean;
	}

	FilterBean DTO2Bean(final FilterDTO filterDTO)
			throws InconsistentConfigurationException {
		final FilterBean bean = new FilterBean();
		bean.setDefaultMessage(filterDTO.getDefaultMessage());
		bean.setFilterID(filterDTO.getIFilterID());
		bean.setName(filterDTO.getName());

		final List<FilterbedingungBean> conditions = bean.getConditions();
		conditions.clear();
		for (final FilterConditionDTO condition : filterDTO
				.getFilterConditions()) {
			final int filterConditionID = condition.getIFilterConditionID();
			final FilterbedingungBean filterbedingungBean = this.filterbedingungBeans
					.get(filterConditionID);
			conditions.add(filterbedingungBean);
		}
		bean.setConditions(conditions);
		bean.setRubrikName(this.getRubrikNameForId(filterDTO.getIGroupRef()));
		final List<FilterActionDTO> filterActions = filterDTO
				.getFilterActions();
		for (final FilterActionDTO filterActionDTO : filterActions) {
			FilterAction filterAction = null;
			if (filterActionDTO instanceof AbstAlarmbFilterActionDTO) {
				final AlarmbearbeiterFilterAction alarmbearbeiterFilterAction = new AlarmbearbeiterFilterAction();
				alarmbearbeiterFilterAction
						.setReceiver(this.alarmbearbeiterBeans
								.get(((AbstAlarmbFilterActionDTO) filterActionDTO)
										.getReceiver().getUserId()));
				filterAction = alarmbearbeiterFilterAction;
			} else if (filterActionDTO instanceof AbstAlarmbGruppenFilterActionDTO) {
				final AlarmbearbeitergruppenFilterAction alarmbearbeitergruppenFilterAction = new AlarmbearbeitergruppenFilterAction();
				alarmbearbeitergruppenFilterAction
						.setReceiver(this.alarmbearbeitergruppenBeans
								.get(((AbstAlarmbGruppenFilterActionDTO) filterActionDTO)
										.getReceiver().getUserGroupId()));
				filterAction = alarmbearbeitergruppenFilterAction;
			} else if (filterActionDTO instanceof TopicFilterActionDTO) {
				final AlarmTopicFilterAction alarmTopicFilterAction = new AlarmTopicFilterAction();
				alarmTopicFilterAction.setReceiver(this.alarmtopicBeans
						.get(((TopicFilterActionDTO) filterActionDTO)
								.getReceiver().getId()));
				filterAction = alarmTopicFilterAction;
			} else {
				throw new InconsistentConfigurationException(
						"Falscher ActionType für Filter in db."); //$NON-NLS-1$
			}
			filterAction.setType(filterActionDTO.getFilterActionType());
			filterAction.setMessage(filterActionDTO.getMessage());
			ConfigurationBeanServiceImpl._logger.logDebugMessage(this,
					"found action: " + filterAction.toString()); //$NON-NLS-1$

			bean.addFilterAction(filterAction);
		}

		return bean;
	}

	AlarmtopicBean DTO2Bean(final TopicDTO dto) {
		final AlarmtopicBean bean = new AlarmtopicBean();
		bean.setDescription(dto.getDescription());
		bean.setHumanReadableName(dto.getName());
		bean.setTopicID(dto.getId());
		bean.setTopicName(dto.getTopicName());
		bean.setRubrikName(this.getRubrikNameForId(dto.getGroupRef())); // GUI-Group
		// = Rubrik
		return bean;
	}

	private List<FilterConditionDTO> createFilterConditionDTOListForFilter(
			final Collection<FilterbedingungBean> conditions)
			throws InconsistentConfigurationException, StorageError,
			StorageException {

		final List<FilterConditionDTO> result = new LinkedList<FilterConditionDTO>();

		for (final FilterbedingungBean filterbedingungBean : conditions) {

			FilterConditionDTO conditionDTO = this
					.findDTO4Bean(filterbedingungBean);

			if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
				final JunctorConditionForFilterTreeBean junctorBean = (JunctorConditionForFilterTreeBean) filterbedingungBean;
				final List<FilterConditionDTO> listForFilter = this
						.createFilterConditionDTOListForFilter(junctorBean
								.getOperands());

				final JunctorCondForFilterTreeDTO newDTO = new JunctorCondForFilterTreeDTO();
				newDTO.setCName(junctorBean.getJunctorConditionType()
						.toString());
				newDTO.setCDesc(""); //$NON-NLS-1$
				newDTO.setIGroupRef(this.getRubrikIDForName(junctorBean
						.getRubrikName(), RubrikTypeEnum.FILTER_COND));
				newDTO.setOperator(junctorBean.getJunctorConditionType());

				conditionDTO = newDTO;
				((JunctorCondForFilterTreeDTO) conditionDTO)
						.setOperands(new HashSet<FilterConditionDTO>(
								listForFilter));
			}
			if (filterbedingungBean instanceof NotConditionForFilterTreeBean) {
				final NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) filterbedingungBean;

				final List<FilterConditionDTO> listForFilter = this
						.createFilterConditionDTOListForFilter(Collections
								.singletonList(notBean.getFilterbedingungBean()));

				final NegationCondForFilterTreeDTO newDTO = new NegationCondForFilterTreeDTO();

				newDTO.setCName("NOT"); //$NON-NLS-1$
				newDTO.setCDesc(""); //$NON-NLS-1$
				newDTO.setIGroupRef(this.getRubrikIDForName(notBean
						.getRubrikName(), RubrikTypeEnum.FILTER_COND));
				newDTO.setNegatedFilterCondition(listForFilter.get(0));

				conditionDTO = newDTO;
			}

			if (conditionDTO == null) {
				throw new InconsistentConfigurationException(
						"No DTO found for " + filterbedingungBean.toString()); //$NON-NLS-1$
			}

			result.add(conditionDTO);

		}

		return result;
	}

	private void deleteAlarmbearbeiterBean(final AlarmbearbeiterBean bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {

		AlarmbearbeiterDTO dto = null;
		for (final AlarmbearbeiterDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			this.configurationService.deleteDTO(dto);
			this.alarmbearbeiterBeans.remove(dto.getUserId());
			ConfigurationBeanServiceImpl._logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " + dto.getUserId() //$NON-NLS-1$
							+ " " + dto.getUserName()); //$NON-NLS-1$
		}
	}

	private void deleteAlarmbearbeiterGruppenBean(
			final AlarmbearbeiterGruppenBean bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {

		AlarmbearbeiterGruppenDTO dto = null;
		for (final AlarmbearbeiterGruppenDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmbearbeiterGruppen()) {
			if (potentialdto.getUserGroupId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			this.configurationService.deleteDTO(dto);
			this.alarmbearbeitergruppenBeans.remove(dto.getUserGroupId());
			ConfigurationBeanServiceImpl._logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " //$NON-NLS-1$
							+ dto.getUserGroupId() + " " //$NON-NLS-1$
							+ dto.getUserGroupName());
		}
	}

	private void deleteAlarmtopicBean(final AlarmtopicBean bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		TopicDTO dto = null;
		for (final TopicDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmtopics()) {
			if (potentialdto.getId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			this.configurationService.deleteDTO(dto);
			this.alarmtopicBeans.remove(dto.getId());
			ConfigurationBeanServiceImpl._logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " + dto.getId() //$NON-NLS-1$
							+ " " + dto.getTopicName()); //$NON-NLS-1$
		}

	}

	private void deleteFilterBean(final FilterBean bean)
			throws InconsistentConfigurationException {
		FilterDTO dto = null;
		for (final FilterDTO potentialdto : this.entireConfiguration
				.gibAlleFilter()) {
			if (potentialdto.getIFilterID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			try {
				this.configurationService.deleteDTO(dto);
			} catch (final StorageError e) {
				throw new InconsistentConfigurationException(
						"failed to deleteFilter()", e); //$NON-NLS-1$
			} catch (final StorageException e) {
				throw new InconsistentConfigurationException(
						"failed to deleteFilter()", e); //$NON-NLS-1$
			}
			this.filterBeans.remove(dto.getIFilterID());
			ConfigurationBeanServiceImpl._logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " //$NON-NLS-1$
							+ dto.getIFilterID() + " " + dto.getName()); //$NON-NLS-1$
		}
	}

	private void deleteFilterbedingungBean(final FilterbedingungBean bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		FilterConditionDTO dto = null;
		for (final FilterConditionDTO potentialdto : this.entireConfiguration
				.gibAlleFilterConditions()) {
			if (potentialdto.getIFilterConditionID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			this.configurationService.deleteDTO(dto);
			this.filterbedingungBeans.remove(dto.getIFilterConditionID());
			ConfigurationBeanServiceImpl._logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " //$NON-NLS-1$
							+ dto.getIFilterConditionID() + " " //$NON-NLS-1$
							+ dto.getCName());
		}
	}

	private FilterbedingungBean DTO2Bean(
			final FilterConditionDTO filterCondtionDTO) {
		FilterbedingungBean bean = new FilterbedingungBean();

		FilterConditionAddOnBean filterSpecificBean = null;
		if (filterCondtionDTO instanceof JunctorConditionDTO) {
			final JunctorConditionBean junctorConditionBean = new JunctorConditionBean();
			junctorConditionBean.setFirstCondition(this
					.DTO2Bean(((JunctorConditionDTO) filterCondtionDTO)
							.getFirstFilterCondition()));
			junctorConditionBean
					.setJunctor(((JunctorConditionDTO) filterCondtionDTO)
							.getJunctor());
			junctorConditionBean.setRubrikName(""); // RubrikName is set by the
													// //$NON-NLS-1$
			// main Bean.
			junctorConditionBean.setSecondCondition(this
					.DTO2Bean(((JunctorConditionDTO) filterCondtionDTO)
							.getSecondFilterCondition()));
			filterSpecificBean = junctorConditionBean;
		} else if (filterCondtionDTO instanceof ProcessVarFiltCondDTO) {
			final PVFilterConditionBean filterbedingungBean = new PVFilterConditionBean();
			filterbedingungBean.setRubrikName(""); //$NON-NLS-1$
			filterbedingungBean
					.setChannelName(((ProcessVarFiltCondDTO) filterCondtionDTO)
							.getCPvChannelName());
			filterbedingungBean
					.setCompareValue(((ProcessVarFiltCondDTO) filterCondtionDTO)
							.getCCompValue());
			filterbedingungBean
					.setOperator(((ProcessVarFiltCondDTO) filterCondtionDTO)
							.getPVOperator());
			filterbedingungBean
					.setSuggestedType(((ProcessVarFiltCondDTO) filterCondtionDTO)
							.getSuggestedPVType());
			filterSpecificBean = filterbedingungBean;
		} else if (filterCondtionDTO instanceof StringArFilterConditionDTO) {
			final StringArrayFilterConditionBean stringArrayFilterConditionBean = new StringArrayFilterConditionBean();
			stringArrayFilterConditionBean.setRubrikName(""); //$NON-NLS-1$

			stringArrayFilterConditionBean
					.setCompareValues(((StringArFilterConditionDTO) filterCondtionDTO)
							.getCompareValueStringList());
			stringArrayFilterConditionBean
					.setKeyValue(((StringArFilterConditionDTO) filterCondtionDTO)
							.getKeyValueEnum());
			stringArrayFilterConditionBean
					.setOperator(((StringArFilterConditionDTO) filterCondtionDTO)
							.getOperatorEnum());
			filterSpecificBean = stringArrayFilterConditionBean;
		} else if (filterCondtionDTO instanceof StringFilterConditionDTO) {
			final StringFilterConditionBean stringFilterConditionBean = new StringFilterConditionBean();
			stringFilterConditionBean.setRubrikName(""); //$NON-NLS-1$
			stringFilterConditionBean
					.setCompValue(((StringFilterConditionDTO) filterCondtionDTO)
							.getCompValue());
			stringFilterConditionBean
					.setKeyValue(((StringFilterConditionDTO) filterCondtionDTO)
							.getKeyValueEnum());
			stringFilterConditionBean
					.setOperator(((StringFilterConditionDTO) filterCondtionDTO)
							.getOperatorEnum());
			filterSpecificBean = stringFilterConditionBean;
		} else if (filterCondtionDTO instanceof TimeBasedFilterConditionDTO) {
			final TimeBasedFilterConditionBean timeBasedConditionBean = new TimeBasedFilterConditionBean();
			timeBasedConditionBean.setRubrikName(""); //$NON-NLS-1$
			timeBasedConditionBean
					.setConfirmCompValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getCConfirmCompValue());
			timeBasedConditionBean
					.setConfirmKeyValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getConfirmKeyValue());
			timeBasedConditionBean
					.setStartCompValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getCStartCompValue());
			timeBasedConditionBean
					.setStartKeyValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getStartKeyValue());
			timeBasedConditionBean
					.setConfirmOperator(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTBConfirmOperator());
			timeBasedConditionBean
					.setStartOperator(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTBStartOperator());
			timeBasedConditionBean
					.setTimeBehavior(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTimeBehavior());
			timeBasedConditionBean
					.setTimePeriod(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTimePeriod());
			filterSpecificBean = timeBasedConditionBean;
		} else if (filterCondtionDTO instanceof JunctorCondForFilterTreeDTO) {
			final JunctorCondForFilterTreeDTO fcdto = (JunctorCondForFilterTreeDTO) filterCondtionDTO;
			final JunctorConditionForFilterTreeBean jcfftBean = new JunctorConditionForFilterTreeBean();
			jcfftBean.setJunctorConditionType(fcdto.getOperator());
			for (final FilterConditionDTO innerFC : fcdto.getOperands()) {
				// TODO (gs) dieses Verhalten hebelt das eindeute hinzufügen in
				// die Map aus
				// sollte sich wie in der loadConfiguration() verhalten
				jcfftBean.addOperand(this.DTO2Bean(innerFC));
			}
			bean = jcfftBean;
		} else if (filterCondtionDTO instanceof NegationCondForFilterTreeDTO) {
			final NegationCondForFilterTreeDTO ncffDTO = (NegationCondForFilterTreeDTO) filterCondtionDTO;
			final NotConditionForFilterTreeBean notBean = new NotConditionForFilterTreeBean();

			// TODO (gs) dieses Verhalten hebelt das eindeute hinzufügen in die
			// Map aus
			// sollte sich wie in der loadConfiguration() verhalten
			notBean.setFilterbedingungBean(this.DTO2Bean(ncffDTO
					.getNegatedFilterCondition()));

			bean = notBean;
		}

		bean.setFilterbedinungID(filterCondtionDTO.getIFilterConditionID());
		bean.setDescription(filterCondtionDTO.getCDesc());
		bean.setName(filterCondtionDTO.getCName());
		bean.setRubrikName(this.getRubrikNameForId(filterCondtionDTO
				.getIGroupRef())); // GUI-Group
		// =
		// Rubrik
		if (filterSpecificBean != null) {
			bean.setFilterSpecificBean(filterSpecificBean);
		} else {
			// FCFFT und NCFFT haben keine speciefic beans!
			if (!(filterCondtionDTO instanceof JunctorCondForFilterTreeDTO)
					&& !(filterCondtionDTO instanceof NegationCondForFilterTreeDTO)) {
				throw new IllegalArgumentException(
						"Unrecognized FilterConditionDTO: " + filterCondtionDTO); //$NON-NLS-1$
			}
		}
		return bean;
	}

	private User2GroupBean DTO2Bean(final User2UserGroupDTO map,
			final AlarmbearbeiterGruppenBean groupBean) {
		final AlarmbearbeiterBean userBean = this.alarmbearbeiterBeans.get(map
				.getUser2UserGroupPK().getIUserRef());
		final User2GroupBean result = new User2GroupBean(userBean);

		result.setActive(map.isActive());
		result.setLastChange(new Date(map.getLastchange()));
		result.setRubrikName(""); //$NON-NLS-1$
		result.setActiveReason(map.getActiveReason());

		return result;
	}

	private FilterDTO findDTO4Bean(final FilterBean bean) {
		FilterDTO dto = null;
		for (final FilterDTO potentialdto : this.entireConfiguration
				.gibAlleFilter()) {
			if (potentialdto.getIFilterID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	/**
	 * @param condition
	 * @return
	 */
	private FilterConditionDTO findDTO4Bean(final FilterbedingungBean bean) {
		FilterConditionDTO filterConditionDTO = null;
		for (final FilterConditionDTO potentialdto : this.entireConfiguration
				.gibAlleFilterConditions()) {
			if (potentialdto.getIFilterConditionID() == bean
					.getFilterbedinungID()) {
				filterConditionDTO = potentialdto;
				break;
			}
		}
		return filterConditionDTO;
	}

	// private StringArrayFilterConditionCompareValuesDTO getCompareValueDTO(
	// final List<StringArrayFilterConditionCompareValuesDTO> oldCompareValues,
	// final FilterbedingungBean bean, final String compValue) {
	// for (final StringArrayFilterConditionCompareValuesDTO
	// stringArrayFilterConditionCompareValuesDTO : oldCompareValues) {
	// if (stringArrayFilterConditionCompareValuesDTO.getCompValue() ==
	// compValue) {
	// return stringArrayFilterConditionCompareValuesDTO;
	// }
	// }
	// final StringArrayFilterConditionCompareValuesDTO result = new
	// StringArrayFilterConditionCompareValuesDTO();
	// final StringArrayFilterConditionCompareValuesDTO_PK pk = new
	// StringArrayFilterConditionCompareValuesDTO_PK();
	// pk.setCompValue(compValue);
	// pk.setFilterConditionRef(bean.getFilterbedinungID());
	// result.setPk(pk);
	//
	// return result;
	// }

	private AlarmbearbeiterDTO findDTO4Bean(final AlarmbearbeiterBean bean) {
		AlarmbearbeiterDTO dto = null;
		for (final AlarmbearbeiterDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private AlarmbearbeiterGruppenDTO findDTO4Bean(
			final AlarmbearbeiterGruppenBean bean) {
		AlarmbearbeiterGruppenDTO dto = null;
		for (final AlarmbearbeiterGruppenDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmbearbeiterGruppen()) {
			if (potentialdto.getUserGroupId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private TopicDTO findDTO4Bean(final AlarmtopicBean bean) {
		TopicDTO dto = null;
		for (final TopicDTO potentialdto : this.entireConfiguration
				.gibAlleAlarmtopics()) {
			if (potentialdto.getId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private int getRubrikIDForName(final String rubrikName,
			final RubrikTypeEnum type) throws StorageError, StorageException,
			InconsistentConfigurationException {
		if ((rubrikName == null) || (rubrikName.length() == 0)) {
			return 0;
		}
		int result = 0;
		for (final RubrikDTO rubrikDTO : this.rubrikDTOs) {
			if (rubrikDTO.getCGroupName().equals(rubrikName)
					&& rubrikDTO.getType().equals(type)) {
				result = rubrikDTO.getIGroupId();
				break;
			}
		}
		if ((result == 0) && !("".equals(rubrikName))) { //$NON-NLS-1$
			final RubrikDTO newRubrikDTO = new RubrikDTO();
			newRubrikDTO.setCGroupName(rubrikName);
			newRubrikDTO.setType(type);
			this.configurationService.saveDTO(newRubrikDTO);
			result = newRubrikDTO.getIGroupId();
		}
		return result;
	}

	private String getRubrikNameForId(final int groupRef) {
		String result = ""; //$NON-NLS-1$
		for (final RubrikDTO rubrikDTO : this.rubrikDTOs) {
			if (rubrikDTO.getIGroupId() == groupRef) {
				result = rubrikDTO.getCGroupName();
				break;
			}
		}
		return result;
	}

	private void insertNotification(final IConfigurationBean bean) {
		for (final ConfigurationBeanServiceListener listener : this.listeners) {
			listener.onBeanInsert(bean);
		}
	}

	private void insertOrUpdateNotification(final IConfigurationBean bean,
			final boolean inserted) {
		if (inserted) {
			this.insertNotification(bean);
		} else {
			this.updateNotification(bean);
		}
	}

	private void loadConfiguration() {
		try {
			this.entireConfiguration = this.configurationService
					.getEntireConfiguration();
			// TODO Folgendes Exception-Handling überdenken....
			if (this.entireConfiguration == null) {
				throw new RuntimeException("Couldn't load the Configuration"); //$NON-NLS-1$
			}
			this.rubrikDTOs = this.entireConfiguration.gibAlleRubriken();

			final Collection<AlarmbearbeiterDTO> alarmbearbeiter = this.entireConfiguration
					.gibAlleAlarmbearbeiter();

			final Collection<DefaultFilterTextDTO> allDefaultFilterTexts = this.entireConfiguration
					.getAllDefaultFilterTexts();
			this.messageTemplateBeans = new MessageTemplateBean[allDefaultFilterTexts
					.size()];
			int i = 0;
			for (final DefaultFilterTextDTO dto : allDefaultFilterTexts) {
				this.messageTemplateBeans[i] = new MessageTemplateBean(dto
						.getMessageName(), dto.getText());
				i++;
			}

			for (final AlarmbearbeiterDTO alarmbearbeiterDTO : alarmbearbeiter) {
				final AlarmbearbeiterBean bean = this
						.DTO2Bean(alarmbearbeiterDTO);
				final AlarmbearbeiterBean origBean = this.alarmbearbeiterBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					this.alarmbearbeiterBeans.put(bean.getID(), bean);
				}
			}

			final Collection<AlarmbearbeiterGruppenDTO> alarmbearbeiterGruppen = this.entireConfiguration
					.gibAlleAlarmbearbeiterGruppen();
			for (final AlarmbearbeiterGruppenDTO alarmbearbeiterGruppenDTO : alarmbearbeiterGruppen) {
				final AlarmbearbeiterGruppenBean bean = this
						.DTO2Bean(alarmbearbeiterGruppenDTO);
				final AlarmbearbeiterGruppenBean origBean = this.alarmbearbeitergruppenBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					this.alarmbearbeitergruppenBeans.put(bean.getID(), bean);
				}
			}

			final Collection<TopicDTO> alarmtopics = this.entireConfiguration
					.gibAlleAlarmtopics();
			for (final TopicDTO topicDTO : alarmtopics) {
				final AlarmtopicBean bean = this.DTO2Bean(topicDTO);
				final AlarmtopicBean origBean = this.alarmtopicBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					this.alarmtopicBeans.put(bean.getID(), bean);
				}
			}

			final Collection<FilterConditionDTO> filterConditions = this.entireConfiguration
					.gibAlleFilterConditions();
			for (final FilterConditionDTO filterConditionDTO : filterConditions) {
				final FilterbedingungBean bean = this
						.DTO2Bean(filterConditionDTO);
				final FilterbedingungBean origBean = this.filterbedingungBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					this.filterbedingungBeans.put(bean.getID(), bean);
				}
			}

			final Collection<FilterDTO> filters = this.entireConfiguration
					.gibAlleFilter();
			for (final FilterDTO filter : filters) {
				final FilterBean bean = this.DTO2Bean(filter);
				final FilterBean origBean = this.filterBeans.get(new Integer(
						bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					this.filterBeans.put(bean.getID(), bean);
				}
			}

		} catch (final StorageError e) { // TODO mz: Exceptions durchwerfen!
			ConfigurationBeanServiceImpl._logger.logErrorMessage(this,
					"Could not load Configuration", e); //$NON-NLS-1$
		} catch (final StorageException e) {
			ConfigurationBeanServiceImpl._logger.logErrorMessage(this,
					"Could not load Configuration", e); //$NON-NLS-1$
		} catch (final InconsistentConfigurationException e) {
			ConfigurationBeanServiceImpl._logger.logErrorMessage(this,
					"Could not load Configuration", e); //$NON-NLS-1$
		}
	}

	private void notifyDeleteListeners(final IConfigurationBean bean) {
		for (final ConfigurationBeanServiceListener listener : this.listeners) {
			listener.onBeanDeleted(bean);
		}
	}

	private void removeJunctorConditionForFilterTreeBeans(
			final Collection<FilterConditionDTO> filterConditions) {
		for (final FilterConditionDTO filterConditionDTO : filterConditions) {
			if (filterConditionDTO instanceof JunctorCondForFilterTreeDTO) {
				final JunctorCondForFilterTreeDTO junctorDTO = (JunctorCondForFilterTreeDTO) filterConditionDTO;
				this.removeJunctorConditionForFilterTreeBeans(junctorDTO
						.getOperands());
				this.filterbedingungBeans.remove(new Integer(junctorDTO
						.getIFilterConditionID()));
			}
			if (filterConditionDTO instanceof NegationCondForFilterTreeDTO) {
				final NegationCondForFilterTreeDTO notDTO = (NegationCondForFilterTreeDTO) filterConditionDTO;
				this.removeJunctorConditionForFilterTreeBeans(Collections
						.singletonList(notDTO.getNegatedFilterCondition()));
				this.filterbedingungBeans.remove(new Integer(notDTO
						.getIFilterConditionID()));
			}
		}
	}

	private AlarmbearbeiterBean saveAlarmbearbeiterBean(
			final AlarmbearbeiterBean bean) throws StorageError,
			StorageException, InconsistentConfigurationException {
		boolean inserted = false;
		AlarmbearbeiterDTO dto = this.findDTO4Bean(bean);
		if (dto == null) {
			dto = new AlarmbearbeiterDTO();
			inserted = true;
		}
		dto.setActive(bean.isActive());
		dto.setConfirmCode(bean.getConfirmCode());
		dto.setEmail(bean.getEmail());
		dto.setMobilePhone(bean.getMobilePhone());
		dto.setUserName(bean.getName());
		dto.setPhone(bean.getPhone());
		dto.setPreferedAlarmType(bean.getPreferedAlarmType());
		dto.setStatusCode(bean.getStatusCode());
		dto.setGroupRef(this.getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.USER));

		this.configurationService.saveDTO(dto);
		this.loadConfiguration();

		final AlarmbearbeiterBean resultBean = this.alarmbearbeiterBeans
				.get(new Integer(dto.getUserId()));

		this.insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private AlarmbearbeiterGruppenBean saveAlarmbearbeiterGruppenBean(
			final AlarmbearbeiterGruppenBean bean) throws StorageError,
			StorageException, InconsistentConfigurationException {
		boolean inserted = false;
		AlarmbearbeiterGruppenDTO dto = this.findDTO4Bean(bean);
		if (dto == null) {
			dto = new AlarmbearbeiterGruppenDTO();
			inserted = true;
		}
		dto.setActive(bean.isActive());
		dto.setMinGroupMember(bean.getMinGroupMember());
		dto.setTimeOutSec(bean.getTimeOutSec());
		dto.setUserGroupName(bean.getName());
		dto.setGroupRef(this.getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.USER_GROUP));

		final Map<Integer, AlarmbearbeiterDTO> userDtos = new HashMap<Integer, AlarmbearbeiterDTO>();
		for (final AlarmbearbeiterDTO userDto : this.entireConfiguration
				.gibAlleAlarmbearbeiter()) {
			userDtos.put(userDto.getUserId(), userDto);
		}

		dto.alleAlarmbearbeiterEntfernen();

		final List<User2GroupBean> user2groupss = bean.getUsers();
		for (final User2GroupBean user2groupBean : user2groupss) {
			dto.alarmbearbeiterZuordnen(userDtos.get(user2groupBean
					.getUserBean().getID()), user2groupBean.isActive(),
					user2groupBean.getActiveReason(), user2groupBean
							.getLastChange());
		}

		try {
			this.configurationService.saveDTO(dto);
		} catch (final Throwable e) {
			ConfigurationBeanServiceImpl._logger.logFatalMessage(this,
					"failed to save group", e); //$NON-NLS-1$
			throw new StorageException("failed to save alarmbearbeitergruppe.", //$NON-NLS-1$
					e);
		}
		this.loadConfiguration();
		final AlarmbearbeiterGruppenBean resultBean = this.alarmbearbeitergruppenBeans
				.get(new Integer(dto.getUserGroupId()));
		this.insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private AlarmtopicBean saveAlarmtopicBean(final AlarmtopicBean bean)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		boolean inserted = false;
		TopicDTO dto = this.findDTO4Bean(bean);
		if (dto == null) {
			dto = new TopicDTO();
			inserted = true;
		}
		dto.setDescription(bean.getDescription());
		dto.setName(bean.getHumanReadableName());
		dto.setTopicName(bean.getTopicName());
		dto.setGroupRef(this.getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.TOPIC));

		this.configurationService.saveDTO(dto);
		this.loadConfiguration();
		final AlarmtopicBean resultBean = this.alarmtopicBeans.get(new Integer(
				dto.getId()));
		this.insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private FilterBean saveFilterBean(final FilterBean bean)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		boolean inserted = false;

		FilterDTO dto = this.findDTO4Bean(bean);
		if (dto == null) {
			dto = new FilterDTO();
			inserted = true;
		} else {
			this.removeJunctorConditionForFilterTreeBeans(dto
					.getFilterConditions());
		}
		dto.setDefaultMessage(bean.getDefaultMessage());

		final List<FilterConditionDTO> list = this
				.createFilterConditionDTOListForFilter(bean.getConditions());

		dto.setFilterConditions(list);

		List<FilterActionDTO> filterActionDTOs = dto.getFilterActions();

		List<FilterActionDTO> newActionDTOs = new ArrayList<FilterActionDTO>(
				bean.getActions().size());
		List<FilterAction> actions = bean.getActions();
		for (FilterAction filterAction : actions) {
			FilterActionType filterActionType = filterAction
					.getFilterActionType();

			boolean useOld = false;
			for (FilterActionDTO actionDTO : filterActionDTOs) {
				if (filterAction.getReceiver().getID() == actionDTO
						.getIReceiverRef()) {
					actionDTO.setMessage(filterAction.getMessage());
					actionDTO.setReceiver(findDTO4Bean(filterAction
							.getReceiver()));
					newActionDTOs.add(actionDTO);
					useOld = true;
				}
			}

			if (useOld) {
				continue;
			}

			if (filterActionType instanceof AlarmbFilterActionType) {
				AlarmbFilterActionType type = (AlarmbFilterActionType) filterActionType;
				AbstAlarmbFilterActionDTO actiondto = null;
				switch (type) {
				case EMAIL:
					actiondto = new AlarmbEmailFilterActionDTO();
					break;
				case SMS:
					actiondto = new AlarmbSMSFilterActionDTO();
					break;
				case VMAIL:
					actiondto = new AlarmbVoiceMailFilterActionDTO();
					break;
				}
				actiondto
						.setReceiver(findDTO4Bean((AlarmbearbeiterBean) filterAction
								.getReceiver()));
				actiondto.setMessage(filterAction.getMessage());
				newActionDTOs.add(actiondto);
			} else if (filterActionType instanceof AlarmbGruppenFilterActionType) {
				AlarmbGruppenFilterActionType type = (AlarmbGruppenFilterActionType) filterActionType;
				AbstAlarmbGruppenFilterActionDTO actiondto = null;
				switch (type) {
				case EMAIL:
					actiondto = new AlarmbGruppenEmailFilterActionDTO();
					break;
				case EMAIL_Best:
					actiondto = new AlarmbGruppenEmailBestFilterActionDTO();
					break;
				case SMS:
					actiondto = new AlarmbGruppenSMSFilterActionDTO();
					break;
				case SMS_Best:
					actiondto = new AlarmbGruppenSMSBestFilterActionDTO();
					break;
				case VMAIL:
					actiondto = new AlarmbGruppenVMailFilterActionDTO();
					break;
				case VMAIL_Best:
					actiondto = new AlarmbGruppenVMailBestFilterActionDTO();
					break;
				}
				actiondto
						.setReceiver(findDTO4Bean((AlarmbearbeiterGruppenBean) filterAction
								.getReceiver()));
				actiondto.setMessage(filterAction.getMessage());
				newActionDTOs.add(actiondto);
			} else if (filterActionType instanceof AlarmTopicFilterActionType) {
				TopicFilterActionDTO actiondto = new TopicFilterActionDTO();
				actiondto
						.setReceiver(findDTO4Bean((AlarmtopicBean) filterAction
								.getReceiver()));
				actiondto.setMessage(filterAction.getMessage());
				newActionDTOs.add(actiondto);
			}
		}

		dto.setFilterActions(newActionDTOs);
		dto.setName(bean.getName());
		dto.setIGroupRef(this.getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.FILTER));

		this.configurationService.saveDTO(dto);
		this.loadConfiguration();
		final FilterBean resultBean = this.filterBeans.get(new Integer(dto
				.getIFilterID()));

		this.insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private NewAMSConfigurationElementDTO findDTO4Bean(IReceiverBean receiver) {
		if (receiver instanceof AlarmbearbeiterBean) {
			return findDTO4Bean((AlarmbearbeiterBean) receiver);
		} else if (receiver instanceof AlarmbearbeiterGruppenBean) {
			return findDTO4Bean((AlarmbearbeiterGruppenBean) receiver);
		} else if (receiver instanceof AlarmtopicBean) {
			return findDTO4Bean((AlarmtopicBean) receiver);
		}
		return null;
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private FilterbedingungBean saveFilterbedingungBean(
			final FilterbedingungBean bean) throws StorageError,
			StorageException, InconsistentConfigurationException {
		boolean inserted = false;

		FilterConditionDTO filterConditionDTO = null;
		final Class<? extends AbstractConfigurationBean> beanClass = bean
				.getFilterSpecificBean().getClass();
		if (JunctorConditionBean.class.equals(beanClass)) {
			final JunctorConditionBean specificBean = (JunctorConditionBean) bean
					.getFilterSpecificBean();

			JunctorConditionDTO junctorConditionDTO = null;
			final FilterConditionDTO dto4Bean = this.findDTO4Bean(bean);
			if ((dto4Bean != null) && (dto4Bean instanceof JunctorConditionDTO)) {
				junctorConditionDTO = (JunctorConditionDTO) dto4Bean;
			} else {
				junctorConditionDTO = new JunctorConditionDTO();
				inserted = true;
			}

			junctorConditionDTO.setFirstFilterConditionRef(specificBean
					.getFirstCondition().getFilterbedinungID());
			junctorConditionDTO.setSecondFilterConditionRef(specificBean
					.getSecondCondition().getFilterbedinungID());

			// FIXME mw, mz 2008-07-21: Dieses Verhalten auf andrem Wege wieder
			// herstellen: Einfache Injektion der ersten und zweiten FC
			// junctorConditionDTO.injectYourselfYourChildren(entireConfiguration);

			junctorConditionDTO.setJunctor(specificBean.getJunctor());

			// result to be saved with configurationService
			filterConditionDTO = junctorConditionDTO;
		} else if (PVFilterConditionBean.class.equals(beanClass)) {
			final PVFilterConditionBean specificBean = (PVFilterConditionBean) bean
					.getFilterSpecificBean();

			ProcessVarFiltCondDTO pvFilterConditionDTO = null;
			final FilterConditionDTO dto4Bean = this.findDTO4Bean(bean);
			if ((dto4Bean != null)
					&& (dto4Bean instanceof ProcessVarFiltCondDTO)) {
				pvFilterConditionDTO = (ProcessVarFiltCondDTO) dto4Bean;
			} else {
				pvFilterConditionDTO = new ProcessVarFiltCondDTO();
				inserted = true;
			}
			pvFilterConditionDTO.setCPvChannelName(specificBean
					.getChannelName());
			pvFilterConditionDTO.setCCompValue(specificBean.getCompareValue());
			pvFilterConditionDTO.setPVOperator(specificBean.getOperator());
			pvFilterConditionDTO.setSuggestedPVType(specificBean
					.getSuggestedType());

			// result to be saved with configurationService
			filterConditionDTO = pvFilterConditionDTO;
		} else if (StringFilterConditionBean.class.equals(beanClass)) {
			final StringFilterConditionBean specificBean = (StringFilterConditionBean) bean
					.getFilterSpecificBean();

			StringFilterConditionDTO stringFilterConditionDTO = null;
			final FilterConditionDTO dto4Bean = this.findDTO4Bean(bean);
			if ((dto4Bean != null)
					&& (dto4Bean instanceof StringFilterConditionDTO)) {
				stringFilterConditionDTO = (StringFilterConditionDTO) dto4Bean;
			} else {
				stringFilterConditionDTO = new StringFilterConditionDTO();
				inserted = true;
			}

			stringFilterConditionDTO.setCompValue(specificBean.getCompValue());
			stringFilterConditionDTO.setKeyValue(specificBean.getKeyValue());
			stringFilterConditionDTO
					.setOperatorEnum(specificBean.getOperator());

			// result to be saved with configurationService
			filterConditionDTO = stringFilterConditionDTO;
		} else if (StringArrayFilterConditionBean.class.equals(beanClass)) {
			final StringArrayFilterConditionBean specificBean = (StringArrayFilterConditionBean) bean
					.getFilterSpecificBean();

			StringArFilterConditionDTO stringArrayFilterConditionDTO = null;
			final FilterConditionDTO dto4Bean = this.findDTO4Bean(bean);
			if ((dto4Bean != null)
					&& (dto4Bean instanceof StringArFilterConditionDTO)) {
				stringArrayFilterConditionDTO = (StringArFilterConditionDTO) dto4Bean;
				// stringArrayFilterConditionDTO
				// .setCompareValues(new
				// LinkedList<StringArrayFilterConditionCompareValuesDTO>());
			} else {
				stringArrayFilterConditionDTO = new StringArFilterConditionDTO();
				inserted = true;
			}

			// final List<StringArrayFilterConditionCompareValuesDTO>
			// oldCompareValues = new
			// LinkedList<StringArrayFilterConditionCompareValuesDTO>();
			// final Collection<StringArrayFilterConditionCompareValuesDTO>
			// allStringArrayCompareValues = this.entireConfiguration
			// .getAllStringArrayCompareValues();
			// for (final StringArrayFilterConditionCompareValuesDTO
			// stringArrayFilterConditionCompareValuesDTO :
			// allStringArrayCompareValues) {
			// if (stringArrayFilterConditionCompareValuesDTO
			// .getFilterConditionRef() == bean.getFilterbedinungID()) {
			// oldCompareValues
			// .add(stringArrayFilterConditionCompareValuesDTO);
			// }
			// }
			final List<StrgArFiltCondCompValDTO> currentCompareValues = new LinkedList<StrgArFiltCondCompValDTO>();
			for (final String compValue : specificBean.getCompareValues()) {
				// currentCompareValues.add(this.getCompareValueDTO(
				// oldCompareValues, bean, compValue));

				final StrgArFiltCondCompValDTO newCompValue = new StrgArFiltCondCompValDTO();
				final StrgArFiltCondCompValDTOPK pk = new StrgArFiltCondCompValDTOPK();
				pk.setCompValue(compValue);
				pk.setFilterConditionRef(bean.getFilterbedinungID());
				newCompValue.setPk(pk);
				currentCompareValues.add(newCompValue);
			}

			stringArrayFilterConditionDTO
					.setCompareValues(currentCompareValues);

			stringArrayFilterConditionDTO.setKeyValue(specificBean
					.getKeyValue());
			stringArrayFilterConditionDTO.setOperatorEnum(specificBean
					.getOperator());

			// result to be saved with configurationService
			filterConditionDTO = stringArrayFilterConditionDTO;
		} else if (TimeBasedFilterConditionBean.class.equals(beanClass)) {
			final TimeBasedFilterConditionBean specificBean = (TimeBasedFilterConditionBean) bean
					.getFilterSpecificBean();

			TimeBasedFilterConditionDTO timeBasedFilterConditionDTO = null;
			final FilterConditionDTO dto4Bean = this.findDTO4Bean(bean);
			if ((dto4Bean != null)
					&& (dto4Bean instanceof TimeBasedFilterConditionDTO)) {
				timeBasedFilterConditionDTO = (TimeBasedFilterConditionDTO) dto4Bean;
			} else {
				timeBasedFilterConditionDTO = new TimeBasedFilterConditionDTO();
				inserted = true;
			}

			timeBasedFilterConditionDTO.setCConfirmCompValue(specificBean
					.getConfirmCompValue());
			timeBasedFilterConditionDTO.setConfirmKeyValue(specificBean
					.getStartKeyValue());
			timeBasedFilterConditionDTO.setTBConfirmOperator(specificBean
					.getConfirmOperator());

			timeBasedFilterConditionDTO.setCStartCompValue(specificBean
					.getStartCompValue());
			timeBasedFilterConditionDTO.setStartKeyValue(specificBean
					.getStartKeyValue());
			timeBasedFilterConditionDTO.setTBStartOperator(specificBean
					.getStartOperator());

			timeBasedFilterConditionDTO.setTimeBehavior(specificBean
					.getTimeBehavior());
			timeBasedFilterConditionDTO.setTimePeriod(specificBean
					.getTimePeriod());

			// result to be saved with configurationService
			filterConditionDTO = timeBasedFilterConditionDTO;
		}

		filterConditionDTO.setIGroupRef(this.getRubrikIDForName(bean
				.getRubrikName(), RubrikTypeEnum.FILTER_COND));
		filterConditionDTO.setCName(bean.getName());
		filterConditionDTO.setCDesc(bean.getDescription());

		this.configurationService.saveDTO(filterConditionDTO);

		this.loadConfiguration();

		final FilterbedingungBean resultBean = this.filterbedingungBeans
				.get(new Integer(filterConditionDTO.getIFilterConditionID()));

		this.insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private void updateNotification(final IConfigurationBean bean) {
		for (final ConfigurationBeanServiceListener listener : this.listeners) {
			listener.onBeanUpdate(bean);
		}
	}

	@Override
    public boolean isDeleteable(IConfigurationBean bean) {
		if (bean instanceof AlarmbearbeiterBean) {
			if (isReferencedByFilterAction(bean,
					AlarmbFilterActionType.class)
					|| isReferencedByAlarmbearbeiterGruppen(bean)) {
				return false;
			}
		}
		if (bean instanceof AlarmbearbeiterGruppenBean) {
			if (isReferencedByFilterAction(bean,
					AlarmbGruppenFilterActionType.class)) {
				return false;
			}
		}
		if (bean instanceof AlarmtopicBean) {
			if (isReferencedByFilterAction(bean,
					AlarmTopicFilterActionType.class)) {
				return false;
			}
		}
		if (bean instanceof FilterbedingungBean) {
			Collection<FilterbedingungBean> filterConditions = this.filterbedingungBeans
					.values();
			for (FilterbedingungBean filterbedingungBean : filterConditions) {
				if (filterbedingungBean.getFilterSpecificBean() instanceof JunctorConditionBean) {
					JunctorConditionBean orBean = (JunctorConditionBean) filterbedingungBean
							.getFilterSpecificBean();
					if (orBean.getFirstCondition().getID() == bean.getID()
							|| orBean.getSecondCondition().getID() == bean
									.getID()) {
						return false;
					}
				}
			}

			Collection<FilterBean> filters = this.filterBeans.values();
			for (FilterBean filterBean : filters) {
				List<FilterbedingungBean> conditions = filterBean
						.getConditions();

				if (isReferencedBySubCondition(bean, conditions)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isReferencedBySubCondition(IConfigurationBean bean,
			Collection<FilterbedingungBean> conditions) {
		for (FilterbedingungBean condition : conditions) {
			if (condition.getID() == bean.getID()) {
				return true;
			}
			if (condition instanceof JunctorConditionForFilterTreeBean) {
				JunctorConditionForFilterTreeBean jcfftb = (JunctorConditionForFilterTreeBean) condition;
				if (isReferencedBySubCondition(bean, jcfftb.getOperands())) {
					return true;
				}
			}
			if (condition instanceof NotConditionForFilterTreeBean) {
				NotConditionForFilterTreeBean ncfftb = (NotConditionForFilterTreeBean) condition;
				if (isReferencedBySubCondition(bean, Collections
						.singleton(ncfftb.getFilterbedingungBean()))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isReferencedByAlarmbearbeiterGruppen(IConfigurationBean bean) {
		Collection<AlarmbearbeiterGruppenBean> alarmbearbeitergruppen = this.alarmbearbeitergruppenBeans
				.values();
		for (AlarmbearbeiterGruppenBean alarmbearbeiterGruppenBean : alarmbearbeitergruppen) {
			List<User2GroupBean> users = alarmbearbeiterGruppenBean.getUsers();
			for (User2GroupBean user2GroupBean : users) {
				if (user2GroupBean.getUserBean().getID() == bean.getID()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isReferencedByFilterAction(IConfigurationBean bean,
			Class<? extends FilterActionType> expectedActionType) {
		Collection<FilterBean> filters = this.filterBeans.values();
		for (FilterBean filterBean : filters) {
			List<FilterAction> actions = filterBean.getActions();
			for (FilterAction filterAction : actions) {
				IReceiverBean receiver = filterAction.getReceiver();
				if (filterAction.getFilterActionType().getClass()
						.isAssignableFrom(expectedActionType)
						&& receiver.getID() == bean.getID()) {
					return true;
				}
			}
		}
		return false;
	}

}
