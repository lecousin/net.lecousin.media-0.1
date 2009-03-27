package net.lecousin.media.jsound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import net.lecousin.framework.collections.CollectionUtil;
import net.lecousin.framework.files.TypedFile;
import net.lecousin.framework.files.TypedFileDetector;
import net.lecousin.framework.files.audio.AudioFile;
import net.lecousin.framework.files.audio.AudioFileInfo;
import net.lecousin.framework.io.LCPartialBufferedInputStream;
import net.lecousin.framework.io.LCPartialBufferedInputStream.StreamProvider;
import net.lecousin.framework.log.Log;
import net.lecousin.framework.media.Media;
import net.lecousin.media.jsound.internal.DecoderThread;
import net.lecousin.media.jsound.internal.PlayerThread;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;

public class JavaSoundMedia implements Media {

	JavaSoundMedia(URI uri) {
		this.uri = uri;
		fileStream = new LCPartialBufferedInputStream(new StreamProvider() {
			public InputStream open() {
				try { return EFS.getStore(JavaSoundMedia.this.uri).openInputStream(EFS.NONE, null); }
				catch (CoreException e) {
					if (Log.error(this))
						Log.error(this, "Unable to get JavaSoundMedia stream.", e);
					return null; 
				}
			}
		});
		try {
			file = TypedFileDetector.detect(uri, fileStream, CollectionUtil.single_element_list(AudioFile.FILE_TYPE));
		} catch (IOException e) {
			if (Log.error(this))
				Log.error(this, "Unable to determine JavaSoundMedia type", e);
			file = null;
		} catch (CoreException e) {
			if (Log.error(this))
				Log.error(this, "Unable to determine JavaSoundMedia type", e);
			file = null;
		}
	}
	
	private URI uri;
	private LCPartialBufferedInputStream fileStream;
	private TypedFile file;
	private DecoderThread decoder = null;
	private PlayerThread player = null;
	private boolean paused = false;
	
	public URI getURI() { return uri; } 
	
	public long getDuration() {
		AudioFileInfo info = (AudioFileInfo)file.getInfo();
		if (info != null && info.getDuration() >= 0)
			return info.getDuration();
		return 0;
	}
	
	void close() {
		stop();
		try { fileStream.close(); } catch (IOException e) {}
	}
	
	void start(JavaSoundMediaPlayerPlugin plugin) {
		if (player != null && paused) {
			pause();
			return;
		}
		paused = false;
		AudioDecoder dec = AudioDecoderPlugins.get(file);
		dec.init(fileStream);
		if (dec == null) return;
		decoder = new DecoderThread(dec);
		decoder.start();
		player = new PlayerThread(decoder, this, plugin);
		player.start();
	}
	void pause() {
		if (player != null)
			paused = player.pausePlayer();
	}
	void stop() {
		paused = false;
		if (decoder != null)
			decoder.stopDecoder();
		decoder = null;
		if (player != null)
			player.stopPlayer();
		player = null;
	}
	
	long getTime() {
		if (player == null)
			return 0;
		return player.getTime();
	}
	double getPosition() {
		if (player == null)
			return 0;
		long dur = getDuration();
		if (dur == 0) return 0;
		long time = player.getTime();
		return ((double)time)/((double)dur);
	}
	void setTime(long time) {
		if (player == null) return;
		player.setTime(time);
	}
	void setPosition(double pos) {
		if (player == null) return;
		long dur = getDuration();
		if (dur == 0) return;
		player.setTime((long)(dur*pos));
	}
}
