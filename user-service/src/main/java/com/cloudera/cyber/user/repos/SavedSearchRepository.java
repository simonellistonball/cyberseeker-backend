package com.cloudera.cyber.user.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cloudera.cyber.user.domain.SavedSearch;

@RepositoryRestResource(collectionResourceRel = "savedSearch", path = "savedSearch")
public interface SavedSearchRepository extends CrudRepository<SavedSearch, UUID> {
	List<SavedSearch> findByName(@Param("name") String name);
}
