package com.cloudera.cyber.query.repos;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.cloudera.cyber.query.domain.ColumnDTO;

public interface ColumnsRepository extends CrudRepository<ColumnDTO, UUID>  {
}
