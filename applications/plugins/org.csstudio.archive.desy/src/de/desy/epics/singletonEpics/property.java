//package de.desy.eLogbook;
package de.desy.epics.singletonEpics;

public class property {
    
    /*
     * The eLogbook is storing logbook entries as well as images from screen dumps
     * but also any other kind of files in Oracle. 
     * The design is modular in a way that reading and writing to/ from eLog sources is
     * encapsulated in dedicated classes which are extended from the base class 
     * LogBook. 
     * A basic feature of this eLogbook is the ability to read entries from other 
     * logbooks and display internal and external logbook entries in the correct 
     * sequential order.
     * References in the Oracle-eLogbook can point to any other logbook.
     * Icons in the html pages will lead to these entries accordingly.
     * This implementation of the eLogbook is based on the logbook implemented
     * initially for TTF by the MVP group at DESY.
     *
     * (C) DESY Hamburg 2003
     *
     * @author Matthias Clausen DESY/MKS-2
     * @param 
     * @return
     * @version 1.5.9
     *
     * The property class
     * is used to define properties within the eLogbook package
     * Initial implementation by Matthias Clausen DESY/MKS-2 February-2003
     */
    //
    // INFO HISTORY
    //
    /*
    SourceInfo info = new SourceInfo("Matthias Clausen","1.0.0.4","2003.02.12",
    "start the implementation","","");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.6.0","2003.03.31",
    "add support for the howTo error facility","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.6.2","2003.04.01",
    "add limitations for maximum number of entries (text/image)","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.6.8","2003.04.04",
    "change default size of MIME images from 320 x 200 to 450 x 330","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
        SourceInfo info = new SourceInfo("Matthias Clausen","1.7.2","2003.04.15",
    "http address is now created dynamically, XSL file-path no longer hard coded (File:)","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.7.3.2","2003.04.23",
    "cookie names are created dynamically according to serverName-cookieName","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.8.3","2003.07-14",
    "add support for different sort options","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","1.9.3","2003.08-08",
    "use package de.desy.eLogbook","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    SourceInfo info = new SourceInfo("Matthias Clausen","2.0.0","2003.09-11",
    "new release","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    
    SourceInfo info = new SourceInfo("Matthias Clausen","2.1.0-T","2004-02-09",
    "add MVA","","logbooks and their properties should be stored in a special class and read on startup from an XML file");
    */
    
    //
    // D E B U G G I N G   H I N T S
    //
    // use the "-T" in the version number to identify debug versions
    // set the debug level DEFAULT_DEBUG_LEVEL to the desired value
    // set the WEB_SERVER_NAME to your local host
    // set the WEB_SERVER_DOMAIN accordingly
    // set EPICS_ENABLE_CA_PUT to true/ false
    // set ALARM_CONFIG_DIRECTORY to the directory on th local eLogbook server
    // set TEMPLATE_FILE_DIRECTORY to the directory on th local eLogbook server
    // set SERVLET_DIRECTORY to /elogbook/
    // set UTILITY_HOST_PORT_SERVICE to  + WEB_SERVER_NAME + ":8080/eLogbook";
    //
    // set MYSQL_HOST to the right name (must be localhost)
    //
    
    //
    // Version numbers go here
    //
    public final static String VERSION_NUMBER = "2.4.15";
    public final static String VERSION_DATE = " (09.09.2004)";
    public final static String VERSION = "eLog Vers. " + VERSION_NUMBER + VERSION_DATE;
    public final static String JDK_BUILD_VERSION = "J2sdk 1.4.2";
    
    //
    // Applet Version number
    //
    public final static String APPLET_VERSION_NUMBER = "2.5.0";
    public final static String APPLET_VERSION_DATE = " (22.07.2004)";
    public final static String APPLET_VERSION =  "Log Books:  V" + APPLET_VERSION_NUMBER;
    public final static String APPLET_JDK_BUILD_VERSION = "JDK 1.1.7a";
    
    //
    // epicsSingleton Version Numbers
    //
    public final static String EPICS_SINGLETON_VERSION_NUMBER = "1.2.1";
    public final static String EPICS_SINGLETON_VERSION_DATE = " (17.01.2005)";
    public final static String EPICS_SINGLETON_VERSION = "epicsSingletonVersion: " + EPICS_SINGLETON_VERSION_NUMBER + EPICS_SINGLETON_VERSION_DATE ;
    public final static String EPICS_SINGLETON_JDK_BUILD_VERSION = "J2sdk 1.4.2";
    public final static String CHANNEL_ACCESS_JCA_VERSION_NUMBER = "jca-2.1.5";
    public final static String CHANNEL_ACCESS_CAJ_VERSION_NUMBER = "caj-1.0.2d03";
    
    public final static int ONLINE = 1;
    public final static int TOMCAT = 2;
    
    public final static String ENTRY_MIME_TYPE = "GXL";
    
    public final static String LOGBOOK_SEPARATOR = ",";
    public final static char LOGBOOK_SEPARATOR_CHAR = ',';
    public final static char LOGBOOK_SEPARATOR_FOR_COOKIE_CHAR = '#';
    
    public final static int TYPE_ENTRY_ID_ONLY = 1;
    public final static int TYPE_IMAGE_ID_ONLY = 2;
    public final static int TYPE_SEARCH_FROM_TO = 3;
    public final static int TYPE_SEARCH_COMPLEX = 4;
    public final static int TYPE_SINGLE_LINE_MODE = 5;
    
