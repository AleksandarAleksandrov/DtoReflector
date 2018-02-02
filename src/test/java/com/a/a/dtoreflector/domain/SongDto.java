package com.a.a.dtoreflector.domain;

import com.a.a.dtoreflector.annotation.DtoField;

public class SongDto implements Comparable<SongDto>{
	
	private String title;
	private double duration;
	
	@DtoField(name="artist")
	private ArtistDto artistDto;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public ArtistDto getArtistDto() {
		return artistDto;
	}

	public void setArtistDto(ArtistDto artistDto) {
		this.artistDto = artistDto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		SongDto other = (SongDto) obj;
		if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SongDto [title=" + title + ", duration=" + duration + ", artistDto=" + artistDto + "]";
	}

	@Override
	public int compareTo(SongDto o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
