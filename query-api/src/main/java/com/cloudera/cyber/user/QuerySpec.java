package com.cloudera.cyber.user;

import java.time.ZonedDateTime;
import java.util.List;

public interface QuerySpec {
	String getQuery();
	List<String> getColumns();
	List<? extends Filter> getFilters();
	List<? extends Sort> getSorts();
	ZonedDateTime getDateFrom();
	ZonedDateTime getDateTo();	
}
