package com.cloudera.cyber.user.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.cloudera.cyber.QuerySpec;

import lombok.Data;

@Entity
@Data
public class SavedSearch implements QuerySpec {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String owner;
	private String query;

	@Embedded
	private List<FilterDTO> filters;

	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
	
	@ManyToMany
	private List<String> columns;

	@Embedded
	private List<SortDTO> sorts;
	
	private String name;
	
}
