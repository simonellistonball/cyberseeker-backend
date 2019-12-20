package com.cloudera.cyber.query.domain;

import java.util.List;

import com.cloudera.cyber.user.Column;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultSet {
	private List<Row> rows;
	private String query;
	private List<? extends Column> columns;
	
	public static class ResultSetBuilder {
		public ResultSetBuilder row(Row row) {
			this.rows.add(row);
			return this;
		}
	}
}
