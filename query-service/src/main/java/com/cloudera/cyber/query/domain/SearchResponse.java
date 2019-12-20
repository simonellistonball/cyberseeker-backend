package com.cloudera.cyber.query.domain;

import java.util.List;
import java.util.UUID;

import com.cloudera.cyber.user.Column;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResponse {
	private UUID historyId;
	private List<? extends Column> columns;
	private List<Row> data;
}
