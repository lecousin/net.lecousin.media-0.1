package net.lecousin.media.sound.files.audio.cda;

import java.util.List;

import net.lecousin.media.sound.files.audio.AudioFileInfo;
import net.lecousin.media.sound.files.audio.InvalidAudioFile;

public class CDAFormat extends AudioFileInfo {

	public CDAFormat(byte[] content) throws InvalidAudioFile {
		if (content.length != 44) throw new InvalidAudioFile("Invalid CDA content");
		length = ((long)(content[0x2A]&0xFF)*60+((long)content[0x29]&0xFF))*(long)1000;
		trackNumber = (content[0x16]&0xFF)+(((int)content[0x17]&0xFF)<<8);
	}
	
	private long length;
	private int trackNumber;
	
	@Override
	public int getTrackNumber() { return trackNumber; }
	@Override
	public long getDuration() { return length; }
	
	@Override
	public String getAlbum() { return null; }
	@Override
	public String getArtist() { return null; }
	@Override
	public byte[] getCDIdentifier() { return null; }
	@Override
	public String getComment() { return null; }
	@Override
	public String getGenre() { return null; }
	@Override
	public int getNumberOfTracksInAlbum() { return -1; }
	@Override
	public List<Picture> getPictures() { return null; }
	@Override
	public String getSongTitle() { return null; }
	@Override
	public int getYear() { return -1; }
}
