package com.a.a.dtoreflector.domain;

import java.util.List;
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
	@Override
	public String toString() {
		return "AlbumDto [artist=" + artist + ", songs=" + songs + ", songsSet=" + songsSet + ", songsQueue="
				+ songsQueue + "]";
	}
	
	

}
