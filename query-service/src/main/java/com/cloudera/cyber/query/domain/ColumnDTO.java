package com.cloudera.cyber.query.domain;

import com.cloudera.cyber.Column;
import com.cloudera.cyber.ColumnType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnDTO implements Column {
	
	private String name;
	private ColumnType type;
	private String label;
}
