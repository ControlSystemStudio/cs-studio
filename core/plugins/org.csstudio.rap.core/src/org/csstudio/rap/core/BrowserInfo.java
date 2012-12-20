package org.csstudio.rap.core;

/**
 * Information about the browser that is opening the RAP application.
 * 
 * @author Xihui Chen
 * 
 */
public class BrowserInfo {
	public static final String ANDROID = "android"; //$NON-NLS-1$
	public static final String IPHONE = "iphone"; //$NON-NLS-1$
	public static final String IPOD = "ipod"; //$NON-NLS-1$
	public static final String IPAD = "ipad"; //$NON-NLS-1$
	public static final String BLACKBERRY = "blackberry"; //$NON-NLS-1$
	
	private String userAgentHeader;
	private String browserName;
	private String browserVersion;
	private String os;
	private Boolean isMobile = null;

	public BrowserInfo(String userAgentHeader, String browserName,
			String bowserVersion, String os) {
		this.userAgentHeader = userAgentHeader;
		this.browserName = browserName;
		this.browserVersion = bowserVersion;
		this.os = os;
	}

	public String getBrowserName() {
		return browserName;
	}

	public String getBowserVersion() {
		return browserVersion;
	}

	public String getOs() {
		return os;
	}
	
	public String getUserAgentHeader() {
		return userAgentHeader;
	}

	public synchronized Boolean isMobile(){
		if(isMobile == null){
			String ua = os.toLowerCase();
			isMobile = ua.contains(ANDROID)|ua.contains(IPHONE)
					|ua.contains(IPAD)|ua.contains(IPOD)|ua.contains(BLACKBERRY);
		}
		return isMobile;
	}

	@Override
	public String toString() {
		return browserName + "-" + browserVersion + " on " + os;
	}

	@SuppressWarnings("nls") //$NON-NLS-1$
	public static BrowserInfo getBrowserInfo(String userAgentHeader) {
		String browsername = userAgentHeader;
		String browserversion = "";
		String browser = userAgentHeader;
		String os = userAgentHeader.substring(userAgentHeader.indexOf('(') + 1,
				userAgentHeader.indexOf(')'));
		if (browser.contains("MSIE")) {
			String subsString = browser.substring(browser.indexOf("MSIE"));
			String Info[] = (subsString.split(";")[0]).split(" ");
			browsername = Info[0];
			browserversion = Info[1];
		} else if (browser.contains("Firefox")) {

			String subsString = browser.substring(browser.indexOf("Firefox"));
			String Info[] = (subsString.split(" ")[0]).split("/");
			browsername = Info[0];
			browserversion = Info[1];
		} else if (browser.contains("Chrome")) {

			String subsString = browser.substring(browser.indexOf("Chrome"));
			String Info[] = (subsString.split(" ")[0]).split("/");
			browsername = Info[0];
			browserversion = Info[1];
		} else if (browser.contains("Opera")) {

			String subsString = browser.substring(browser.indexOf("Opera"));
			String Info[] = (subsString.split(" ")[0]).split("/");
			browsername = Info[0];
			browserversion = Info[1];
		} else if (browser.contains("Safari")) {

			String subsString = browser.substring(browser.indexOf("Safari"));
			String Info[] = (subsString.split(" ")[0]).split("/");
			browsername = Info[0];
			browserversion = Info[1];
		}
		return new BrowserInfo(userAgentHeader, browsername, browserversion, os);

	}

}