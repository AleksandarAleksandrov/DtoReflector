package com.a.a.dtoreflector.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.a.a.dtoreflector.annotation.DtoField;
import com.a.a.dtoreflector.annotation.DtoBigDecimalFromStr;
import com.a.a.dtoreflector.annotation.DtoBigIntegerFromStr;

public class SongDto implements Comparable<SongDto>{
	
	private String title;
	private double duration;
	
	@DtoField(name="artist")
	private ArtistDto artistDto;
	
	@DtoBigIntegerFromStr
	private BigInteger soldRecordsCount;
	
	@DtoBigDecimalFromStr
	private BigDecimal soldRecordsValue;

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
	public BigInteger getSoldRecordsCount() {
		return soldRecordsCount;
	}
	public void setSoldRecordsCount(BigInteger soldRecordsCount) {
		this.soldRecordsCount = soldRecordsCount;
	}
	public BigDecimal getSoldRecordsValue() {
		return soldRecordsValue;
	}
	public void setSoldRecordsValue(BigDecimal soldRecordsValue) {
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
		return "SongDto [title=" + title + ", duration=" + duration + ", artistDto=" + artistDto + ", soldRecordsCount="
				+ soldRecordsCount + ", soldRecordsValue=" + soldRecordsValue + "]";
	}

	@Override
	public int compareTo(SongDto o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
