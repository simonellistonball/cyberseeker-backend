package com.cloudera.cyber.query.hive;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudera.cyber.ColumnService;
import com.cloudera.cyber.ColumnType;
import com.cloudera.cyber.QuerySpec;
import com.cloudera.cyber.query.QueryService;
import com.cloudera.cyber.query.domain.ColumnDTO;
import com.cloudera.cyber.query.domain.ResultSet;
import com.cloudera.cyber.query.domain.ResultSet.ResultSetBuilder;
import com.cloudera.cyber.query.domain.Row;

import reactor.core.publisher.Mono;

@Service
public class HiveService implements QueryService, ColumnService {

	private BasicDataSource connectionPool;

	@Value("hive.url")
	private String databaseUrl;

	@Value("hive.viewname")
	private String viewName;

	@Autowired
	private ColumnService columnService;

	private boolean initialized = false;

	private void init() throws URISyntaxException {
		URI dbUri = new URI(databaseUrl);
		String dbUrl = "jdbc:hive2://" + dbUri.getHost() + dbUri.getPath();
		connectionPool = new BasicDataSource();

		if (dbUri.getUserInfo() != null) {
			connectionPool.setUsername(dbUri.getUserInfo().split(":")[0]);
			connectionPool.setPassword(dbUri.getUserInfo().split(":")[1]);
		}
		connectionPool.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
		connectionPool.setUrl(dbUrl);
		connectionPool.setInitialSize(1);
		connectionPool.setMaxTotal(10);

		this.initialized = true;
	}

	public Mono<ResultSet> query(QuerySpec query) {
		try {
			String sql = new SimpleHQL(query, viewName, columnService).sql();

			ResultSetBuilder builder = ResultSet.builder().query(sql);
			if (!this.initialized)
				this.init();

			Connection connection = connectionPool.getConnection();
			Statement stmt = connection.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery(sql);

			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			while (rs.next()) {
				Row row = new Row();
				for (int i = 1; i <= columns; i++) {
					row.put(md.getColumnName(i), rs.getString(i));
				}
				builder.row(row);
			}

			connection.close();

			return Mono.just(builder.build());
		} catch (SQLException e) {
			return Mono.error(e);
		} catch (URISyntaxException e) {
			return Mono.error(e);
		} catch (SqlParseException e) {
			return Mono.error(new IllegalArgumentException("Invalid SQL in query", e));
		}
	}

	@Override
	public List<ColumnDTO> getAllColumns() {
		try {
			if (!this.initialized)
				this.init();

			List<ColumnDTO> results = new ArrayList<ColumnDTO>();
			String sql = "SELECT * FROM `information_schema`.`columns` WHERE `table_name` = ?";
			Connection connection = connectionPool.getConnection();

			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(0, viewName);
			java.sql.ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				results.add(ColumnDTO.builder().name(executeQuery.getString("column_name"))
						.label(executeQuery.getString("column_name")).type(ColumnType.STRING).build());
			}
			stmt.close();
			connection.close();

			return results;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
