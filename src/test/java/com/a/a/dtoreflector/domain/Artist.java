package com.a.a.dtoreflector.domain;

public class Artist {
	
	private String firstName;
	private String lastName;
	private String alias;
	private int age;
	
	public Artist() {}
	
	public Artist(String firstName, String lastName, String alias, int age) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.alias = alias;
		this.age = age;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastNamd) {
		this.lastName = lastNamd;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Artist [firstName=" + firstName + ", lastName=" + lastName + ", alias=" + alias + ", age=" + age + "]";
	}
	
	

}
