package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;
import java.util.List;

public interface OrderPosFinder {
	List<OrderPos> findByBaNr(BigDecimal baNr);
}
