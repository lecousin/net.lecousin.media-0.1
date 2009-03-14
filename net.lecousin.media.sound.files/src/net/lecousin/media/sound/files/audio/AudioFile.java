package net.lecousin.media.sound.files.audio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

public abstract class AudioFile {

	public AudioFile(String uri) throws InvalidAudioFile {
		this.uri = uri;
		InputStream stream = getStream(uri);
		if (stream != null) {
			this.info = loadInfo(stream);
			try { stream.close(); } catch (IOException e) {}
		}
	}
	public AudioFile(URI uri) throws InvalidAudioFile {
		this(uri.toString());
	}
	public AudioFile(URL url) throws InvalidAudioFile {
		this(getURI(url));
	}
	protected static String getURI(URL url) {
		try { return url.toURI().toString(); }
		catch (URISyntaxException e) {
			return url.toString();
		}
	}
	public AudioFile(File file) throws InvalidAudioFile {
		this(file.toURI());
	}
	public AudioFile(IFileStore file) throws InvalidAudioFile {
		this(file.toURI());
	}
	
	protected static InputStream getStream(String uri) {
		try { 
			IFileStore store = EFS.getStore(new URI(uri));
			return store.openInputStream(EFS.NONE, null);
		} catch (Throwable t) { return null; }
	}
	protected abstract AudioFileInfo loadInfo(InputStream stream) throws InvalidAudioFile;
	
	private String uri;
	private AudioFileInfo info;
	
	public String getURI() { return uri; }
	public AudioFileInfo getInfo() { return info; }
	
}
