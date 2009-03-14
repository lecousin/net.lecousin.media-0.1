package net.lecousin.media.sound.files.audio.mp3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import net.lecousin.framework.io.LCBufferedInputStream;
import net.lecousin.media.sound.files.audio.AudioFile;
import net.lecousin.media.sound.files.audio.AudioFileInfo;
import net.lecousin.media.sound.files.audio.InvalidAudioFile;

import org.eclipse.core.filesystem.IFileStore;

public class MP3File extends AudioFile {

	public MP3File(String uri) throws InvalidAudioFile {
		super(uri);
	}
	public MP3File(URI uri) throws InvalidAudioFile {
		super(uri);
	}
	public MP3File(File file) throws InvalidAudioFile {
		super(file);
	}
	public MP3File(URL url) throws InvalidAudioFile {
		super(url);
	}
	public MP3File(IFileStore file) throws InvalidAudioFile {
		super(file);
	}
	
	@Override
	protected AudioFileInfo loadInfo(InputStream stream) throws InvalidAudioFile {
		byte[] buf = new byte[2];
		try {
			if (stream.read(buf) != 2) throw new InvalidAudioFile("Not a valid MP3 file");
		} catch (IOException e) { throw new InvalidAudioFile("Not a valid MP3 file"); }
		LCBufferedInputStream in = new LCBufferedInputStream(buf, 0, 2, stream);
		try { return ID3Format.load(in); }
		catch (IOException e) {}
		if (buf[0] != 0xFF || buf[1] != 0xFB) throw new InvalidAudioFile("Not a valid MP3 file");
		return null;
	}
}
