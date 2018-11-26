package com.itranswarp.learnsql;

import java.sql.SQLException;
import java.util.List;

public class LearnSql {

	public static void main(String[] args) throws SQLException {
		try (SqlExecutor executor = new SqlExecutor("jdbc:mysql://localhost:3306/test", "root", "password")) {
			// raw query:
			List<List<Object>> results = executor.select("SELECT * FROM students WHERE score >= ?", 85);
			results.forEach(row -> {
				System.out.println(String.join(", ", row.stream().map(String::valueOf).toArray(String[]::new)));
			});
			// update:
			executor.update("UPDATE students SET score = 99 WHERE id = ?", 1);
			// delete:
			executor.delete("DELETE FROM students WHERE class_id = ?", 2);
			// query as object:
			List<Student> students = executor.select(Student.class,
					"SELECT id, class_id classId, name, gender, score FROM students");
			students.forEach(System.out::println);
		}
	}
}
