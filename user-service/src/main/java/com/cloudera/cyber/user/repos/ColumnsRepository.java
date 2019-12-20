package com.cloudera.cyber.user.repos;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.cloudera.cyber.user.domain.ColumnDTO;

public interface ColumnsRepository extends CrudRepository<ColumnDTO, UUID>  {
}
