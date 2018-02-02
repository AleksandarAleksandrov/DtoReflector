package com.a.a.dtoreflector.domain;

import java.util.Arrays;

public class User {
	
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	private String uuid;
	private Song[] likedSongs;
	
	public User(String firstName, String lastName, String email, int age, String uuid) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
		this.uuid = uuid;
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
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Song[] getLikedSongs() {
		return likedSongs;
	}
	public void setLikedSongs(Song[] likedSongs) {
		this.likedSongs = likedSongs;
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", age=" + age
				+ ", uuid=" + uuid + ", likedSongs=" + Arrays.toString(likedSongs) + "]";
	}

}
