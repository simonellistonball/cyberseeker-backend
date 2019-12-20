package com.cloudera.cyber.query.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.cloudera.cyber.Filter;
import com.cloudera.cyber.QuerySpec;
import com.cloudera.cyber.Sort;

import lombok.Data;

@Data
public class SearchRequest implements QuerySpec {
	
	private UUID previous;

	private String query;
	private List<? extends Filter> filters;
	private List<String> columns;
	private List<? extends Sort> sorts;

	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
}
