package com.itranswarp.learnsql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SqlExecutor implements AutoCloseable {

	private HikariDataSource dataSource;

	public SqlExecutor(String url, String username, String password) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setAutoCommit(true);
		this.dataSource = new HikariDataSource(config);
	}

	public Connection openConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public List<List<Object>> select(String sql, Object... args) throws SQLException {
		try (Connection conn = openConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
				try (ResultSet rs = ps.executeQuery()) {
					List<List<Object>> list = new ArrayList<>();
					int columns = rs.getMetaData().getColumnCount();
					while (rs.next()) {
						List<Object> cols = new ArrayList<>();
						for (int i = 1; i <= columns; i++) {
							cols.add(rs.getObject(i));
						}
						list.add(cols);
					}
					return list;
				}
			}
		}
	}

	public <T> List<T> select(Class<T> clazz, String sql, Object... args) throws SQLException {
		try (Connection conn = openConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
				try (ResultSet rs = ps.executeQuery()) {
					List<T> list = new ArrayList<>();
					ResultSetMetaData meta = rs.getMetaData();
					List<String> columns = new ArrayList<>();
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						columns.add(meta.getColumnLabel(i));
					}
					while (rs.next()) {
						list.add(instanceOf(clazz, columns, rs));
					}
					return list;
				}
			}
		}
	}

	public int update(String sql, Object... args) throws SQLException {
		try (Connection conn = openConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
				return ps.executeUpdate();
			}
		}
	}

	public int delete(String sql, Object... args) throws SQLException {
		return update(sql, args);
	}

	public void close() {
		this.dataSource.close();
	}

	private <T> T instanceOf(Class<T> clazz, List<String> columns, ResultSet rs) {
		try {
			T bean = clazz.newInstance();
			int index = 0;
			for (String column : columns) {
				index++;
				Field f = clazz.getField(column);
				f.set(bean, rs.getObject(index, f.getType()));
			}
			return bean;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SQLException
				| NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
