package com.cloudera.cyber.query.hive;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.calcite.sql.parser.SqlParseException;

import com.cloudera.cyber.ColumnService;
import com.cloudera.cyber.query.QueryExpansion;
import com.cloudera.cyber.user.Filter;
import com.cloudera.cyber.user.QuerySpec;
import com.cloudera.cyber.user.SortDirection;

public class SimpleHQL {

	private QuerySpec q;

	private ColumnService columnService;

	private String viewName;

	public SimpleHQL(QuerySpec query, String viewName, ColumnService columnService) {
		this.q = query;
		this.viewName = viewName;
		this.columnService = columnService;
	}

	public String sql() throws SqlParseException {
		StringBuilder sb = new StringBuilder("FROM ").append(viewName).append(" SELECT ");
		sb.append(q.getColumns().stream().map(c -> {
			return String.format("`%s` as %s", c, columnService.getLabel(c));
		}).collect(Collectors.joining(",")));

		// add the query element
		QueryExpansion.expand(q.getQuery(), getColumns());

		if (q.getFilters().size() > 0) {
			sb.append(q.getFilters().stream().map(SimpleHQL::filterToCondition).collect(Collectors.joining(" AND ")));
		}

		sb.append(String.format(" AND timestamp between %d AND %d", normalizeDate(q.getDateFrom()),
				normalizeDate(q.getDateTo())));

		if (q.getSorts().size() > 0) {
			sb.append(" ORDER BY ");
			sb.append(q.getSorts().stream().map(s -> String.format("`%s` %s", s.getField(), getSql(s.getDirection())))
					.collect(Collectors.joining(",")));
		}
		return sb.toString();
	}

	/**
	 * Hook to normalize the dates, this should also round them to within a sensible
	 * period to ensure cache hits
	 * 
	 * @todo Rounding of the dates
	 * @param date
	 * @return
	 */
	private long normalizeDate(ZonedDateTime date) {
		return date.toEpochSecond();
	}

	/**
	 * Return a list of all the available columns for the expander
	 * 
	 * @return
	 */
	private List<String> getColumns() {
		return Collections.emptyList();
	}

	private String getSql(SortDirection direction) {
		if (direction == SortDirection.ASCEND)
			return "ASC";
		return "DESC";
	}

	private static String filterToCondition(Filter f) {
		return String.format("`%s` IN (%s)", f.getField(),
				f.getPredicates().stream().map(p -> String.format("'%s'", p)).collect(Collectors.joining(",")));
	}
}
