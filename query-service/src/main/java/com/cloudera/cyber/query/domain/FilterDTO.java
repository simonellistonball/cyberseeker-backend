package com.cloudera.cyber.query.domain;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.cloudera.cyber.user.Filter;
import com.cloudera.cyber.user.FilterDirection;

import lombok.Data;

@Data
@Embeddable
public class FilterDTO implements Filter {
	private String field;
	@Embedded
	private List<String> predicates;
	
	private FilterDirection direction;
}
