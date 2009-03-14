package net.lecousin.media.sound.files.audio.mp3;

import java.util.List;



public class ID3Format_v1 extends ID3Format {

	ID3Format_v1(byte[] buffer, int pos, int len) {
		StringBuilder str;
		
		str = new StringBuilder();
		for (int i = 0; i < 30 && buffer[pos+3+i] != 0; ++i)
			str.append((char)buffer[pos+3+i]);
		song_title = str.toString();

		str = new StringBuilder();
		for (int i = 0; i < 30 && buffer[pos+33+i] != 0; ++i)
			str.append((char)buffer[pos+33+i]);
		artist = str.toString();

		str = new StringBuilder();
		for (int i = 0; i < 30 && buffer[pos+63+i] != 0; ++i)
			str.append((char)buffer[pos+63+i]);
		album = str.toString();

		str = new StringBuilder();
		for (int i = 0; i < 4 && buffer[pos+93+i] != 0; ++i)
			str.append((char)buffer[pos+93+i]);
		String s = str.toString().trim();
		if (s.length() > 0)
			try { year = Integer.parseInt(s); }
			catch (NumberFormatException e) {}

		str = new StringBuilder();
		for (int i = 0; i < 30 && buffer[pos+97+i] != 0; ++i)
			str.append((char)buffer[pos+97+i]);
		comment = str.toString();
		
		genre = getGenre(buffer[pos+127]);
		
		if (buffer[pos+126] != 0 && buffer[pos+125] == 0)
			track_number = buffer[pos+126];
		else
			track_number = -1;
	}
	
	private String song_title;
	private String artist;
	private String album;
	private int year;
	private String comment;
	private int track_number;
	private String genre;
	
	@Override
	public String getSongTitle() { return song_title; }
	@Override
	public String getArtist() { return artist; }
	@Override
	public String getAlbum() { return album; }
	@Override
	public String getComment() { return comment; }
	@Override
	public String getGenre() { return genre; }
	@Override
	public int getTrackNumber() { return track_number; }
	@Override
	public int getYear() { return year; }
	
	public static String getGenre(byte value) {
		return "misc";
	}
	
	@Override
	public byte[] getCDIdentifier() { return null; }
	@Override
	public long getDuration() { return -1; }
	@Override
	public int getNumberOfTracksInAlbum() { return -1; }
	@Override
	public List<Picture> getPictures() { return null; }
}
