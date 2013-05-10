/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author shroffk
 *
 */
public class LogbookAutoCompleteSearchProvider extends
	AbstractAutoCompleteSearchProvider {

    @Override
    Map<String, List<String>> initializeKeyValueMap() {
	return Collections.emptyMap();
    }
}
