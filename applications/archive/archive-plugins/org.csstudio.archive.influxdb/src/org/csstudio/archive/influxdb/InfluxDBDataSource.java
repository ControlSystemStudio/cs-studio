package org.csstudio.archive.influxdb;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class InfluxDBDataSource {

    static public String DB_KEY = "db";
    static public String USER_KEY = "user";
    static public String PASSW_KEY = "password";
    static public String METADB_KEY = "meta_db";

    final protected String url;
    final protected Map<String, String> args;

    public String getURL() {
        return url;
    }

    public String getArg(final String argkey)
    {
        return args.get(argkey);
    }

    public String getArgRequired(final String argkey) throws Exception
    {
        final String ret = args.get(argkey);
        if (ret == null)
            throw new Exception ("Could not access datasource required argument: " + argkey);
        return ret;
    }

    InfluxDBDataSource(final String url, final Map<String, String> args)
    {
        this.url = url;
        this.args = args;
    }

    public static InfluxDBDataSource decodeURL(final String encoded_url) throws Exception {
        String[] strv = encoded_url.split(Pattern.quote("?"));

        if ((strv.length < 1) || (strv.length > 2))
            throw new Exception("Error parsing influxdb url: " + encoded_url);

        String[] urlv = strv[0].split(Pattern.quote("://"));

        if (urlv.length < 2)
            throw new Exception("Error parsing influxdb host url: " + strv[0]);

        final String actual_url = "http://" + urlv[1];
        final Map<String,String> arg_map = new HashMap<String,String>();

        if (strv.length > 1) {
            strv = strv[1].split(Pattern.quote("&"));
            for (int idx = 0; idx < strv.length; idx++) {
                String argv[] = strv[idx].split("=");
                if (argv.length < 2)
                    throw new Exception("Error parsing InfluxDB raw arg: " + strv[idx]);
                arg_map.put(argv[0], argv[1]);
            }
        }

        return new InfluxDBDataSource(actual_url, arg_map);
    }
}
