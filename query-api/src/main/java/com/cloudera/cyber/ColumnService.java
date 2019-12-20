package com.cloudera.cyber;

import java.util.List;

public interface ColumnService {
	public default String getLabel(String columnName) {
		return findColumn(columnName).getLabel();
	}

	public default Column findColumn(String columnName) {
		return getAllColumns().stream().filter(c -> c.getName() == columnName).findFirst().get();
	};

	List<? extends Column> getAllColumns();
}
