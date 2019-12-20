package com.cloudera.cyber.user.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import com.cloudera.cyber.Filter;
import com.cloudera.cyber.QuerySpec;
import com.cloudera.cyber.Sort;

import lombok.Data;

@Entity
@Data
public class SavedSearch implements QuerySpec {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String owner;
	private String query;

	@ManyToMany(targetEntity = FilterDTO.class)
	private List<? extends Filter> filters;

	@ElementCollection
	@CollectionTable(name = "history_search_columns", joinColumns = @JoinColumn(name = "history_search_id"))
	private List<String> columns;

	@ElementCollection(targetClass = SortDTO.class)
	@CollectionTable(name = "history_search_sorts", joinColumns = @JoinColumn(name = "history_search_id"))
	private List<? extends Sort> sorts;

	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	
	private String name;
	
}