    //
    // enum to specify output type
    //
    public final static int DISPLAY_TYPE_STDOUT = 0;
    public final static int DISPLAY_TYPE_HTML = 1;
    public final static int DISPLAY_TYPE_XML = 2;
    public final static int DISPLAY_TYPE_WAP = 3;   // will probably be specified by XSL file
    public final static int DISPLAY_TYPE_IMAGE = 4;
    public final static int DISPLAY_TYPE_NO_DISPLAY = 5;
    public final static int DISPLAY_TYPE_TXT_VALUE_ONLY = 6;
    public final static int DISPLAY_TYPE_HTML_VALUE_ONLY = 7;
    public final static int DISPLAY_TYPE_HTML_INPUT_READ = 8;
    public final static int DISPLAY_TYPE_HTML_INPUT_READ_WRITE = 9;
    public final static int DISPLAY_TYPE_HTML_FRAME_INPUT_READ = 10;
    public final static int DISPLAY_TYPE_HTML_FRAME_INPUT_READ_WRITE = 11;
    public final static int DISPLAY_TYPE_SEND_WRITE_OBJECT = 12;
    public final static int DISPLAY_TYPE_HTML_MONITOR = 13;
    public final static int DISPLAY_TYPE_HTML_SINGLE_LINE = 14;
    public final static int DISPLAY_TYPE_DEFAULT = 1;
    public final static int DISPLAY_TYPE_HTML_PALM = 15;
    public final static int DISPLAY_TYPE_MAX_ENUM = 15;
    // and the corresponding strings
    //
    // Display Types
    //
    public final static String HTML_GET_DISPLAY_TYPE = "DTYPE";
    //
    public final static String HTML_GET_DTYPE_STDOUT = "STDOUT";
    public final static String HTML_GET_DTYPE_HTML = "HTML";
    public final static String HTML_GET_DTYPE_XML = "XML";
    public final static String HTML_GET_DTYPE_WAP = "WAP";
    public final static String HTML_GET_DTYPE_IMAGE = "IMAGE";
    public final static String HTML_GET_DTYPE_NO_DISPLAY = "NO_DISPLAY";
    public final static String HTML_GET_DTYPE_TXT_VALUE_ONLY = "TXT_VALUE_ONLY";
    public final static String HTML_GET_DTYPE_HTML_VALUE_ONLY = "HTML_VALUE_ONLY";
    public final static String HTML_GET_DTYPE_HTML_INPUT_READ = "HTML_INPUT_READ";
    public final static String HTML_GET_DTYPE_HTML_INPUT_READ_WRITE = "HTML_INPUT_READ_WRITE";
    public final static String HTML_GET_DTYPE_HTML_FRAME_INPUT_READ = "HTML_FRAME_INPUT_READ";
    public final static String HTML_GET_DTYPE_HTML_FRAME_INPUT_READ_WRITE = "HTML_FRAME_INPUT_READ_WRITE";
    public final static String HTML_GET_DTYPE_SEND_WRITE_OBJECT = "SEND_WRITE_OBJECT";
    public final static String HTML_GET_DTYPE_HTML_MONITOR = "HTML_MONITOR";
    public final static String HTML_GET_DTYPE_HTML_SINGLE_LINE = "HTML_SINGLE_LINE";
    public final static String HTML_GET_DTYPE_DEFAULT = "DEFAULT";
    
    public final static String DISPLAY_TYPE[] = { HTML_GET_DTYPE_STDOUT, HTML_GET_DTYPE_HTML, HTML_GET_DTYPE_XML, 
                                            HTML_GET_DTYPE_WAP, HTML_GET_DTYPE_IMAGE, HTML_GET_DTYPE_NO_DISPLAY, 
                                            HTML_GET_DTYPE_TXT_VALUE_ONLY, HTML_GET_DTYPE_HTML_VALUE_ONLY, 
                                            HTML_GET_DTYPE_HTML_INPUT_READ, HTML_GET_DTYPE_HTML_INPUT_READ_WRITE,
                                            HTML_GET_DTYPE_HTML_FRAME_INPUT_READ, HTML_GET_DTYPE_HTML_FRAME_INPUT_READ_WRITE,
                                            HTML_GET_DTYPE_SEND_WRITE_OBJECT, HTML_GET_DTYPE_HTML_MONITOR,
                                            HTML_GET_DTYPE_HTML_SINGLE_LINE, HTML_GET_DTYPE_DEFAULT};
    
    //
    // debug levels
    //
    public final static int DEBUG_LEVEL_OFF = 0;
    public final static int DEBUG_LEVEL_LOW = 1;
    public final static int DEBUG_LEVEL_MED = 2;
    public final static int DEBUG_LEVEL_HIGH = 3;
    //
    // change debug level for the final version
    //
    public final static int DEFAULT_DEBUG_LEVEL = DEBUG_LEVEL_OFF;  // DEBUG_LEVEL_OFF / DEBUG_LEVEL_MED
    
    //
    // enum to specify data sources
    //
    public final static int DATA_SOURCE_XML = 0;
    public final static int DATA_SOURCE_ORACLE = 1;
    public final static int DATA_SOURCE_ORACLE_MKK = 2;
    public final static int DATA_SOURCE_CMLOG = 3;
    public final static int DATA_SOURCE_ORACLE_ALARM = 4;
    public final static int DATA_SOURCE_TINE_EVENT = 5;
    public final static int DATA_SOURCE_OPERATOR_LOG = 6;
    public final static int DATA_SOURCE_TEST = 7;
    public final static int DATA_SOURCE_IT_NEWS = 8;
    public final static int DATA_SOURCE_MYSQL = 9;
    public final static int DATA_SOURCE_ALARM_EPICS = 10;
    public final static int DATA_SOURCE_ALARM_D3 = 11;
    public final static int DATA_SOURCE_IOC_LOG_SERVER = 12;
    // and the corresponding strings
    public final static String DATA_SOURCE[] = { "XML", "ORACLE", "ORACLE_MKK" , 
                                                 "CMLOG" , "ORACLE_ALARM", "TINE_EVENT",
                                                 "OPERATOR_LOG","TEST","IT-NEWS","MySql",
                                                 "ALARM-E", "ALARM-D", "IOC-Log"};
    //
    // for new entries we use a 'virtual' data source - for the switch statement
    //
    public final static int DATA_SOURCE_NEW_ENTRY = 999;
    
