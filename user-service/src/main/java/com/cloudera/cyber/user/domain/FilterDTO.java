package com.cloudera.cyber.user.domain;

import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.cloudera.cyber.Filter;
import com.cloudera.cyber.FilterDirection;

import lombok.Data;

@Data
@Entity
public class FilterDTO implements Filter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String field;
	
	@ElementCollection
	@CollectionTable(name = "predicates", joinColumns = @JoinColumn(name = "filter_id"))
	private List<String> predicates;
	
	private FilterDirection direction;
}
