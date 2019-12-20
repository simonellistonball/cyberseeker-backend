package com.cloudera.cyber.user;

public interface Column {
	String getName();
	String getLabel();
	ColumnType getType();
}