    //
    // two arrays to define logbooks and their datasource type
    //
    // this could be stored in an XML file -> future!
    //
    public final static String PRIVATE_LOGBOOK = "private";        // private will be replaced by default user name
    public final static String PRIVATE_LOGBOOK_PRAEFIX = "P-";
    public final static int    PRIVATE_LOGBOOK_PRAEFIX_LENGTH = 2;
    public final static int    PRIVATE_DATA_SOURCE_TYPE = DATA_SOURCE_ORACLE;
    
    public final static String DEFAULT_LOGBOOK = "IT-NEWS";     // the one to view
    public final static String EDIT_LOGBOOK = "MKS-2";        // the one to edit/ modify
    public final static String DEFAULT_USER_NAME = "Nobody";
    public final static String LOGBOOK[] = { "MKS-2",   "KRYO",
                                             "CTA",     "KOMAG",
                                             "MKK",     "MKS-1",
                                             "MKS-3",   "MKS-4",
                                             "MVA",     "MHF",
                                             "MST",     "MDI",
                                             "MHF-SL",  "HASYLAB",
                                             "D3",      "D5",
                                             "EVENT",   "IT-NEWS",
                                             "HERA",    "eLog",
                                             "ALARM-KRYO", "ALARM-D3",
                                             "ALARM-SMS",  "ALARM-MKK",
                                             "ALARM-WASS", "ALARM-KLIM",
                                             "IOC-Log", "IOC-PutLog",
                                             "TTFX",    "HERAX",
                                             "PETRAX",  "DORISX",
                                             "DESY2X",  "DESY3X",
                                             "LINAC2X", "LINAC3X",
                                             "MySql",
                                             "HERA-X",
                                             "OP-LOG",  "CMLOG",
                                             "TTF", "ALARM",
                                             "TEST"};
    public final static int LOGBOOK_TYPE[] = {  DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE_MKK, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE, 
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE, 
                                                DATA_SOURCE_TINE_EVENT, DATA_SOURCE_IT_NEWS,
                                                DATA_SOURCE_ORACLE, DATA_SOURCE_ORACLE, 
                                                DATA_SOURCE_ALARM_EPICS, DATA_SOURCE_ALARM_D3,
                                                DATA_SOURCE_ALARM_EPICS, DATA_SOURCE_ALARM_EPICS,
                                                DATA_SOURCE_ALARM_EPICS, DATA_SOURCE_ALARM_EPICS,
                                                DATA_SOURCE_IOC_LOG_SERVER, DATA_SOURCE_IOC_LOG_SERVER,
                                                DATA_SOURCE_XML,DATA_SOURCE_XML,
                                                DATA_SOURCE_XML,DATA_SOURCE_XML,
                                                DATA_SOURCE_XML,DATA_SOURCE_XML,
                                                DATA_SOURCE_XML,DATA_SOURCE_XML,
                                                DATA_SOURCE_MYSQL,
                                                DATA_SOURCE_XML,
                                                DATA_SOURCE_OPERATOR_LOG,DATA_SOURCE_CMLOG,
                                                DATA_SOURCE_XML,DATA_SOURCE_ORACLE_ALARM,
                                                DATA_SOURCE_TEST};
    public final static int NUMBER_OF_LOGBOOK_TYPES = 43;
    //public final static int NUMBER_OF_LOGBOOK_TYPES_FOR_APPLET = 28;
    public final static int DEFAULT_LOGBOOK_TYPE = DATA_SOURCE_ORACLE;
    public final static String NEW_LOGBOOK_DEFAULT_TIME = "900101-00:00:00";
    
    //
    // seetings for the web server and the used servlets
    //
    // prodtest - changes go here
    // the WEB_SERVER_NAME is not necesserary for the servlets on Tomcat any more
    // the server name is creeated dynamically during runtime
    // BUT the applic needs a static name - 
    // until we add a parameter in the call during startup of the applet
    //
    // WEB_SERVER_NAME
    // = "NONE" - good for all cases if the host is not called by clients and html files with another (alias) name
    // else use here the alias name! alias.domain.name
    //
    // PRODUKTIONS-SERVER
    //
    public final static String WEB_SERVER_NAME = "elogbook.desy.de";   // common
    ///public final static String WEB_SERVER_NAME = "acclogbook.desy.de";   // HERA
    //
    // Test - Server
    //
    ///public final static String WEB_SERVER_NAME = "krykpcr.desy.de";    // development @ DESY
    ///public final static String WEB_SERVER_NAME = "claus-home";    // development @ home
    ///public final static String WEB_SERVER_NAME = "claus-x300";    // development on laptop
    //
    // UTILITY_HOST_PORT_SERVICE for common use
    //
    public final static String UTILITY_HOST_PORT_SERVICE = WEB_SERVER_NAME + ":8080/eLogbook";
    //
    // UTILITY_HOST_PORT_SERVICE for use on servers with binding between apache server and Tomcat
    //
    //public final static String UTILITY_HOST_PORT_SERVICE = WEB_SERVER_NAME + "eLogbook";
    //
    // UTILITY_HOST_PORT_SERVICE for installation on production server in test environment
    //
    ///public final static String UTILITY_HOST_PORT_SERVICE = WEB_SERVER_NAME + ":8080/eLogbook-test";
    //
    public final static String ELOGBOOK_INDEX_PAGE = "/index.html";
    public final static String UTILITY_VALUE = "VALUE";
    public final static String UTILITY_TEXT = "TEXT";
    public final static String UTILITY_ACTION = "ACTION";
    public final static String UTILITY_ACTION_VIEW = "VIEW";
    public final static String UTILITY_ACTION_LIST = "LIST";
    public final static String UTILITY_ACTION_DELETE = "DELETE";
    public final static String UTILITY_ACTION_EDIT = "EDIT";
    public final static String UTILITY_ACTION_NEW = "NEW";
    public final static String UTILITY_ACTION_VIEW_LIST = "VIEW_LIST";
    public final static String UTILITY_HOWTO_SERVICE_NAME = "HowTo";
    public final static String UTILITY_SEDAC_SERVICE_NAME = "UtilitySedac";
    public final static String UTILITY_SEDAC_SERVICE_CRATE = "Crate";
    public final static String UTILITY_SEDAC_SERVICE_EINSCHUB = "Einschub";
    public final static String UTILITY_SEDAC_SERVICE_DEVICE_NAME = "DeviceName";
    public final static String UTILITY_RECORD_SERVICE_NAME = "UtilityRecord";
    public final static String UTILITY_EPN_SERVICE_NAME = "UtilityEpn";
    public final static String UTILITY_ID_SERVICE_NAME = "UtilityId";
    public final static String UTILITY_IOC_SERVICE_NAME = "UtilityIoc";
    public final static String UTILITY_INFO_SERVICE_NAME = "UtilityInfo";
    public final static String UTILITY_INVENTAR_SERVICE_NAME = "UtilityInventar";
    public final static String UTILITY_TELNET_SERVICE_NAME = "UtilityTelnet";
    
