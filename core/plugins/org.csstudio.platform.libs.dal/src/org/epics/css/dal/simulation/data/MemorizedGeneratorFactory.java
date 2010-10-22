package org.epics.css.dal.simulation.data;

import org.epics.css.dal.simulation.MemoryValueProvider;
import org.epics.css.dal.simulation.ValueProvider;

/**
 * 
 * <code>MemorizedGeneratorFactory</code> is a factory for memorized 
 * value providers
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MemorizedGeneratorFactory implements ValueProviderFactory {

	public <T> ValueProvider<T> createGenerator(Class<T> type, String... options) {
		return new MemoryValueProvider<T>();
	}
}
