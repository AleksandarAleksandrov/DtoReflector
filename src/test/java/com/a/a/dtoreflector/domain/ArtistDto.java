package com.a.a.dtoreflector.domain;

import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoIgnoreIfHasValue;

public class ArtistDto {
	
	private String firstName;
	private String lastName;
	
	@DtoField(name="alias")
	@DtoIgnoreIfHasValue
	private String dtoAlias;

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

	public String getDtoAlias() {
		return dtoAlias;
	}

	public void setDtoAlias(String dtoAlias) {
		this.dtoAlias = dtoAlias;
	}

	@Override
	public String toString() {
		return "ArtistDto [firstName=" + firstName + ", lastName=" + lastName + ", dtoAlias=" + dtoAlias + "]";
	}
	
	

}
