package com.a.a.dtoreflector.domain;

import java.util.List;
import java.util.Map;

public class Album {
	
	private Artist artist;
	private List<Song> songs;
	private int year;
	
	private Map<Song, Producer> songProducerMap;
	private Map<String, Integer> songTitleYearMap;
	
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	public List<Song> getSongs() {
		return songs;
	}
	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public Map<Song, Producer> getSongProducerMap() {
		return songProducerMap;
	}
	public void setSongProducerMap(Map<Song, Producer> songProducerMap) {
		this.songProducerMap = songProducerMap;
	}
	public Map<String, Integer> getSongTitleYearMap() {
		return songTitleYearMap;
	}
	public void setSongTitleYearMap(Map<String, Integer> songTitleYearMap) {
		this.songTitleYearMap = songTitleYearMap;
	}
	@Override
	public String toString() {
		return "Album [artist=" + artist + ", songs=" + songs + ", year=" + year + ", songProducerMap="
				+ songProducerMap + ", songTitleYearMap=" + songTitleYearMap + "]";
	}	

}