    //
    // WEB_SERVER_DOMAIN
    // will be used if WEB_SERVER_NAME is not specified
    // format: domain.name
    public final static String WEB_SERVER_DOMAIN = "desy.de";           // web server name created in Request
    ///public final static String WEB_SERVER_DOMAIN = "";           // web server name created in Request
    public final static String WEB_SERVER_PORT = ":8080";
    // prodtest - changes go here
    ///public final static String SERVLET_DIRECTORY = "/eLogbook-test/";
    public final static String SERVLET_DIRECTORY = "/eLogbook/";
    public final static String SERVLET_CLASS_PATH = "de.desy.eLogbook";
    
    public final static String SERVLET_LIST = "ListEntry";
    public final static String SERVLET_GET_ENTRY = "GetEntry";
    public final static String SERVLET_GET_IMAGE = "GetImage";
    public final static String SERVLET_GET_JOI_EVENT = "GetJoiEvent";
    public final static String SERVLET_NEW_ENTRY = "ModifyEntry";  //"NewEntry";
    public final static String SERVLET_MODIFY_ENTRY = "ModifyEntry";
    public final static String SERVLET_VIEW_ENTRY = "ModifyEntry"; //"ViewEntry";
    public final static String SERVLET_NEW_COOKIE = "NewCookie";
    public final static String SERVLET_GET_ALARMS = "GetAlarms";
    public final static String SERVLET_NEW_USER_NAME = "NewUserName";
    public final static String SERVLET_SET_LANGUAGE = "SetLanguage";
    // XML-elog  servlet
    public final static String SERVLET_GET_XML_ELOG = "GetXmlLogbookEntries";
    
    //
    // howTo definitions
    //
    public final static String HOWTO_SERVLET_DIRECTORY = "/servlet/";
    
    // prodtest - changes go here
    //public final static String XSL_DIRECTORY = "C:/Tomcat/Tomcat 4.1/webapps/eLogbook/xsl/";
    //public final static String XSL_DIRECTORY = "C:/Tomcat/Tomcat 4.1/webapps/examples/eLogbook/xsl/";
    //public final static String XSL_DIRECTORY = "D:/apacheWebServer/Tomcat 4.1/webapps/examples/eLogbook/xsl/";
    public final static String XSL_DIRECTORY = SERVLET_DIRECTORY + "xsl/";
    
    
    //public final static String IMAGES_DIRECTORY = "/E:/Logbook/images";
    // prodtest - changes go here
    //public final static String IMAGES_DIRECTORY = "http://elogbook.desy.de:8080/eLogbook/images/";
    //public final static String IMAGES_DIRECTORY = "http://elogbook.desy.de:8080/examples/eLogbook/images/";
    //public final static String IMAGES_DIRECTORY = "http://krykpcr.desy.de:8080/examples/eLogbook/images/";
    public final static String IMAGES_DIRECTORY = SERVLET_DIRECTORY + "images";
    
    // icons
    public final static String ICON_LINK_FROM = "/hndarrw01d.gif";
    public final static String ICON_LINK_TO = "/hndarrw01c.gif";
    public final static String ICON_HOW_TO = "/help.gif";
    public final static String ICON_NEW_REFERENCE = "/ins_new_ref.gif";
    public final static String ICON_NEW_REFERENCE_CURRENT_DATE = "/ins_new_ref_cd.gif";
    public final static String ICON_NEW_ENTRY = "/new.gif";
    public final static String ICON_SEND_EMAIL = "/mailto.gif";
    public final static String ICON_ARROW_UP = "/arrow-up.png";
    public final static String ICON_ARROW_DOWN = "/arrow-down.png";
    public final static String ICON_HTTP_LINK = "/link.gif";
    public final static String ICON_PRINTER = "/Drucker.bmp";
    public final static String ICON_ELOGBOOK = "/elog-icon.gif";
    // sort images
    public final static String ICON_SORT_TIME = "/sortImages/SortTime.jpg";
    public final static String ICON_SORT_TIME_SELECTED = "/sortImages/SortTime-selected.jpg";
    public final static String ICON_SORT_SUBJECT = "/sortImages/SortSubject.jpg";
    public final static String ICON_SORT_SUBJECT_SELECTED = "/sortImages/SortSubject-selected.jpg";
    public final static String ICON_SORT_SEVERITY = "/sortImages/SortSeverity.jpg";
    public final static String ICON_SORT_SEVERITY_SELECTED = "/sortImages/SortSeverity-selected.jpg";
    public final static String ICON_SORT_AUTHOR = "/sortImages/SortAuthor.jpg";
    public final static String ICON_SORT_AUTHOR_SELECTED = "/sortImages/SortAuthor-selected.jpg";
    public final static String ICON_SORT_UPDATE = "/sortImages/SortUpdate.jpg";
    public final static String ICON_SORT_UPDATE_SELECTED = "/sortImages/SortUpdate-selected.jpg";
    
    //
    // HTML formatting
    //
    public final static String HTML_WIDTH = "450";
    public final static String HTML_HEIGHT = "330";
    public final static String HTML_BORDER = "2";
    public final static String HTML_HSPACE = "5";
    public final static String HTML_VSPACE = "5";
    
