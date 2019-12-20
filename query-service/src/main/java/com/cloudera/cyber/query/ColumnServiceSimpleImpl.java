package com.cloudera.cyber.query;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudera.cyber.ColumnService;
import com.cloudera.cyber.query.domain.ColumnDTO;
import com.cloudera.cyber.user.Column;
import com.cloudera.cyber.user.ColumnType;

@Service
public class ColumnServiceSimpleImpl implements ColumnService {

	public Column findColumn(String columnName) {
		return ColumnDTO.builder().name(columnName).label(columnName).type(ColumnType.STRING).build();
	}

	@Override
	public List<Column> getAllColumns() {
		return Collections.emptyList();
	}

}
