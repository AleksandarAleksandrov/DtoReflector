package com.a.a.dtoreflector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.a.a.dtoreflector.domain.Album;
import com.a.a.dtoreflector.domain.Artist;
import com.a.a.dtoreflector.domain.Producer;
import com.a.a.dtoreflector.domain.Song;
import com.a.a.dtoreflector.domain.User;
import com.a.a.dtoreflector.dto.AlbumDto;
import com.a.a.dtoreflector.dto.ArtistDto;
import com.a.a.dtoreflector.dto.ProducerDto;
import com.a.a.dtoreflector.dto.SongDto;
import com.a.a.dtoreflector.dto.UserDto;

public class TestTransferToDto {

	@Test
	public void testWithCreatedObject()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = new UserDto();
		DtoReflector.toDto(user, dto);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());
	}

	@Test
	public void testWithClazz() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = DtoReflector.toDto(user, UserDto.class);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());
	}

	@Test
	public void testDtoIgnoreAnnotation()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = DtoReflector.toDto(user, UserDto.class);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());
		assertNotEquals(user.getAge(), dto.getAge());
	}

	@Test
	public void testDtoIgnoreIfHasValueAnnotation()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Artist artist = new Artist("Michael", "Jackson", "MJ", 51);
		ArtistDto dto = DtoReflector.toDto(artist, ArtistDto.class);
		assertEquals(artist.getAlias(), dto.getDtoAlias());
		Artist artist2 = new Artist("Saul", "Hudson", "Slash", 51);
		ArtistDto dto2 = new ArtistDto();
		dto2.setDtoAlias("Super Alias");
		DtoReflector.toDto(artist2, dto2);
		assertNotEquals(artist2.getAlias(), dto2.getDtoAlias());
		assertEquals(artist2.getFirstName(), dto2.getFirstName());
		assertEquals(artist2.getLastName(), dto2.getLastName());
	}

	@Test
	public void testNestedDto() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Artist songArtist = new Artist("Michael", "Jackson", "MJ", 51);
		Song song = new Song();
		song.setTitle("Billie Jean");
		song.setYear(1982);
		song.setDuration(4.55);
		song.setArtist(songArtist);
		SongDto songDto = DtoReflector.toDto(song, SongDto.class);
		assertEquals(songDto.getArtistDto().getFirstName(), songArtist.getFirstName());
		assertEquals(songDto.getArtistDto().getLastName(), songArtist.getLastName());
		assertEquals(songDto.getArtistDto().getDtoAlias(), songArtist.getAlias());
	}

	@Test
	public void testDtoHavingComplexCollection()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Album album = new Album();
		album.setYear(2018);
		Artist albumArtist = new Artist("Ed", "Shereen", null, 28);
		album.setArtist(albumArtist);
		List<Song> albumSongs = new ArrayList<>();
		albumSongs.add(new Song("Shape of you", 4.55, albumArtist));
		albumSongs.add(new Song("The night we met", 3.23));
		albumSongs.add(new Song("Once in a lifetime", 3.47));
		albumSongs.add(new Song("Yesterday felt good", 3.03, albumArtist));
		album.setSongs(albumSongs);
		AlbumDto dto = DtoReflector.toDto(album, AlbumDto.class);
		int songMatchCounter = 0;
		for (Song song : albumSongs) {
			for (SongDto songDto : dto.getSongs()) {
				if (song.getTitle().equals(songDto.getTitle()) && song.getDuration() == songDto.getDuration()) {
					songMatchCounter++;
				}
			}
		}
		assertEquals(songMatchCounter, 4);
	}

	@Test
	public void testDtoHavingSimpleCollection()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Producer producer = new Producer();
		List<String> producerAliases = new ArrayList<>();
		producerAliases.add("alias1");
		producerAliases.add("alias2");
		producerAliases.add("alias3");
		producer.setAliases(producerAliases);
		producer.setCompanyId("123");
		producer.setCompanyName("Hersfields");
		producer.setFoundingYear(1999);
		ProducerDto dto = DtoReflector.toDto(producer, ProducerDto.class);
		List<String> dtoAliases = dto.getAliases();
		assertTrue(dtoAliases.contains("alias1"));
		assertTrue(dtoAliases.contains("alias2"));
		assertTrue(dtoAliases.contains("alias3"));
	}

	@Test
	public void testDtoHavinComplexMap()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Album album = new Album();
		Map<Song, Producer> songProducerMap = new HashMap<>();
		songProducerMap.put(new Song("Shape of you", 4.55), new Producer("IG Records", "123", 2013, null));
		songProducerMap.put(new Song("The night we met", 3.23), new Producer("KriskoBeats", "456", 2014, null));
		songProducerMap.put(new Song("Once in a lifetime", 3.47), new Producer("KriskoBeats", "456", 2014, null));
		songProducerMap.put(new Song("Yesterday felt good", 3.03), new Producer("IG Records", "123", 2013, null));
		album.setSongProducerMap(songProducerMap);
		// System.out.println(album);
		AlbumDto dto = DtoReflector.toDto(album, AlbumDto.class);
		// System.out.println(dto);
	}

	@Test
	public void testDtoHavingSimpleMap()
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Album album = new Album();
		Map<String, Integer> simpleMap = new HashMap<>();
		simpleMap.put("test1", 1);
		simpleMap.put("test2", 2);
		simpleMap.put("test3", 3);
		album.setSongTitleYearMap(simpleMap);
		// System.out.println(album);
		AlbumDto dto = DtoReflector.toDto(album, AlbumDto.class);
		// System.out.println(dto);
	}

	@Test
	public void testDtoHavingArray() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Song song1 = new Song("Shape of you", 4.55);
		Song song2 = new Song("The night we met", 3.23);
		Song song3 = new Song("Once in a lifetime", 3.47);
		Song song4 = new Song("Yesterday felt good", 3.03);
		User user = new User("Donald", "Trump", "dtrump@president.us", 22, "123");
		user.setLikedSongs(new Song[] { song1, song2, song3, song4 });
		//System.out.println(user);
		UserDto dto = DtoReflector.toDto(user, UserDto.class);
		//System.out.println(dto);
	}
	
	@Test
	public void testDtoHasBigInteger() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Song song = new Song("Shape of you", 4.55);
		song.setSoldRecordsCount("5456451065184561484010840116654980184");
		//System.out.println(song);
		SongDto dto = DtoReflector.toDto(song, SongDto.class);
		//System.out.println(dto);
	}
	
	@Test
	public void testDtoHasBigDecimal() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Song song = new Song("Shape of you", 4.55);
		song.setSoldRecordsValue("5456.156416581516541151189944555481548");
		//System.out.println(song);
		SongDto dto = DtoReflector.toDto(song, SongDto.class);
		//System.out.println(dto);
	}

}