    //
    // HTML character
    //
    public final static String HTML_CHAR_SPACE= "&#160;";
    
    //
    // request strings for html get methods
    // 
    public final static String HTML_GET_IMAGE = "IMAGE";
    public final static String HTML_GET_ID = "ID";
    public final static String HTML_GET_FROM = "FROM";
    public final static String HTML_GET_TO = "TO";
    public final static String HTML_GET_SERACH_STRING = "SEARCH";
    public final static String HTML_GET_MIME_TYPE = "MIME_TYPE";
    public final static String HTML_GET_LOGBOOK = "LOGBOOK";
    public final static String HTML_GET_SEVERITY = "SEVERITY";
    public final static String HTML_GET_ASCENDING = "ASCENDING";
    public final static String HTML_GET_SELECT_TYPE = "SELECT_TYPE";
    public final static String HTML_GET_EDIT_MODE = "EDIT_MODE";
    public final static String HTML_GET_DEFAULT_LOGBOOK = "DEFAULT_LOGBOOK";
    public final static String HTML_SPECIAL_REQUEST = "SPECIAL_REQUEST";
    public final static String HTML_GET_SORT_BY = "SORT_BY";
    public final static String HTML_GET_SORT_BY_TIME = "TIME";
    public final static String HTML_GET_SORT_BY_SEVERITY = "SEVERITY";
    public final static String HTML_GET_SORT_BY_SUBJECT = "SUBJECT";
    public final static String HTML_GET_SORT_BY_AUTHOR = "AUTHOR";
    public final static String HTML_GET_SORT_BY_UPDATE = "UPDATE";
    
    public final static String HTML_GET_SUBJECT = "SUBJECT";
    // alarms
    public final static String HTML_GET_RECORD_NAME = "RECORD_NAME";
    public final static String HTML_GET_ALARM_PATH = "ALARM_PATH";
    public final static String HTML_GET_ALARM_CONFIGURATION = "ALARM_CONFIG";
    public final static String HTML_GET_ALARM_SORTED_BY = "SORT_BY";
    public final static String ALARM_DEVICE_NAME = "E:/Logbook/Alarm-Reader";
    // production
    public final static String ALARM_CONFIG_DIRECTORY = "C:\\Tomcat\\Tomcat 4.1\\webapps" + SERVLET_DIRECTORY + "config\\";
    // test
    ///public final static String ALARM_CONFIG_DIRECTORY = "F:\\apacheWebServer\\Tomcat 4.1\\webapps" + SERVLET_DIRECTORY + "config\\";
    public final static String TEMPLATE_FILE_DIRECTORY = "C:\\Tomcat\\Tomcat 4.1\\webapps" + SERVLET_DIRECTORY + "templates\\";
    ///public final static String TEMPLATE_FILE_DIRECTORY = "D:\\Apache\\Tomcat 4.1\\webapps" + SERVLET_DIRECTORY + "templates\\";
    ///public final static String TEMPLATE_FILE_DIRECTORY = "F:\\apacheWebServer\\Tomcat 4.1\\webapps" + SERVLET_DIRECTORY + "templates\\";
    
    //public final static String ALARM_DIRECTORY = "E:\\Logbook\\eLogbook-2.0.7-T\\config\\";
    
    public final static String DRIVE_LETTER_FOR_EPICS_FILES = "G";
    public final static String DRIVE_LETTER_FOR_D3_FILES = "Q";
    
    public final static String CONFIG_FILE_SEPARATOR = " ";
    // XML-elog servlet
    public final static String HTML_GET_PATH = "PATH";
    public final static String HTML_GET_SHIFT = "SHIFT";
    
    //
    // sort by enum
    //
    public final static int SORT_BY_TIME = 1;
    public final static int SORT_BY_SEVERITY = 2;
    public final static int SORT_BY_SUBJECT = 3;
    public final static int SORT_BY_AUTHOR = 4;
    public final static int SORT_BY_UPDATE_DATE = 5;
    
    //
    // all the strings in the input form which we can read in the doPost method
    //
    public final static String INPUT_FORM_ELOG_ID = "eLog_ID";
    public final static String INPUT_FORM_CATEGORY = "category";
    public final static String INPUT_FORM_LINK_ID = "linkID";
    public final static String INPUT_FORM_TIME = "time";
    public final static String INPUT_FORM_DATE = "date";
    public final static String INPUT_FORM_AUTHOR = "author";
    public final static String INPUT_FORM_EDIT_MODE = "edit_mode";
    public final static String INPUT_FORM_TITLE = "title";
    public final static String INPUT_FORM_TEXT = "text";
    public final static String INPUT_FORM_SEVERITY = "severity";
    public final static String INPUT_FORM_MIME_TYPE = "mimeType";
    public final static String INPUT_FORM_HOW_TO_ID = "howToID";
    public final static String INPUT_FORM_DEFAULT_LOGBOOK = "defaultLogbook";
    public final static String INPUT_FORM_EDIT_LOGBOOK = "editLogbook";
    public final static String INPUT_FORM_DEFAULT_USER_NAME = "defaultUserName";
    public final static String INPUT_FORM_DELETE_FLAG = "deleteFlag";
    public final static String INPUT_FORM_NO_UPDATE_FLAG = "noUpdateFlag";
    public final static String INPUT_FORM_KEYWORDS = "keywords";
    public final static String INPUT_FORM_KEYWORDS_INPUT = "keywords-input";
    public final static String INPUT_FORM_LOCATION = "location";
    public final static String INPUT_FORM_LOCATION_INPUT = "location-input";
    
    public final static String SEARCH_STRING_FOR_DELETED_ENTRIES = "DELETED_ENTRIES";
    
    //
    // client types enum
    //
    public final static String HTML_GET_CLIENT_TYPE = "CLIENT";
    public final static String HTML_CLIENT_TYPE_PC = "PC";
    public final static String HTML_CLIENT_TYPE_PALM = "PALM";
    public final static String HTML_CLIENT_TYPE_DEFAULT = HTML_CLIENT_TYPE_PC;
    
