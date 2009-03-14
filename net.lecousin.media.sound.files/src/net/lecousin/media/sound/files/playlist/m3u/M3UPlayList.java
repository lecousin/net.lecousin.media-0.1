package net.lecousin.media.sound.files.playlist.m3u;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.lecousin.framework.io.TextLineInputStream;
import net.lecousin.media.sound.files.playlist.PlayList;

public class M3UPlayList implements PlayList {

	public static M3UPlayList load(String name, InputStream stream) {
		TextLineInputStream in = new TextLineInputStream(stream);
		M3UPlayList list = new M3UPlayList(name);
		while (!in.isEndOfStream()) {
			String line;
			try { line = in.readLine(); }
			catch (IOException e) { break; }
			line = line.trim();
			if (line.length() == 0) continue;
			if (line.charAt(0) == '#') continue;
			list.list.add(line);
		}
		return list;
	}
	
	public M3UPlayList(String name) {
		this.name = name;
	}
	
	private String name;
	private List<String> list = new LinkedList<String>();
	
	public String getName() {
		return name;
	}
	public List<String> getPlayListFiles() {
		return list;
	}
}
