package com.github.severinghams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChannelToUserMap {
	
	private static File mapfile;
	
	private HashMap<Long,Long> usermap = new HashMap<Long,Long>();
	
	public ChannelToUserMap(File file) throws IOException {
		mapfile = new File(file, "channel-user-map.txt");
		if (!mapfile.exists()) {
			createConfig(mapfile);
		} else {
			this.usermap = loadConfig(mapfile);
		}
	}
	
	private static void createConfig(File file) throws IOException {
		String[] resetcfg = {
				"# /!\\ DO NOT EDIT /!\\ #",
				"# This is the file containing all users currently in the verification system.",
				"# /!\\ DO NOT EDIT /!\\ #"
		};
		file.getParentFile().mkdirs();
		if (file.getParentFile().exists() && file.getParentFile().isDirectory()) {
			file.createNewFile();
		}	
		if (file.exists() && !file.isFile()) {
			file.delete();
			file.createNewFile();
		}	
		if (file.exists() && file.isFile()) {
			PrintWriter pw = new PrintWriter(file);
			for (int i = 0; i < resetcfg.length; i++) {
				pw.println(resetcfg[i]);
			}
			pw.close();
		}
	}
	
	public long getUserFromChannel(long l) {
		return this.usermap.get(l);
	}
	
	public boolean containsChannel(long l) {
		return this.usermap.containsKey(l);
	}
	
	public void addChannelMap(long channel, long user) {
		if (!this.usermap.containsKey(channel)) {
			this.usermap.put(channel, user);
			try {
				saveConfig(mapfile, usermap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeChannelMap(long channel) {
		if (this.usermap.containsKey(channel)) {
			this.usermap.remove(channel);
			try {
				saveConfig(mapfile, usermap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void saveConfig(File file, HashMap<Long, Long> hm) throws IOException {
		String[] resetcfg = {
				"# /!\\ DO NOT EDIT /!\\ #",
				"# This is the file containing all users currently in the verification system.",
				"# /!\\ DO NOT EDIT /!\\ #"
		};
		file.getParentFile().mkdirs();
		if (file.getParentFile().exists() && file.getParentFile().isDirectory()) {
			file.createNewFile();
		}	
		if (file.exists() && !file.isFile()) {
			file.delete();
			file.createNewFile();
		}	
		if (file.exists() && file.isFile()) {
			PrintWriter pw = new PrintWriter(file);
			ArrayList<Map.Entry<Long,Long>> alme = new ArrayList<Map.Entry<Long,Long>>(hm.entrySet());
			for (int i = 0; i < resetcfg.length; i++) {
				pw.println(resetcfg[i]);
			}
			for (int i = 0; i < alme.size(); i++) {
				pw.println(alme.get(i).getKey().toString()+":"+alme.get(i).getValue().toString());
			}
			pw.close();
		}
	}
	
	private static HashMap<Long, Long> loadConfig(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		HashMap<Long, Long> returnMap = new HashMap<Long, Long>();
		String[] a;
		String a2;
		while(scan.hasNextLine()) {
			a2 = scan.nextLine();
			if (a2.startsWith("#")) {
				continue;
			}
			a = a2.split(":");
			returnMap.put(Long.parseLong(a[0]), Long.parseLong(a[1]));
		}
		scan.close();
		return returnMap;
	}
}
