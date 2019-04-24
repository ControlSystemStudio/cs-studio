package org.csstudio.logging.es.archivedjmslog;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.csstudio.utility.esclient.ElasticsearchClient;
import org.csstudio.utility.esclient.ScrollSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Default filtering is by time stamp. It is assumed that the used index
 * includes a field of type date that represents this time stamp.
 * 
 * @author Michael Ritzert <michael.ritzert@ziti.uni-heidelberg.de>
 */
public class ElasticsearchModel<T extends LogMessage> extends ArchiveModel<T>
{
    /** Number of hits per page when scrolling is used. */
    protected static final int PAGE_SIZE = 10000;

    protected final String dateField;
    protected String mapping;
    protected final String server;
    protected Function<JSONObject, T> parser;

    protected Job queryJob;
    protected Class<T> parameterType;

    protected List<T> messages;

    @SuppressWarnings("unchecked")
    public ElasticsearchModel(String server, String mapping, String dateField,
            Function<JSONObject, T> parser)
    {
        Activator.checkParameterString(dateField, "dateField"); //$NON-NLS-1$
        Activator.checkParameterString(server, "server"); //$NON-NLS-1$
        Activator.checkParameterString(mapping, "mapping"); //$NON-NLS-1$
        Activator.checkParameter(parser, "parser"); //$NON-NLS-1$
        this.dateField = dateField;
        this.server = server;
        this.mapping = mapping;
        this.parser = parser;
        this.parameterType = ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * Build the query to be sent to the Elasticsearch server.
     * 
     * As a default, only filtering by the time stamp and limiting the number of
     * results is implemented. Override to change the query.
     */
    @SuppressWarnings("nls")
    protected JSONObject buildQuery(Instant from, Instant to, long maxResults)
            throws JSONException
    {
        JSONObject request = new JSONObject();
        JSONObject query = new JSONObject();
        request.put("query", query);

        JSONObject bool = new JSONObject();
        List<JSONObject> conditions = new LinkedList<>();
        List<JSONObject> not_conditions = new LinkedList<>();
        query.put("bool", bool);
        conditions.add(getTimeQuery(from, to));
        synchronized (this)
        {
            if (null != this.filters)
            {
                for (PropertyFilter filter : this.filters)
                {
                    if (filter.isInverted())
                    {
                        not_conditions.add(getFilter(filter));
                    }
                    else
                    {
                        conditions.add(getFilter(filter));
                    }
                }
            }
        }
        bool.put("filter", conditions);
        if (!not_conditions.isEmpty())
        {
            bool.put("must_not", not_conditions);
        }

        if (0 >= maxResults)
        {
            // if we want all results, we can use the fastest possible sort in
            // the database.
            request.put("sort", "_doc");
        }
        else
        {
            // however, if we need only the latest n results, we have to sort by
            // date.
            request.put("sort", new JSONObject().put(this.dateField, "desc"));
            request.put("size", maxResults);
        }
        return request;
    }

    protected JSONObject getFilter(PropertyFilter filter)
    {
        if (filter instanceof StringPropertyFilter)
        {
            return getFilterQuery((StringPropertyFilter) filter);
        }
        else if (filter instanceof StringPropertyMultiFilter)
        {
            return getFilterQuery((StringPropertyMultiFilter) filter);
        }
        throw new IllegalArgumentException("Filter type not supported.");
    }

    @SuppressWarnings("nls")
    protected JSONObject getFilterQuery(StringPropertyFilter filter)
    {
        try
        {
            return new JSONObject().put("match", new JSONObject()
                    .put(filter.getProperty(), filter.getPattern()));
        }
        catch (JSONException ex)
        {
            return null;
        }
    }

    @SuppressWarnings("nls")
    protected JSONObject getFilterQuery(StringPropertyMultiFilter filter)
    {
        try
        {
            return new JSONObject().put("terms", new JSONObject()
                    .accumulate(filter.getProperty(), filter.getPatterns()));
        }
        catch (JSONException ex)
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getMessages()
    {
        synchronized (this)
        {
            if (null != this.messages)
            {
                return this.messages.toArray((T[]) Array
                        .newInstance(this.parameterType, this.messages.size()));
            }
            else
            {
                return (T[]) Array.newInstance(this.parameterType, 0);
            }
        }
    }

    /**
     * Override if your time stamp format cannot be correctly converted to
     * millis since the epoch. E.g. if the time zone information is missing…
     */
    @SuppressWarnings("nls")
    protected JSONObject getTimeQuery(Instant from, Instant to)
            throws JSONException
    {
        JSONObject timematch = new JSONObject();
        timematch.put("gte", from.toEpochMilli());
        timematch.put("lte", to.toEpochMilli());
        timematch.put("format", "epoch_millis");
        return new JSONObject().put("range",
                new JSONObject().put(this.dateField, timematch));
    }

    @Override
    public void refresh(Instant from, Instant to, long maxResults)
    {
        Activator.checkParameter(from, "from"); //$NON-NLS-1$
        Activator.checkParameter(to, "to"); //$NON-NLS-1$
        synchronized (this)
        {
            // Cancel a job that might already be running
            if (null != this.queryJob) this.queryJob.cancel();
            this.queryJob = new Job("ES query")
            {
                @Override
                protected IStatus run(IProgressMonitor monitor)
                {

                    List<T> result = new LinkedList<>();
                    ScrollSettings ss = null;
                    if (0 >= maxResults)
                    {
                        // unlimited number of results requested ⇒ use
                        // scrolling.
                        ss = new ScrollSettings("1m", //$NON-NLS-1$
                                ElasticsearchModel.PAGE_SIZE);
                    }
                    try
                    {
                        ElasticsearchClient client = new ElasticsearchClient(
                                ElasticsearchModel.this.server, hit -> {
                                    Optional.ofNullable(
                                            ElasticsearchModel.this.parser
                                                    .apply(hit))
                                            .ifPresent(result::add);
                                    return !monitor.isCanceled();
                                });
                        client.setScrollSettings(ss);
                        JSONObject query = buildQuery(from, to, maxResults);
                        client.executeQuery(ElasticsearchModel.this.mapping,
                                query);
                        if (0 >= maxResults)
                        {
                            Collections.sort(result);
                        }
                    }
                    catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    synchronized (ElasticsearchModel.this)
                    {
                        ElasticsearchModel.this.messages = result;
                        ElasticsearchModel.this.queryJob = null;
                    }
                    ElasticsearchModel.this.sendCompletionNotification();
                    return Status.OK_STATUS;
                }
            };
            this.queryJob.schedule();
        }
    }
}
