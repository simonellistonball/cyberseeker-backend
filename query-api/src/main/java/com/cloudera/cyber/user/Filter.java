package com.cloudera.cyber.user;

import java.util.List;

public interface Filter {
	String getField();
	List<String> getPredicates();
	FilterDirection getDirection();
}
