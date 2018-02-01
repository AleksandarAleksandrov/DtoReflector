package com.a.a.dtoreflector.domain;

import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoIgnore;

public class UserDto {
	
	private String firstName;
	private String lastName;
	
	@DtoField(name="uuid")
	private String uniqueIdentifier;
	
	@DtoIgnore
	public int age;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}	

}
