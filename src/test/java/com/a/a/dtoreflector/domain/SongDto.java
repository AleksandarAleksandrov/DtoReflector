package com.a.a.dtoreflector.domain;

import com.a.a.dtoreflector.annotation.DtoField;

public class SongDto {
	
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

	
}
