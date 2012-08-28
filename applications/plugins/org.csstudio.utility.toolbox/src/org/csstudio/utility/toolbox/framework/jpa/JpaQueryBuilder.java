package org.csstudio.utility.toolbox.framework.jpa;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.Option;

public class JpaQueryBuilder {
	
	public JpaQuery build(Class<?> clazz, List<SearchTerm> searchTerms,  Option<JoinClause> joinClaus, Option<FilterClause> additonalFilter,
				Option<OrderBy> orderBy) {

		Validate.notNull(clazz, "Clazz must not be null");
		Validate.notNull(searchTerms, "SearchTerms must not be null");
		Validate.notNull(joinClaus, "JoinClaus must not be null");
		Validate.notNull(additonalFilter, "AdditonalFilter must not be null");
		Validate.notNull(orderBy, "OrderBy must not be null");

		List<String> terms = new ArrayList<String>();
		String alias =  clazz.getSimpleName().toLowerCase();
		String clazzName = clazz.getSimpleName();

		// if the alias is order hibernate throws an exception (probably because of order by...) 
		if (alias.equalsIgnoreCase("order")) {
			alias = alias + "_";
		}
		
		for (SearchTerm searchTerm : searchTerms) {
			terms.add(searchTerm.asJpaTerm(alias));
		}

		StringBuffer sb = new StringBuffer();
		
		sb.append("from ")
			.append(clazzName)
			.append(" ").
			append(alias);
		
		if (joinClaus.hasValue()) {
			sb.append(" ")
			.append(joinClaus.get().getValue())
			.append(" ");
		}
		
		if (!terms.isEmpty()) {
			String filter = StringUtils.join(terms, " and ");
			sb.append(" where ");
			sb.append(filter);				
		}
		
		if (additonalFilter.hasValue()) {
			if (terms.isEmpty()) {
				sb.append(" where ");				
			} else {
				sb.append(" and ");								
			}
			sb.append(additonalFilter.get().getValue());
		}
		
		if (orderBy.hasValue()) {
			sb.append(" order by ")
				.append(alias)
				.append(".")
				.append(orderBy.get().getValue());
		}	
				
		return new JpaQuery(sb.toString());

	}
}
