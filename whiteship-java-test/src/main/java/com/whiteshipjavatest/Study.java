package com.whiteshipjavatest;

public class Study {
	private final StudyStatus status = StudyStatus.DRAFT;
	private final int limit;
	private String name;

	public Study(int limit) {
		if (limit<0) {
			throw new IllegalArgumentException("limit은 0보다 커야한다.");
		}
		this.limit = limit;
	}

	public Study(Integer limit, String name) {
		this.limit=limit;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StudyStatus getStatus() {
		return status;
	}

	public int getLimit() {
		return limit;
	}
}
