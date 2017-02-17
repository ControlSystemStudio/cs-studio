package org.csstudio.logbook.olog.property.fault;

import static org.csstudio.logbook.olog.property.fault.FaultAdapter.extractFaultFromLogEntry;
import static org.csstudio.logbook.olog.property.fault.FaultAdapter.faultString;
import static org.csstudio.logbook.olog.property.fault.FaultAdapter.faultSummaryString;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.olog.property.fault.Fault.BeamLossState;
import org.csstudio.logbook.olog.property.fault.FaultConfiguration.Group;
import org.csstudio.logbook.ui.LogQueryListener;
import org.csstudio.logbook.ui.PeriodicLogQuery;
import org.csstudio.logbook.ui.PeriodicLogQuery.LogResult;
import org.eclipse.core.runtime.Platform;
import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import jfxtras.controls.agenda.AgendaLastWeekDaysFromDisplayedSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentGroup;
import jfxtras.scene.control.agenda.Agenda.AppointmentImplLocal;
/**
 *
 * @author Kunal Shroff
 *
 */
public class FaultCalendar extends FXViewPart {

    // Initialize resources:
    public FaultCalendar() {
    }

    private LogbookClient logbookClient;

    private Agenda agenda;
    private AgendaLastWeekDaysFromDisplayedSkin skin;

    // Model
    private PeriodicLogQuery logQuery;
    private Map<Appointment, LogEntry> map;

    // Model listener
    private LogQueryListener listener = new LogQueryListener() {

        @Override
        public void queryExecuted(final LogResult result) {
            map = new HashMap<Appointment, LogEntry>();

            for (LogEntry log : result.logs) {
                Fault fault = FaultAdapter.extractFaultFromLogEntry(log);
                if (fault.getFaultOccuredTime() != null) {
                    AppointmentImplLocal appointment = new Agenda.AppointmentImplLocal();
                    appointment.withSummary(faultSummaryString(fault));
                    appointment.withDescription(faultString(fault));
                    appointment.withStartLocalDateTime(LocalDateTime.ofInstant(fault.getFaultOccuredTime(), ZoneId.systemDefault()));
                    if (fault.getFaultClearedTime() != null) {
                        appointment.withEndLocalDateTime(
                                LocalDateTime.ofInstant(fault.getFaultClearedTime(), ZoneId.systemDefault()));
                    } else {
                        appointment.withEndLocalDateTime(
                                LocalDateTime.ofInstant(fault.getFaultOccuredTime(), ZoneId.systemDefault()));
                    }
                    synchronized (this) {
                        if(groups!=null && !groups.isEmpty()){
                            int index = groups.indexOf(fault.getAssigned());
                            if(index >= 0 && index <= 22){
                                appointment.setAppointmentGroup(appointmentGroupMap.get(String.format("group%02d",(index+1))));
                            } else {
                                appointment.setAppointmentGroup(appointmentGroupMap.get(String.format("group%02d", 23)));
                            }
                        }
                    }
                    map.put(appointment, log);

                    if(fault.getBeamLossState() != null && fault.getBeamLossState().equals(BeamLossState.True)){
                        // This fault has additional information about beam loss
                        // create an additional appointment for this
                        AppointmentImplLocal beamLossAppointment = new Agenda.AppointmentImplLocal()
                                .withSummary(appointment.getSummary())
                                .withDescription(appointment.getDescription());
                        if (fault.getBeamlostTime() != null) {
                            beamLossAppointment.withStartLocalDateTime(
                                    LocalDateTime.ofInstant(fault.getBeamlostTime(), ZoneId.systemDefault()));
                            if (fault.getBeamRestoredTime() != null) {
                                beamLossAppointment.withEndLocalDateTime(
                                        LocalDateTime.ofInstant(fault.getBeamRestoredTime(), ZoneId.systemDefault()));
                            } else {
                                beamLossAppointment.withEndLocalDateTime(
                                        LocalDateTime.ofInstant(fault.getBeamlostTime(), ZoneId.systemDefault()));
                            }
                            beamLossAppointment
                                    .setAppointmentGroup(appointmentGroupMap.get(String.format("group%02d", 0)));
                            map.put(beamLossAppointment, log);
                        }
                    }
                }
            }
            Display.getDefault().asyncExec(() -> {
                agenda.appointments().setAll(map.keySet());
            });
        };
    };

    private Map<String, Agenda.AppointmentGroup> appointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
    private List<String> groups;

    private void initialize() {
        ExecutorService ex = Executors.newFixedThreadPool(1);
        ex.execute(() -> {
            try {
                logbookClient = LogbookClientManager.getLogbookClientFactory().getClient();
                logQuery = new PeriodicLogQuery("property:fault limit:200", logbookClient, 60, TimeUnit.SECONDS);
                logQuery.addLogQueryListener(listener);
                logQuery.start();

                // load the groups from the configured css file
                synchronized (this) {
                    groups = FaultConfigurationFactory.getConfiguration().getGroups().stream().map(Group::getName)
                            .collect(Collectors.toList());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Scene createFxScene() {
        AnchorPane anchorpane = new AnchorPane();
        final Scene scene = new Scene(anchorpane);
        agenda = new Agenda();
        agenda.setEditAppointmentCallback(new Callback<Agenda.Appointment, Void>() {

            @Override
            public Void call(Appointment param) {
                // show context menu
                return null;
            }
        });

        agenda.setActionCallback((appointment) -> {
            // show detailed view
            try {
                DetailsView detailView = (DetailsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .showView(DetailsView.ID);
                if(map != null){
                    LogEntry logEntry = map.get(appointment);
                    detailView.setFault(extractFaultFromLogEntry(logEntry));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        agenda.allowDraggingProperty().set(false);
        agenda.allowResizeProperty().set(false);
        // find the css file
        String faultCSS = Platform.getPreferencesService().getString("org.csstudio.logbook.olog.property.fault",
                            "fault.css", "Agenda.css", null);
        try {
            agenda.getStylesheets().add(getClass().getResource("/resources/Agenda.css").toString());
            agenda.getStylesheets().add(new File(faultCSS).toURI().toURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        appointmentGroupMap = agenda.appointmentGroups().stream()
                .collect(Collectors.toMap(AppointmentGroup::getDescription, Function.identity()));

        skin = new AgendaLastWeekDaysFromDisplayedSkin(agenda);
        skin.setDaysBeforeFurthest(-14);
        skin.setDaysAfterFurthest(7);
        agenda.setSkin(skin);

        AnchorPane.setTopAnchor(agenda, 6.0);
        AnchorPane.setBottomAnchor(agenda, 6.0);
        AnchorPane.setLeftAnchor(agenda, 6.0);
        AnchorPane.setRightAnchor(agenda, 6.0);
        anchorpane.getChildren().add(agenda);

        initialize();

        return scene;
    }

    @Override
    protected void setFxFocus() {
    }

    @Override
    public void dispose() {
        if (logQuery != null) {
            logQuery.removeLogQueryListener(listener);
            logQuery.stop();
        }
        super.dispose();
    }

}