    //
    // specific strings to be set by the applet
    // to specify the well defined TO-Time
    // this is necessary for the automatic refresh procedure
    // if one of these strings appers the to time will be NOW
    // limiting statistical writes to MySql database:
    // For the 24h request:
    //  Only write statistics if to-from is within 24h + 5 minutes
    // For the 7d request:
    //  Only write statistics if to-from is within 7d + 5 minutes
    // For the NOW request (not set by applet - other applications might use it...:
    //  Write always statistics
    //
    public final static String APPLET_TO_TIME_NOW = "NOW";
    public final static String APPLET_TO_TIME_NOW24H = "NOW24H";
    public final static String APPLET_TO_TIME_NOW3D = "NOW3D";
    public final static String APPLET_TO_TIME_NOW7D = "NOW7D";
    public final static String APPLET_TO_TIME_NOW14D = "NOW14D";
    public final static String APPLET_TO_TIME_NOW21D = "NOW21D";
    
    //
    // strings for the JoiMint - Event - Config files
    //
    public final static String HTML_EVENT_ARCHIVE_SERVER = "ARCHIVE_S";
    public final static String HTML_EVENT_DEVICE_SERVER = "DEVICE_S";
    public final static String HTML_EVENT_NUMBER = "EVENT_N";
    
    //
    // html frames
    //
    public final static String FRAME_LIST_FRAME = "list_frame";
    
    //
    // edit mode flags
    //
    public final static int EDIT_MODE_REPLACE = 1;
    public final static int EDIT_MODE_NEW = 2;
    public final static int EDIT_MODE_NEW_REFERENCE = 3;
    public final static int EDIT_MODE_NEW_REFERENCE_CURRENT_DATE = 4;
    
    //
    // HTML output wrap string parameter
    //
    public final static int WRAP_STRING_SINGLE_LINE = 80;
    public final static int WRAP_STRING_AFTER = 70;
    public final static int WRAP_HOWTO_STRING_AFTER = 80;
    
    //
    // Simple date format config strings
    //
    public final static String LOGBOOK_DATE_FORMAT = "yyMMdd-HH:mm:ss";
    public final static String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String FROM_TO_DATE_FORMAT = "dd-MM-yy HH:mm:ss";
    public final static String FROM_TO_DATE_FORMAT_APPLET = "dd-MM-yy$HH:mm:ss";
    public final static String EPICS_ALARM_DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";
    public final static String EPICS_ALARM_FILE_DATE_FORMAT = "yyyy-MM-dd";
    public final static String D3_ALARM_DATE_FORMAT = "dd-MMM-yy HH:mm:ss";       // 02-Jan-04 23:20:55
    public final static String D3_ALARM_FILE_DATE_FORMAT = "ddMMMyy";             // AL14JAN04.001_CDCM
    public final static String HTML_DISPLAY_DATE_FORMAT = "dd-MMM-yyyy HH:mm:ss";
    public final static String DEFAULT_UPDATE_DATE = "2000-01-01 00:00:01.0";
    public final static String DEFAULT_UPDATE_DATE_INSERT = "2000-01-01 00:00:01";  // insert in ORACLE without ms
    
    //
    // settings for cookies
    //
    // in the end the name of the cookie will be:  serverName-cookieName
    public final static String COOKIE_INPUT_COOKIE = "cookie";
    public final static String COOKIE_FOR_DEFAULT_LOGBOOK = "eLogbookDefaultCookie";
    public final static String COOKIE_FOR_EDIT_LOGBOOK = "eLogbookEditCookie";
    public final static String COOKIE_FOR_DEFAULT_USER_NAME = "eLogbookDefaultUserName";
    public final static String COOKIE_FOR_LAST_SELECTED_LOGBOOKS = "eLogbookLastSelectedLogBooks";
    public final static String COOKIE_FOR_LANGUAGE = "eLogbookLanguage";
    public final static int    COOKIE_LIFE_TIME = 60*60*24*7*10; // ten weeks
    
    //
    // general limitations
    //
    public final static int LIMIT_MAX_TEXT_ENTRIES = 500;
    public final static int LIMIT_MAX_IMAGE_ENTRIES = 100;
    
    //
    // alternate text characters
    // for ' causing problems when stored in Oracle
    // < and > when displayed using XML
    //
    public final static char ALTERNATE_CHAR = '*';
    
    //
    // Oracle table limits
    //
    public final static int TABLE_MAX_SUBJECT_LENGTH = 100;
    public final static int TABLE_MAX_TEXT_LENGTH = 4000;
    public final static int TABLE_MAX_ID_LENGTH = 100;
    public final static int TABLE_MAX_HOW_TO_ID_LENGTH = 30;
    
    public final static int TABLE_HOWTO_MAX_DESCLONG_LENGTH = 4000;
    
    //
    // remove entries
    //
    public final static String DELETE_STRING = "DELETE_ENTRY";
    
    // ========================================================================
    //              section for special settings for datasources
    //
    // ================  TINE ================
    // maximum events from one device server
    public final static int TINE_MAX_EVENT_NUMBER = 100;
    
    // ================  MySql ================
    // maximum events from one device server
    public final static boolean STATISTIC_ENABLE = true;
    public final static String MYSQL_HOST = "localhost";  // krykpcn.desy.de - if the remote access is properly configured!
    ///public final static String MYSQL_HOST = "krykpcn.desy.de";  // krykpcn.desy.de - if the remote access is properly configured!
    
    //
    // MySql statistic table limitations
    //
    public final static int MYSQL_LIMIT_USER_NAME = 100;
    public final static int MYSQL_LIMIT_NODE_NAME = 50;
    public final static int MYSQL_LIMIT_ACTION = 50;
    public final static int MYSQL_LIMIT_ACTION_PARAMETER = 200;
    public final static int MYSQL_LIMIT_LOGBOOK_NAME = 50;

    
    // ================  EPICS ================
    // 
    
