package com.a.a.dtoreflector.dto;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.a.a.dtoreflector.annotation.DtoField;

public class AlbumDto {
	
	private ArtistDto artist;
	private List<SongDto> songs;
	@DtoField(name="songs")
	private Set<SongDto> songsSet;
	@DtoField(name="songs")
	private Queue<SongDto> songsQueue;
	
	private Map<SongDto, ProducerDto> songProducerMap;
	@DtoField(name="songTitleYearMap")
	private Map<String, Integer> songTitleToYear;
	
	public ArtistDto getArtist() {
		return artist;
	}
	public void setArtist(ArtistDto artist) {
		this.artist = artist;
	}
	public List<SongDto> getSongs() {
		return songs;
	}
	public void setSongs(List<SongDto> songs) {
		this.songs = songs;
	}
	public Set<SongDto> getSongsSet() {
		return songsSet;
	}
	public void setSongsSet(Set<SongDto> songsSet) {
		this.songsSet = songsSet;
	}
	public Queue<SongDto> getSongsQueue() {
		return songsQueue;
	}
	public void setSongsQueue(Queue<SongDto> songsQueue) {
		this.songsQueue = songsQueue;
	}
	public Map<SongDto, ProducerDto> getSongProducerMap() {
		return songProducerMap;
	}
	public void setSongProducerMap(Map<SongDto, ProducerDto> songProducerMap) {
		this.songProducerMap = songProducerMap;
	}
	public Map<String, Integer> getSongTitleToYear() {
		return songTitleToYear;
	}
	public void setSongTitleToYear(Map<String, Integer> songTitleToYear) {
		this.songTitleToYear = songTitleToYear;
	}
	@Override
	public String toString() {
		return "AlbumDto [artist=" + artist + ", songs=" + songs + ", songsSet=" + songsSet + ", songsQueue="
				+ songsQueue + ", songProducerMap=" + songProducerMap + ", songTitleToYear=" + songTitleToYear + "]";
	}	

}
