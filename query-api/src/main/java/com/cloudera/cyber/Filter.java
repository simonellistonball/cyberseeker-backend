package com.cloudera.cyber;

import java.util.List;

public interface Filter {
	String getField();
	List<String> getPredicates();
	FilterDirection getDirection();
}