    // allow puts and gets?
    // for debugging we want to disable puts - 
    // it might cause problems if you archive your statistical values!
    //
    public final static boolean EPICS_ENABLE_CA_PUT = true;  // true / false
    public final static boolean EPICS_ENABLE_CA_GET = true;
    
    public final static String EPICS_SERVER_NAME = "krykpcn.desy.de";
    public final static int EPICS_SERVER_PORT_NUMBER = 8080;
    public final static String EPICS_SERVER_PORT = ":8080";
    public final static String EPICS_SERVLET_DIRECTORY = "/epics/";
    
    public final static String EPICS_SERVLET_EPICS_CA = "EpicsCa";
    
    public final static String EPICS_CA_GET = "GET";
    public final static String EPICS_CA_PUT = "PUT";
    public final static String EPICS_CA_VALUE = "VALUE";
    public final static String EPICS_CA_WRITE_ACCESS = "ACCESS";
    public final static String EPICS_CA_ACCESS_VALUE = "KRISTA2884";
    public final static String EPICS_CA_HTML_REFRESH = "REFRESH";
    
    public final static String EPICS_MONITOR = "MONITOR";
    public final static String EPICS_MONITOR_UPDATE_TIME = "MONITOR_UPDATE_TIME";
    
    public final static String EPICS_RECORD_NUMBER_OF_ENTRIES = "elog:NumberOfEntries_ai";
    public final static String EPICS_RECORD_SIZE_OF_ENTRIES = "elog:SizeOfEntries_ai";
    public final static String EPICS_RECORD_NUMBER_OF_ACCESS = "elog:NumberOfInteractions_ai";

    // =======  EPICS Channel Archiver ========
    public final static String EPICS_CHANNEL_ARCHIVER_MKS = "http://krynfs.desy.de:88/applets/LACA/cgi/CGIExport2.cgi?DIRECTORY=%2Fdata7%2FChannelArchiver%2FchannelReference2.mks&COMMAND=GET&FORMAT=PLOT";
    public final static String EPICS_CHANNEL_ARCHIVER_KRYO = "http://krynfs.desy.de:88/applets/LACA/cgi/CGIExport2.cgi?DIRECTORY=%2Fdata7%2FChannelArchiver%2FchannelReference2.kryo&COMMAND=GET&FORMAT=PLOT";
    
    // ================  cmlog ================
    // 
    public final static String CMLOG_SERVER_NAME = "kryksunj.desy.de";
    public final static int CMLOG_SERVER_PORT = 8900;
    public final static int CMLOG_PROTOCOL_MAJOR_VERSION = 2;  // 1 or 2
    public final static int CMLOG_WAIT_LOOP_COUNT = 4; // total waiting time = MLOG_WAIT_LOOP_COUNT * CMLOG_WAITING_TIME
    public final static int CMLOG_WAITING_TIME = 500; // ms
    
    //
    // multi language support
    //
    public final static int LANG_SELECTED_DEUTSCH = 0;
    public final static int LANG_SELECTED_ENGLISH = 1;
    public final static int LANG_SELECTED_NN = 2;
    public final static int LANG_SELECTED_MAX_NUMBER = 2;
    
    public final static int LANG_SELECTED_DEFAULT = LANG_SELECTED_DEUTSCH;
    
    public final static String[] LANG_SELECT = { "Deutsch", "English", "NN"};
    
