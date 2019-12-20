package com.cloudera.cyber.query;

import com.cloudera.cyber.QuerySpec;
import com.cloudera.cyber.query.domain.ResultSet;

import reactor.core.publisher.Mono;

public interface QueryService {
	Mono<ResultSet> query(QuerySpec query);
}
