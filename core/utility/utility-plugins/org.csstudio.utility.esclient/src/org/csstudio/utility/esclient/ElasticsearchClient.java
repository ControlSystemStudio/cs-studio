package org.csstudio.utility.esclient;

import java.util.function.Function;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * Synchronous client to post a query to an Elasticsearch server.
 *
 * The query is passed in as a {@link JSONObject}. The results are returned one
 * by one to the provided callback function
 * {@link ElasticsearchClient#dataConsumer}.
 *
 * Additionally, the entire response is passed to the
 * {@link ElasticsearchClient#responseConsumer} function, so that all metadata
 * is available to the caller.
 *
 * Scrolling can be used if an unlimited number of results is requested.
 *
 * @author Michael Ritzert
 */
public class ElasticsearchClient
{
    protected static final Logger LOGGER = Logger
            .getLogger(ElasticsearchClient.class.getName());

    /** The name of the field in the query defining the scrolling page size. */
    protected static final String QUERY_PAGE_SIZE_FIELD = "size"; //$NON-NLS-1$
    /**
     * The name of the field in the second and later query containing the scroll
     * id.
     */
    protected static final String QUERY_SCROLL_ID = "scroll_id"; //$NON-NLS-1$
    /**
     * The name of the field in the second and later query containing the scroll
     * settings.
     */
    protected static final String QUERY_SCROLL_PARAMETER = "scroll"; //$NON-NLS-1$
    /**
     * The name of the array within the {@link #RESPONSE_HITS_FIELD} field
     * containing the actual hits.
     */
    protected static final String RESPONSE_HITS_ARRAY = "hits"; //$NON-NLS-1$
    /** The name of the field in the response containing the hit information. */
    protected static final String RESPONSE_HITS_FIELD = "hits"; //$NON-NLS-1$
    /** The field in the response containing the scroll id. */
    protected static final String RESPONSE_SCROLL_ID = "_scroll_id"; //$NON-NLS-1$
    /** The URL to access for the second and later scroll request. */
    protected static final String SCROLL_URL = "/_search/scroll"; //$NON-NLS-1$
    /**
     * The URL parameter to add for the first request to request scrolling mode.
     */
    protected static final String SCROLL_URL_PARAMETER = "?scroll="; //$NON-NLS-1$
    /** The URL part to append for search queries. */
    protected static final String SEARCH_URL = "/_search"; //$NON-NLS-1$

    /**
     * The {@link Client} to use for HTTP(S) requests.
     *
     * If this is {@code null}, the default
     * {@link com.sun.jersey.api.client.Client#create()} is used.
     */
    protected Client client = null;

    /** The function handling incoming results. */
    protected final Function<JSONObject, Boolean> dataConsumer;

    /** The function receiving the full response. */
    protected Function<JSONObject, Boolean> responseConsumer = null;

    /** The active {@link ScrollSettings}. */
    protected ScrollSettings scrollSettings;

    /** The URL of the Elasticsearch server. */
    protected final String server;

    /**
     * Initialize with the required settings.
     *
     * @param server
     *            The URL of the Elasticsearch REST API.
     * @param dataConsumer
     *            The callback function for results.
     */
    public ElasticsearchClient(String server,
            Function<JSONObject, Boolean> dataConsumer)
    {
        if ((null == server) || server.isEmpty())
        {
            throw new IllegalArgumentException("server is required."); //$NON-NLS-1$
        }
        if (null == dataConsumer)
        {
            throw new IllegalArgumentException("dataConsumer is required."); //$NON-NLS-1$
        }
        this.server = server;
        this.dataConsumer = dataConsumer;
    }

    /**
     * Expire the scroll context on the server.
     *
     * @param scrollId
     *            The context's scroll id. Passing {@code null} will lead to no
     *            action being performed.
     */
    protected void clearScrolling(String scrollId)
    {
        if (null == scrollId)
        {
            return;
        }
        try
        {
            JSONObject clear = new JSONObject().put(QUERY_SCROLL_ID, scrollId);
            performQuery(SCROLL_URL, builder -> {
                return builder.delete(ClientResponse.class, clear.toString());
            });
        }
        catch (Throwable e)
        { // do not bother about errors. nothing we can do about it, anyway.
        }
    }

    /**
     * Execute a query against the Elasticsearch server.
     *
     * If scrolling is used, and a {@code size} field is present in the query,
     * the {@code size} field is discarded.
     *
     * @param index
     *            The index (and optionally the mapping, separated with /) to
     *            query.
     * @param query
     *            The query to perform in <a href=
     *            "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html">Query
     *            DSL</a> form.
     */
    public void executeQuery(String index, JSONObject query)
    {
        if ((null == index) || index.isEmpty())
        {
            throw new IllegalArgumentException("index is required."); //$NON-NLS-1$
        }
        if (null == query)
        {
            throw new IllegalArgumentException("query is required."); //$NON-NLS-1$
        }

        // first query
        JSONObject scrollRequest = query;
        // TODO: properly encode all this
        String uri = "/" + index + SEARCH_URL; //$NON-NLS-1$
        if (null != this.scrollSettings)
        {
            if (null != query.remove(QUERY_PAGE_SIZE_FIELD))
            {
                LOGGER.warning(
                        String.format("Removed user-provided '%s' field.", //$NON-NLS-1$
                                QUERY_PAGE_SIZE_FIELD));
            }
            try
            {
                query.put(QUERY_PAGE_SIZE_FIELD,
                        this.scrollSettings.getPageSize());
                uri += SCROLL_URL_PARAMETER + this.scrollSettings.getTimeout();
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e.getMessage());
            }
        }

        try
        {
            do
            {
                String body = scrollRequest.toString();
                // perform the HTTP request
                JSONObject result = performQuery(uri, builder -> {
                    return builder.post(ClientResponse.class, body);
                });

                // all following requests go to this URL with the scroll id as
                // returned by handleResponse.
                scrollRequest = handleResponse(result);
                uri = SCROLL_URL;
            }
            while (null != scrollRequest);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Handle the response returned by the Elasticsearch server.
     *
     * This method takes care of distributing the returned hits to the callback
     * function, and determines whether another query in scrolling mode needs to
     * be performed.
     *
     * @param result
     *            The response received from the server.
     * @return If != null, use this String to perform the next query.
     * @throws JSONException
     */
    protected JSONObject handleResponse(JSONObject result) throws JSONException
    {
        // default: no scrolling ⇒ no next request
        JSONObject ret = null;
        String scrollId = null;
        // if we use scrolling, a _scroll_id is present and this has to be
        // passed in the next request.
        if (result.has(RESPONSE_SCROLL_ID))
        {
            if (null == this.scrollSettings)
            {
                // weird things going on.
                throw new RuntimeException(
                        "Scrolling result received, but scrolling not configured."); //$NON-NLS-1$
            }
            scrollId = result.getString(RESPONSE_SCROLL_ID);
            ret = new JSONObject()
                    .put(QUERY_SCROLL_PARAMETER,
                            this.scrollSettings.getTimeout())
                    .put(QUERY_SCROLL_ID, scrollId);
        }

        synchronized (this)
        {
            if (null != this.responseConsumer)
            {
                Boolean goOn = this.responseConsumer.apply(result);
                if ((null == goOn) || !goOn)
                {
                    // abort the query, if false is returned.
                    // (also guard against a null return.)
                    clearScrolling(scrollId);
                    return null;
                }
            }
        }

        final JSONArray array = result.getJSONObject(RESPONSE_HITS_FIELD)
                .getJSONArray(RESPONSE_HITS_ARRAY);
        final int count = array.length();
        if (0 == count)
        {
            // from the documentation
            // https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html:
            // "The size parameter allows you to
            // configure the maximum number of hits to be returned with each
            // batch of results. Each call to the scroll API returns the next
            // batch of results until there are no more results left to return,
            // ie the hits array is empty."
            // ⇒ no results = we're done
            clearScrolling(scrollId);
            return null;
        }
        for (int i = 0; i < count; ++i)
        {
            JSONObject msg = array.getJSONObject(i);
            Boolean cont = this.dataConsumer.apply(msg);
            if ((null == cont) || (!cont))
            {
                // the user returning false signals to end the processing.
                clearScrolling(scrollId);
                return null;
            }
        }

        return ret;
    }

    /**
     * Perform an HTTP query.
     *
     * @param uri
     *            The URI on the configured server.
     * @param method
     *            The method to call on the WebResource.Builder.
     * @return The JSON data returned by Elasticsearch.
     * @throws RuntimeException
     */
    JSONObject performQuery(String uri,
            Function<WebResource.Builder, ClientResponse> method)
            throws RuntimeException
    {
        // check, if the user configured a special client.
        Client c = null;
        boolean destroyClient = false;
        synchronized (this)
        {
            if (null != this.client)
            {
                c = this.client;
            }
            else
            {
                c = Client.create();
                destroyClient = true;
            }
        }

        try
        {
            // perform the HTTP request
            WebResource.Builder builder = c.resource(this.server + uri)
                    .type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            ClientResponse response = method.apply(builder);
            if (Status.OK != Status.fromStatusCode(response.getStatus()))
            {
                LOGGER.warning(response.getEntity(String.class));
                throw new RuntimeException(response.toString());
            }
            return new JSONObject(response.getEntity(String.class));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        finally
        {
            if (destroyClient)
            {
                c.destroy();
            }
        }
    }

    /**
     * Set the {@link Client} to use for HTTP(S) requests to the server.
     *
     * Use this method if the default {@link Client} configuration is not
     * suitable, e.g. because a proxy needs to be used, or an HTTP/2 connection
     * can be shared.
     *
     * This class does not take ownership.
     *
     * @param client
     *            The {@link Client} to use. {@code null} to revert to the
     *            default {@code Client.create()}.
     */
    public void setClient(Client client)
    {
        synchronized (this)
        {
            this.client = client;
        }
    }

    /**
     * Set the function to call with the entire result.
     *
     * Within this function, all metadata of the response can be accessed. This
     * function is called (once for each chunk of scrolling data) before any
     * calls to the {@link ElasticsearchClient#dataConsumer} function receiving
     * the individual hits of this result set. If {@code false} is returned by
     * the function, further processing of the result set is immediately
     * terminated.
     *
     * Set to {@code null} to disable the functionality.
     *
     * @param responseConsumer
     *            The function to set.
     */
    public void setResponseConsumer(
            Function<JSONObject, Boolean> responseConsumer)
    {
        synchronized (this)
        {
            this.responseConsumer = responseConsumer;
        }
    }

    /**
     * Configure scrolling.
     *
     * The default setting is scrolling disabled.
     *
     * @param scrollSettings
     *            The scroll settings to use. Set {@code null} to deactivate
     *            scrolling.
     */
    public void setScrollSettings(ScrollSettings scrollSettings)
    {
        this.scrollSettings = scrollSettings;
    }
}
