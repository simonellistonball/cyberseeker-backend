package com.cloudera.cyber.query;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.dialect.AnsiSqlDialect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryExpansion {
	private static Logger LOG = LoggerFactory.getLogger(QueryExpansion.class);

	public static class ExpansionVisitor extends SqlBasicVisitor<SqlNode> implements SqlVisitor<SqlNode> {
		private List<String> columns;

		public ExpansionVisitor(List<String> columns) {
			super();
			this.columns = columns;
		}

		public SqlNode visit(SqlCall call) {
			final SqlOperator operator = call.getOperator();
			List<SqlNode> operandList = call.getOperandList();

			if (SqlKind.FUNCTION.contains(operator.getKind())) {
				return expandFunction(operandList, operator);
			}

			if (operandList.get(0).getKind() == SqlKind.IDENTIFIER) {
				SqlIdentifier identifier = (SqlIdentifier) operandList.get(0);
				String field = identifier.names.get(0);
				if (field.contains(EXPAND_PLACEHOLDER)) {
					List<String> rewriteFields = fieldsFromExpansion(field, columns);

					// replace this operator with an expanded version
					List<SqlBasicCall> newCalls = rewriteFields.stream().map(f -> {
						return new SqlBasicCall(operator, replaceInList(operandList, operandList.get(0), f),
								SqlParserPos.ZERO);
					}).collect(Collectors.toList());

					SqlCall newCall = SqlStdOperatorTable.OR.createCall(SqlParserPos.ZERO, newCalls);
					LOG.debug("Expanded {} to give {}", identifier.names, newCall);
					// insert this new call in place of the original operator
					return newCall;
				}
			}

			// binary operators
			if (operandList.size() == 2) {
				call.setOperand(0, call.operand(0).accept(this));
				call.setOperand(1, call.operand(1).accept(this));
				return call;
			}

			LOG.debug("Default for Visited: {}", call);
			return super.visit(call);
		}

		public SqlNode visit(SqlIdentifier id) {
			LOG.debug("Visited identified: {}", id.toString());
			return id;
		}

		public SqlNode visit(SqlLiteral literal) {
			LOG.debug("Visited literal: {}", literal.toString());
			return literal;
		}

		private SqlNode expandFunction(List<SqlNode> operandList, SqlOperator operator) {
			// ensure there is only one expansion in the list

			List<SqlNode> expansions = operandList.stream()
					.filter(f -> f.getKind() == SqlKind.IDENTIFIER
							&& ((SqlIdentifier) f).names.get(0).contains(EXPAND_PLACEHOLDER))
					.collect(Collectors.toList());

			if (expansions.size() > 1) {
				throw new IllegalArgumentException("Can only have one expansion operator in a funciton call");
			}
			if (expansions.size() < 1) {
				return new SqlBasicCall(operator, operandList.toArray(new SqlNode[operandList.size()]),
						SqlParserPos.ZERO);
			}

			// replace each operand that has expansion in it
			List<SqlBasicCall> newCalls = fieldsFromExpansion(((SqlIdentifier) expansions.get(0)).names.get(0), columns).stream().map(f -> {
				SqlNode[] newList = replaceInList(operandList, expansions.get(0), f);
				return new SqlBasicCall(operator, newList, SqlParserPos.ZERO);
			}).collect(Collectors.toList());

			return SqlStdOperatorTable.OR.createCall(SqlParserPos.ZERO, newCalls);
		}

		private SqlNode[] replaceInList(List<SqlNode> operandList, SqlNode o, String f) {
			List<SqlNode> list = operandList.stream()
					.map(orig -> (orig == o) ? new SqlIdentifier(f.toUpperCase(), SqlParserPos.ZERO) : orig)
					.collect(Collectors.toList());

			return list.toArray(new SqlNode[list.size()]);
		}
	}

	private static final String EXPAND_PLACEHOLDER = "__EXPAND__";

	public static String expand(String query, List<String> columns) throws SqlParseException {
		LOG.debug("Expanding query: {}", query);

		SqlNode node = SqlParser.create("select * from test WHERE " + query.replace("*", EXPAND_PLACEHOLDER))
				.parseQuery();
		final SqlBasicCall where = (SqlBasicCall) ((SqlSelect) node).getWhere();

		// Transform the where clause to expand any operation with an expand tag as a
		// new tree with ORs for each of the columns it represents

		SqlNode output = where.accept(new ExpansionVisitor(columns));

		if (output == null) {
			LOG.debug("No output, returning original where: {}", where.toSqlString(AnsiSqlDialect.DEFAULT).getSql());

			return where.toSqlString(AnsiSqlDialect.DEFAULT).getSql();
		}

		LOG.debug("Result query: {}", output.toSqlString(AnsiSqlDialect.DEFAULT).getSql());

		return "(" + output.toSqlString(AnsiSqlDialect.DEFAULT).getSql() + ")";
	}

	private static List<String> fieldsFromExpansion(String search, List<String> columns) {
		Pattern pattern = Pattern.compile("^" + search.replaceAll(EXPAND_PLACEHOLDER, ".*") + "$",
				Pattern.CASE_INSENSITIVE);
		List<String> rewriteFields = columns.stream().filter(pattern.asPredicate()).collect(Collectors.toList());
		if (rewriteFields.size() == 0) {
			// this expansion is for non-existent fields, remove this clause
			throw new IllegalArgumentException("Field expansion with no matching fields");
		}
		return rewriteFields;

	}
}
