package com.cloudera.cyber.query;

import static com.cloudera.cyber.query.QueryExpansion.expand;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class TestQueryExpansion {

	@Test
	public void testExpanderHelper() {
		List<String> columns = Stream.concat(makeColumns(10).stream(), makeColumns("not_these", 2).stream())
				.collect(Collectors.toList());

		List<String> invokeMethod = ReflectionTestUtils.invokeMethod(QueryExpansion.class, "fieldsFromExpansion",
				"test" + ReflectionTestUtils.getField(QueryExpansion.class, "EXPAND_PLACEHOLDER"), columns);

		assertThat(columns, hasSize(12));
		assertThat(invokeMethod, hasSize(10));
		assertThat(invokeMethod, hasItem("test_column_0"));
	}

	@Test
	public void testNoExpansion() throws SqlParseException {
		List<String> columns = makeColumns(10);
		assertThat(expand("test_column_0='test'", columns), is("(`TEST_COLUMN_0` = 'test')"));
	}

	@Test
	public void testSingleSimpleExpansion() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("test_*='test'", columns), is("(`TEST_COLUMN_0` = 'test' OR `TEST_COLUMN_1` = 'test')"));
	}

	@Test
	public void testZeroColumnMatch() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThrows(IllegalArgumentException.class, () -> expand("none_* = 'test'", columns));
	}

	@Test
	public void testExpandAndNormal() throws SqlParseException {
		List<String> columns = Stream.concat(makeColumns(2).stream(), makeColumns("prefix", 2).stream())
				.collect(Collectors.toList());

		assertThat(expand("test_*='test' AND prefix_0='test'", columns),
				is("((`TEST_COLUMN_0` = 'test' OR `TEST_COLUMN_1` = 'test') AND `PREFIX_0` = 'test')"));
	}

	@Test
	void testExpansionInNest() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("prefix_0='test' AND (prefix_1='test' OR test_*='test')", columns), is(
				"(`PREFIX_0` = 'test' AND (`PREFIX_1` = 'test' OR (`TEST_COLUMN_0` = 'test' OR `TEST_COLUMN_1` = 'test')))"));
	}

	@Test
	void testWithAFunctionOnField() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("UDF_FUNC(test_*)", columns),
				is("(`UDF_FUNC`(`TEST_COLUMN_0`) OR `UDF_FUNC`(`TEST_COLUMN_1`))"));
	}

	@Test
	void testWithAFunctionOnFieldWithComparison() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("UDF_FUNC(test_*)='test'", columns),
				is("(`UDF_FUNC`(`TEST_COLUMN_0`)='test' OR `UDF_FUNC`(`TEST_COLUMN_1`)='test')"));
	}

	@Test
	void testWithAFunctionOnFieldPlusExansions() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("UDF_FUNC(test) AND UDF2_FUNC(test_*)", columns),
				is("(`UDF_FUNC`(`TEST`) AND (`UDF2_FUNC`(`TEST_COLUMN_0`) OR `UDF2_FUNC`(`TEST_COLUMN_1`)))"));
	}

	@Test
	void testFunctionWithMultipleExpansionArguments() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThrows(IllegalArgumentException.class, () -> expand("UDF_FUNC(test*, test*)", columns));
	}

	@Test
	void testFunctionWithExpansionInSecondPlace() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("UDF_FUNC(test, test*)", columns),
				is("(`UDF_FUNC`(`TEST`, `TEST_COLUMN_0`) OR `UDF_FUNC`(`TEST`, `TEST_COLUMN_1`))"));
	}

	@Test
	void testExpansionWithLike() throws SqlParseException {
		List<String> columns = makeColumns(2);
		assertThat(expand("test* LIKE 'test%'", columns),
				is("(`TEST_COLUMN_0` LIKE 'test%' OR `TEST_COLUMN_1` LIKE 'test%')"));
	}

	private List<String> makeColumns(String prefix, int i) {
		return IntStream.range(0, i).mapToObj(n -> String.format("%s_%d", prefix, n)).collect(Collectors.toList());
	}

	private List<String> makeColumns(Integer i) {
		return makeColumns("test_column", i);
	}
}
