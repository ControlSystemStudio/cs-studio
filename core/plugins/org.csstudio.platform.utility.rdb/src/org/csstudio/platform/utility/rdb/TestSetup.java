package org.csstudio.platform.utility.rdb;

@SuppressWarnings("nls")
public interface TestSetup
{
    /** Default database URL */
    final public static String URL =
    // SNS Test w/ write access
    "jdbc:oracle:thin:chan_arch1/sns@//snsdb1.sns.ornl.gov:1521/prod";
}
