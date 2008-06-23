package org.csstudio.archive.rdb;

/** My test setup(s).
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestSetup
{
    /** Default database URL */
    final public static String URL =
    // SNS Test w/ write access
    //"jdbc:oracle:thin:chan_arch/sns@//snsrac1.sns.ornl.gov:1521/prod";
     "jdbc:oracle:thin:chan_arch/sns@//snsdb1.sns.ornl.gov:1521/prod";
        
    // SNS Test, read-only
    // "jdbc:oracle:thin:sns_reports/sns@//snsdb1.sns.ornl.gov:1521/prod";

    // MySQL Test
    // "jdbc:mysql://titan-terrier.sns.ornl.gov/archive?user=fred&password=$fred";
    
    /** Data types to test and the PVs to use for them */
    public enum TestType
    {
        DOUBLE("test_fred"),
        LONG("test_long_test17"),
        ENUM("test_enum_test_32"),
        STRING("test_string_test42"),
        ARRAY("test_array_test2");
        
        final private String pv_name;
        
        private TestType(final String pv_name)
        {
            this.pv_name = pv_name;
        }

        public String getPvName()
        {
            return pv_name;
        }
    }
}
