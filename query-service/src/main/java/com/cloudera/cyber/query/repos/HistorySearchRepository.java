package com.cloudera.cyber.query.repos;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cloudera.cyber.query.domain.HistorySearch;

@RepositoryRestResource(collectionResourceRel = "history", path = "history")
public interface HistorySearchRepository extends PagingAndSortingRepository<HistorySearch, UUID> {
	List<HistorySearch> findByOwner(@Param("owner") String owner);
	
	@Modifying
	@Query("update HistorySearch h set h.completedAt = ?1 where h.id = ?2")
	void setCompletedAt(ZonedDateTime complete, UUID id);
}
