package org.csstudio.platform.ui.display;

/**Open display from external program, such as browser, alarm GUI...
 * @author Xihui Chen
 *
 */
public interface IOpenDisplayAction {
	
	/**Open display
	 * @param path the path of the display file
	 * @param data the input data. null if it is not necessary. The implementation will be respond
	 * to parse the data.
	 * @throws Exception
	 */
	public void openDisplay(String path, String data) throws Exception;

}
