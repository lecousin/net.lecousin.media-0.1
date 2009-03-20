package net.lecousin.media.sound.files.audio.cda;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import net.lecousin.media.sound.files.audio.AudioFile;
import net.lecousin.media.sound.files.audio.AudioFileInfo;
import net.lecousin.media.sound.files.audio.InvalidAudioFile;

import org.eclipse.core.filesystem.IFileStore;

public class CDAFile extends AudioFile {

	public CDAFile(String uri) throws InvalidAudioFile {
		super(uri);
	}
	public CDAFile(URI uri) throws InvalidAudioFile {
		super(uri);
	}
	public CDAFile(File file) throws InvalidAudioFile {
		super(file);
	}
	public CDAFile(URL url) throws InvalidAudioFile {
		super(url);
	}
	public CDAFile(IFileStore file) throws InvalidAudioFile {
		super(file);
	}
	
	@Override
	protected AudioFileInfo loadInfo(InputStream stream) throws InvalidAudioFile {
		byte[] buf = new byte[44];
		try {
			if (stream.read(buf) != 44) throw new InvalidAudioFile("Not a valid CDA file");
		} catch (IOException e) { throw new InvalidAudioFile("Not a valid CDA file"); }
		return new CDAFormat(buf);
	}
}