    public final static String[] LANG_MAIN_PAGE_SEARCH_IN = { "Suchen in Logbuch: ", "Search in Logbook: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_ADD_TO = { "Meldungen hinzufügen in: ", "Add messages in: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_FROM = { "Von: ", "From: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_TO = { "Bis: ", "To: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_SEARCH_FOR = { "Suchen nach: ", "Search for: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_ELOG_SELECT = { "Ihr default(schreib) Logbuch auswaehlen", "-+-","-+-"};
    public final static String[] LANG_MAIN_PAGE_MAIL_ALL = { "Diese ausgewaehlten Eintraege als eMail versenden", "Send these selected entries as eMail","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINK_ALL = { "Http-Link dieser ausgewaehlten Eintraege", "Http-Link of these selected entries","-+-"};
    public final static String[] LANG_MAIN_PAGE_PRINTER_ALL = { "Diese Eintraege (zum Drucken) einzeln darstellen", "Show these entries (for printing)","-+-"};
    public final static String[] LANG_MAIN_PAGE_ASCENDING = { "Liste aufsteigend", "List Ascending","-+-"};
    public final static String[] LANG_MAIN_PAGE_DESCENDING = { "Liste absteigend", "List Descending","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINE_MODE_TEXT = { "Zum Zeilenmodus", "To Line-Mode","-+-"};
    public final static String[] LANG_MAIN_PAGE_FULL_MODE_TEXT = { "Zum Voll-Modus", "To Full-Mode","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINE_MODE = { "In Zeilenmodus umschalten", "Switch to Line-Mode","-+-"};
    public final static String[] LANG_MAIN_PAGE_FULL_MODE = { "In Voll-Modus umschalten", "Switch to Full-Mode","-+-"};
    public final static String[] LANG_MAIN_PAGE_SET_LANGUAGE = { "Sprache auswaehlen", "Select language","-+-"};
    public final static String[] LANG_MAIN_PAGE_LANGUAGE = { "Sprache: ", "Language: ","-+-"};
    
    public final static String[] LANG_MAIN_PAGE_SORT_BY = { "Sortiert nach: ", "Sorted by: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_SORT_TIME = { "Nach Ereigniszeit sortieren", "Sort by Event-Time","-+-"};
    public final static String[] LANG_MAIN_PAGE_SORT_SEVERITY = { "Nach Wichtigkeit sortieren", "Sort by Importance","-+-"};
    public final static String[] LANG_MAIN_PAGE_SORT_SUBJECT = { "Nach Subject sortieren", "Sort by Subject","-+-"};
    public final static String[] LANG_MAIN_PAGE_SORT_AUTHOR = { "Nach Autor sortieren", "Sort by Author","-+-"};
    public final static String[] LANG_MAIN_PAGE_SORT_UPDATE = { "Nach Aenderungsdatum sortieren", "Sort by Update","-+-"};
    
    public final static String[] LANG_MAIN_PAGE_NEW_ENTRY = { "Neuen Eintrag hinzufügen in: ", "Add new entry in eLog: ","-+-"};
    public final static String[] LANG_MAIN_PAGE_NEW_REFERENCE = { "Neue Referenz zu diesem Eintrag mit Referenz-Datum hinzufügen in: ", "Add new reference to this entry with the entries event date","-+-"};
    public final static String[] LANG_MAIN_PAGE_NEW_REFERENCE_CD = { "Neue Referenz zu diesem Eintrag mit aktuellem Datum hinzufügen in: ", "Add new reference to this entry with the current date","-+-"};
    public final static String[] LANG_MAIN_PAGE_EDIT = { "Diesen Eintrag aendern", "Modify this entry","-+-"};
    public final static String[] LANG_MAIN_PAGE_VIEW = { "Diesen Eintrag ansehen", "View this entry","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINK_BACK = { "Referenz auf diesen Eintrag anzeigen-> ", "Show reference to this entry-> ","-+-"};
    public final static String[] LANG_MAIN_PAGE_MAIL = { "Diesen Eintrag als eMail versenden", "Send this entry as eMail","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINK = { "Http-Link dieses Eintrags", "Http-Link of this entry","-+-"};
    public final static String[] LANG_MAIN_PAGE_PRINTER = { "Diesen Eintrag (zum Drucken) einzeln darstellen", "Show only this entry (for printing)","-+-"};
    public final static String[] LANG_MAIN_PAGE_LINK_ELOGBOOK = { "Zur eLogbuch Hauptseite", "To the eLogbook main page","-+-"};
    
    public final static String[] LANG_MAIN_PAGE_MAIL_BODY = {"Hallo,%20es%20gibt%20einen%20neuen%20Eintrag%20in:%20", "Hi,%20there%20is%20a%20new%20entry%20in:%20", "-+-"};
    
    public final static String[] LANG_HOWTO_MAIL = { "Diese HowTo Beschreibung als eMail versenden", "Send this HowTo description as eMail","-+-"};
    
    
    public final static String DATABASE_DYN_TAB_REFERENCE = "Dyn_Tab";
    public final static String DATABASE_HOST_PORT_SERVICE = "http://" + WEB_SERVER_NAME + ":8080/eLogbook";
    //public final static String DATABASE_HOST_PORT_SERVICE = "http://" + WEB_SERVER_NAME + "/eLogbook";
    
    public final static String DATABASE_DYN_TAB_SERVICE_NAME = "DatabaseDynTab";
    public final static String DATABASE_DYN_TAB_NAME = "DynTabName";
    public final static String DATABASE_DYN_TAB_CLASS = "DynTabClass";
    public final static String DATABASE_DYN_TAB_ROOT_ID = "DynTabRootId";
    public final static String DATABASE_DYN_TAB_ROOT_TABLE = "DynTabRootTable";
    public final static String DATABASE_DYN_TAB_REFERENCED_ID = "DynTabReferencedId";
    public final static String DATABASE_DYN_TAB_REFERENCED_SUB_ID = "DynTabReferencedSubId";
    public final static String DATABASE_DYN_TAB_PROPERTY_NAME = "DynTabPropertyName";
    public final static String DATABASE_INSTANCE_LIST = "InstanceList";
    public final static String DATABASE_DYN_TAB_EXPAND = "DynTabExpand";
    public final static String DATABASE_DYN_TAB_SHOW_ALL = "DynTabShowAll";
    
    public final static String DATABASE_ACTION_SHOW_PROPERTIES = "SHOW_PROPERTIES";
    public final static String DATABASE_ACTION_EDIT_PROPERTY = "EDIT_PROPERTY";
    public final static String DATABASE_ACTION_SAVE_ENTRY = "SAVE_ENTRY";
    //public final static String DATABASE_ACTION_PROPERTY_LIST = "SHOW_PROPERTY_LIST";
    public final static String DATABASE_ACTION_NEW_PROPERTY = "NEW_PROPERTY";
    public final static String DATABASE_ACTION_SAVE_NEW_PROPERTY = "SAVE_NEW_PROPERTY";
    public final static String DATABASE_ACTION_NEW_PROPERTY_INSTANCE = "NEW_PROPERTY_INSTANCE";
    public final static String DATABASE_ACTION_SHOW_INSTANCE = "SHOW_INSTANCE";
    public final static String DATABASE_ACTION_CREATE_TABLE_INSTANCE = "CREATE_TABLE_INSTANCE";
    public final static String DATABASE_ACTION_SAVE_TABLE_INSTANCE = "SAVE_TABLE_INSTANCE";
    public final static String DATABASE_ACTION_SHOW_TABLE_INSTANCE = "SHOW_TABLE_INSTANCE";
    public final static String DATABASE_ACTION_SAVE_PROPERTY_INSTANCE = "SAVE_PROPERTY_INSTANCE";
    public final static String DATABASE_ACTION_MODIFY_PROPERTY_INSTANCE = "MODIFY_PROPERTY_INSTANCE";
    public final static String DATABASE_ACTION_SHOW_ALL_INSTANCE_TABLES = "SHOW_ALL_INSTANCE_TABLES";
    public final static String DATABASE_DYN_TAB_DEFAULT_PROPERTY = "Beschreibung";
    public final static String DATABASE_DYN_TAB_DEFAULT_PROPERTY_VALUE = "Beschreibung eingeben";
    public final static String DATABASE_DYN_TAB_DEFAULT_CLASS_TABLE = "Class";
    
    
    public final static int DATABASE_DYN_TAB_MAX_LEVEL = 5;

    public property () {
    }
    
}