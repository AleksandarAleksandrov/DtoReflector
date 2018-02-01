package com.a.a.dtoreflector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.a.a.dtoreflector.domain.Artist;
import com.a.a.dtoreflector.domain.ArtistDto;
import com.a.a.dtoreflector.domain.Song;
import com.a.a.dtoreflector.domain.SongDto;
import com.a.a.dtoreflector.domain.User;
import com.a.a.dtoreflector.domain.UserDto;

public class TestTransferToDto {
	
	@Test
	public void testWithCreatedObject() {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = new UserDto();
		DtoReflector.transferToDto(user, dto);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());		
	}
	
	@Test
	public void testWithClazz() {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = DtoReflector.transferToDto(user, UserDto.class);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());
	}
	
	@Test
	public void testDtoIgnoreAnnotation() {
		User user = new User("John", "Doe", "john@doe.com", 33, "abcd-efg-123-hij-456");
		UserDto dto = DtoReflector.transferToDto(user, UserDto.class);
		assertEquals(user.getFirstName(), dto.getFirstName());
		assertEquals(user.getLastName(), dto.getLastName());
		assertEquals(user.getUuid(), dto.getUniqueIdentifier());
		assertNotEquals(user.getAge(), dto.getAge());
	}
	
	@Test
	public void testDtoIgnoreIfHasValueAnnotation() {
		Artist artist = new Artist("Michael", "Jackson", "MJ", 51);
		ArtistDto dto = DtoReflector.transferToDto(artist, ArtistDto.class);
		assertEquals(artist.getAlias(), dto.getDtoAlias());
		Artist artist2 = new Artist("Saul", "Hudson", "Slash", 51);
		ArtistDto dto2 = new ArtistDto();
		dto2.setDtoAlias("Super Alias");
		DtoReflector.transferToDto(artist2, dto2);
		assertNotEquals(artist2.getAlias(), dto2.getDtoAlias());
		assertEquals(artist2.getFirstName(), dto2.getFirstName());
		assertEquals(artist2.getLastName(), dto2.getLastName());
	}
	
	@Test
	public void testNestedDto() {
		Artist songArtist = new Artist("Michael", "Jackson", "MJ", 51);
		Song song = new Song();
		song.setTitle("Billie Jean");
		song.setYear(1982);
		song.setDuration(4.55);
		song.setArtist(songArtist);
		SongDto songDto = DtoReflector.transferToDto(song, SongDto.class);
		assertEquals(songDto.getArtistDto().getFirstName(), songArtist.getFirstName());
		assertEquals(songDto.getArtistDto().getLastName(), songArtist.getLastName());
		assertEquals(songDto.getArtistDto().getDtoAlias(), songArtist.getAlias());
	}

}
