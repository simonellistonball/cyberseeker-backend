package com.cloudera.cyber.query;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class QueryRouter {
	@Bean
	public RouterFunction<ServerResponse> route(QueryHandler queryHandler) {
		return RouterFunctions
				.route(RequestPredicates.POST("/search").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						queryHandler::search)
				.andRoute(RequestPredicates.GET("/health").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						queryHandler::health);

	}
}
