package com.cloudera.cyber.query.domain;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.cloudera.cyber.Filter;
import com.cloudera.cyber.FilterDirection;

import lombok.Data;

@Data
@Embeddable
public class FilterDTO implements Filter {
	private String field;
	@Embedded
	private List<String> predicates;
	
	private FilterDirection direction;
}
