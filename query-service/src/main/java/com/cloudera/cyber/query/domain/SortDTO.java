package com.cloudera.cyber.query.domain;

import javax.persistence.Embeddable;

import com.cloudera.cyber.user.Sort;
import com.cloudera.cyber.user.SortDirection;

import lombok.Data;

@Data
@Embeddable
public class SortDTO implements Sort {
	private String field;
	private SortDirection direction;
}
