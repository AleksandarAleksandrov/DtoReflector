package com.a.a.dtoreflector.domain;

import java.util.List;

public class Song {

	private String title;
	private int year;
	private double duration;

	private String soldRecordsCount;

	private String soldRecordsValue;

	public Song() {
		super();
	}

	public Song(String title, double duration) {
		super();
		this.title = title;
		this.duration = duration;
	}

	public Song(String title, double duration, Artist artist) {
		super();
		this.title = title;
		this.duration = duration;
		this.artist = artist;
	}

	private Artist artist;
	private List<Producer> producers;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public List<Producer> getProducers() {
		return producers;
	}

	public void setProducers(List<Producer> producers) {
		this.producers = producers;
	}

	public String getSoldRecordsCount() {
		return soldRecordsCount;
	}

	public void setSoldRecordsCount(String soldRecordsCount) {
		this.soldRecordsCount = soldRecordsCount;
	}

	public String getSoldRecordsValue() {
		return soldRecordsValue;
	}

	public void setSoldRecordsValue(String soldRecordsValue) {
		this.soldRecordsValue = soldRecordsValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Song other = (Song) obj;
		if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Song [title=" + title + ", year=" + year + ", duration=" + duration + ", soldRecordsCount="
				+ soldRecordsCount + ", soldRecordsValue=" + soldRecordsValue + ", artist=" + artist + ", producers="
				+ producers + "]";
	}

}
