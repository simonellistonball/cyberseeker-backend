package com.cloudera.cyber.query.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.jdo.annotations.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.cloudera.cyber.Filter;
import com.cloudera.cyber.QuerySpec;
import com.cloudera.cyber.Sort;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class HistorySearch implements QuerySpec {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String owner;
	private String query;

	@Embedded
	private List<? extends Filter> filters;

	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	
	@Embedded
	private List<String> columns;

	@Embedded
	private List<? extends Sort> sorts;
	
	private ZonedDateTime runAt;
	private ZonedDateTime completedAt;
	
	/**
	 * A flag set by the UI to indicate that this search is important in the history
	 */
	private Boolean important;
	
	/**
	 * Optional link to the previous search before this one for the trail of
	 * searches
	 */
	@OneToOne(optional = true)
	private HistorySearch preceding;

}
