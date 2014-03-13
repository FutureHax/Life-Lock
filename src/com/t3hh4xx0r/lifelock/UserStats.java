package com.t3hh4xx0r.lifelock;

import com.parse.ParseObject;

public class UserStats {
	static int MALE = 0;
	static int FEMALE = 1;
	int UNKNOWN = -1;
	private int sex = UNKNOWN;
	private int age = UNKNOWN;
	private String country = "UNKNOWN";

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "UserStats [sex=" + (sex == MALE ? "Male" : "Female") + ", age="
				+ age + ", country=" + country + "]";
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void saveInBackground() {
		ParseObject o = new ParseObject("UserStats");
		o.put("age", age);
		o.put("sex", sex == MALE ? "Male" : "Female");
		o.put("country", country);
		o.saveInBackground();
	}
}
