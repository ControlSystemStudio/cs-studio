package org.csstudio.nams.application.department.decision.simplefilter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.WildcardStringCompare;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class SimpleFilterWorker {
    
    private final StandardAblagekorb<Vorgangsmappe> ausgangskorb;
    private final List<FilterDTO> filters;
    private final ILogger logger;
    
    public SimpleFilterWorker(Collection<FilterDTO> filters,
                              StandardAblagekorb<Vorgangsmappe> ausgangskorb,
                              ILogger logger) {
        this.ausgangskorb = ausgangskorb;
        this.logger = logger;
        this.filters = new ArrayList<FilterDTO>();
        for (FilterDTO filter : filters) {
            if (filter.isSimpleStringBasedFilter()) {
                this.filters.add(filter);
            }
        }
    }
    
    public void bearbeiteAlarmnachricht(AlarmNachricht alarmNachricht) {
        for (FilterDTO filter : filters) {
            applyFilterOnMessage(filter, alarmNachricht);
        }
    }
    
    private void applyFilterOnMessage(FilterDTO filter, AlarmNachricht alarmNachricht) {
        Map<MessageKeyEnum, String> alarmContents = alarmNachricht.getContentMap();
        StringFilterConditionDTO stringFilterConditionDTO =
                                                            (StringFilterConditionDTO) (filter
                                                                    .getFilterConditions().get(0));
        if (alarmContents.keySet().contains(stringFilterConditionDTO.getKeyValueEnum())) {
            String filterValue = stringFilterConditionDTO.getCompValue();
            boolean shouldSend;
            try {
                shouldSend =
                             WildcardStringCompare.compare(alarmContents
                                     .get(stringFilterConditionDTO.getKeyValueEnum()), filterValue);
            } catch (Exception wildCardCompareException) {
                logger.logErrorMessage(this, wildCardCompareException.getMessage());
                shouldSend = true;
            }
            if (shouldSend) {
                try {
                    Vorgangsmappe vorgangsmappe =
                                                  new Vorgangsmappe(Vorgangsmappenkennung
                                                                            .createNew(InetAddress
                                                                                               .getLocalHost(),
                                                                                       new Date()),
                                                                    alarmNachricht);
                    vorgangsmappe.setzePruefliste(new Pruefliste(Regelwerkskennung.valueOf(filter
                            .getIFilterID(), filter.getName()), null) {
                        @Override
                        public WeiteresVersandVorgehen gesamtErgebnis() {
                            return WeiteresVersandVorgehen.VERSENDEN;
                        }
                    });
                    logger.logInfoMessage(SimpleFilterWorker.class,
                                          "filter " + filter.getIFilterID() + " "
                                                  + filter.getName() + " sends: " + alarmNachricht);
                    this.ausgangskorb.ablegen(vorgangsmappe);
                } catch (UnknownHostException e) {
                    logger.logErrorMessage(this, e.getMessage());
                } catch (InterruptedException e) {
                    logger.logErrorMessage(this, e.getMessage());
                }
            }
        }
    }
}
