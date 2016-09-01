package org.csstudio.alarm.beast.history.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.csstudio.alarm.beast.history.views.PeriodicAlarmHistoryQuery.AlarmHistoryResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.sun.jersey.api.client.WebResource;

/**
 * @author Kunal Shroff
 *
 */
public class AlarmHistoryQueryJob extends Job {

    private final static String name = "AlarmHistoryQueryJob";

    private final AlarmHistoryQueryParameters query;
    private final WebResource webResource;

    AlarmHistoryQueryJob(AlarmHistoryQueryParameters query, WebResource webResource) {
        super(name);
        this.query = query;
        this.webResource = webResource;
    }

    void completedQuery(AlarmHistoryResult result) {

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        AlarmHistoryResult result = null;
        List<Map<String, String>> alarmMessages = new ArrayList<Map<String, String>>();
    try {
        String response = webResource.accept(MediaType.APPLICATION_JSON).post(String.class, query.getQueryString());

        try {
            JsonFactory factory = new JsonFactory();

            ObjectMapper mapper = new ObjectMapper(factory);
            JsonNode rootNode = mapper.readTree(response);

            JsonNode node = rootNode.get("hits").get("hits");
            for (JsonNode jsonNode : node) {
                alarmMessages.add(
                        mapper.readValue(jsonNode.get("_source"), new TypeReference<Map<String, String>>() {
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = new AlarmHistoryResult(alarmMessages, null);
    } catch (Exception e) {
        result = new AlarmHistoryResult(alarmMessages, e);
    } finally {
        if (!monitor.isCanceled()) {
                completedQuery(result);
        }
    }
    return Status.OK_STATUS;
    }

}
