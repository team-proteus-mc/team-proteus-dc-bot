package com.teamproteus.discordbot;

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
	private HashMap<Long,Long> chanmap = new HashMap<Long,Long>();
	
	public ChannelToUserMap(File file) throws IOException {
		mapfile = new File(file, "channel-user-map.txt");
		if (!mapfile.exists()) {
			createConfig(mapfile);
		} else {
			HashMap<Long, Long>[] returnMap = loadConfig(mapfile);
			this.usermap = returnMap[0];
			this.chanmap = returnMap[1];
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
	
	public long getChannelFromUser(long l) {
		return this.chanmap.get(l);
	}
	
	public boolean containsChannel(long l) {
		return this.usermap.containsKey(l);
	}
	
	public boolean containsUser(long l) {
		return this.chanmap.containsKey(l);
	}
	
	public void addChannelMap(long channel, long user) {
		if (!this.usermap.containsKey(channel)) {
			this.usermap.put(channel, user);
			this.chanmap.put(user, channel);
			try {
				saveConfig(mapfile, usermap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeChannelMap(long channel) {
		if (this.usermap.containsKey(channel)) {
			this.chanmap.remove(this.usermap.get(channel));
			this.usermap.remove(channel);
			try {
				saveConfig(mapfile, usermap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeChannelMapWithUser(long user) {
		if (this.chanmap.containsKey(user)) {
			this.usermap.remove(this.chanmap.get(user));
			this.chanmap.remove(user);
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
	
	@SuppressWarnings("unchecked")
	private static HashMap<Long, Long>[] loadConfig(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		HashMap<Long, Long>[] returnMap = new HashMap[2]; 
		returnMap[0] = new HashMap<Long, Long>(); 
		returnMap[1] = new HashMap<Long, Long>();
		String[] a;
		String a2;
		while(scan.hasNextLine()) {
			a2 = scan.nextLine();
			if (a2.startsWith("#")) {
				continue;
			}
			a = a2.split(":");
			returnMap[0].put(Long.parseLong(a[0]), Long.parseLong(a[1]));
			returnMap[1].put(Long.parseLong(a[1]), Long.parseLong(a[0]));			
		}
		scan.close();
		return returnMap;
	}
}
