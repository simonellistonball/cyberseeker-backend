package com.cloudera.cyber.query;

import java.time.ZonedDateTime;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.cloudera.cyber.query.domain.HistorySearch;
import com.cloudera.cyber.query.domain.SearchRequest;
import com.cloudera.cyber.query.domain.SearchResponse;
import com.cloudera.cyber.query.hive.HiveService;
import com.cloudera.cyber.query.repos.HistorySearchRepository;

import reactor.core.publisher.Mono;

@Component
public class QueryHandler {

	@Autowired
	HiveService hiveService;

	@Autowired
	HistorySearchRepository repository;

	public Mono<ServerResponse> search(ServerRequest request) {
		Mono<Object> results = request.bodyToMono(SearchRequest.class).map(historyEntity()).map(q -> {
			return hiveService.query(q)
					.map(rs -> {
						repository.setCompletedAt(ZonedDateTime.now(), q.getId());
						return rs;
					})
					.map(rs -> {
						// @formatter:off
						return SearchResponse.builder()
								.historyId(q.getId())
								.data(rs.getRows())
								.columns(rs.getColumns())
								.build();
						// @formatter:on
					}).cast(SearchResponse.class);
		});
		
		// log the request, get an ID for it and send this back with the results

		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(results)
				.onErrorReturn(ServerResponse.badRequest().bodyValue("Error").block());
	}

	private Function<SearchRequest, HistorySearch> historyEntity() {
		return q -> {
			HistorySearch historyEntity = HistorySearch.builder().dateFrom(q.getDateFrom()).dateTo(q.getDateTo())
					.query(q.getQuery()).sorts(q.getSorts()).filters(q.getFilters()).columns(q.getColumns())
					.runAt(ZonedDateTime.now()).preceding(repository.findById(q.getPrevious()).orElse(null)).build();
			repository.save(historyEntity);
			return historyEntity;
		};
	}

	public Mono<ServerResponse> health(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("true");
	}

}
