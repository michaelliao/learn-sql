package com.itranswarp.learnsql;

public class Student {

	public long id;
	public long classId;
	public String name;
	public String gender;
	public double score;

	@Override
	public String toString() {
		return String.format("{Student: id=%s, classId=%s, name=%s, gender=%s, score=%s}", id, classId, name, gender,
				score);
	}
}
