package com.github.severinghams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;

public class ChannelArchiver {

	private File dir;
	
	public ChannelArchiver(File file) {
		this.dir = new File(file, "archives");
		if (!this.dir.exists()) {
			this.dir.mkdir();
		}
	}
	
	public void archiveChannel(List<Message> mh, String name) {
		File file = new File(new File(dir,name), ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		for (int i = mh.size() - 1; i >= 0; i--) {
			pw.println("<"+mh.get(i).getAuthor().getName()+"> "+mh.get(i).getContentDisplay());
			System.out.println("<"+mh.get(i).getAuthor().getName()+"> "+mh.get(i).getContentDisplay());
		}
		pw.close();
	}
}
