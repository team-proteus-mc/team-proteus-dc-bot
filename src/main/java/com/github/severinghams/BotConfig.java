package com.github.severinghams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.github.severinghams.ConfigItem.Type;

import static com.github.severinghams.ConfigItem.Type.*;

public class BotConfig {
	
	private HashMap<String, ConfigItem> config;
	private static File cfg;
	private static final ArrayList<String> defaultConfig = new ArrayList<String>(Arrays.asList(
		new String[]
		{
			"token", 
			"rules-channel-id", 
			"rules-message-id",
			"ticket-category-id", 
			"verify-role-id", 
			"member-role-id",
			"mod-role-id",
			"admin-role-id",
			"owner-role-id",
		}
	));

	
	public BotConfig(File file) throws IOException, BotConfigException {
		cfg = new File(file, "config.txt");
		if (!cfg.exists()) {
			createConfig(cfg);
		}
		this.config = loadConfig(cfg);
		this.checkConfig();
	}
	
	public ConfigItem getConfig(String s) {
		if (this.config == null) return new ConfigItem("");
		ConfigItem ss = config.get(s);
		return ss != null ? ss : new ConfigItem("");
	}
	
	public void setMessageId(ConfigItem c) {
		this.config.remove("rules-message-id");
		this.config.put("rules-message-id", c);
		try {
			saveConfig(cfg, this.config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createConfig(File file) throws IOException {
		String[] resetcfg = {
				"# Do not add any lines extra here, they will be erased.",
				"# Do not change the format of this file, just paste in your",
				"# configs after the '>>'",
				"",
				"# DO NOT leave ANY inputs blank. Put a 0 to leave a config blank.",
				"",
				"# Discord Token",
				"token:String>>",
				"# Channel to place the verification message in.",
				"rules-channel-id:long>>",
				"# The message ID of the 'react to verify' message. ",
				"# LEAVE THIS value as 0 if you don't already have a message to use",
				"# for this.",
				"rules-message-id:long>>0",
				"# Category ID to place all user's ticket channels under.",
				"ticket-category-id:long>>",
				"# Role to give users to let mods know they aren't verified",
				"verify-role-id:long>>",
				"# Verified member role",
				"member-role-id:long>>",
				"# Moderator role",
				"mod-role-id:long>>",
				"# Admin role",
				"admin-role-id:long>>",
				"# Owner role",
				"owner-role-id:long>>"
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
	
	private static void saveConfig(File file, HashMap<String, ConfigItem> hm) throws IOException {
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
			pw.println("# Do not add any lines extra here, they will be erased.");
			pw.println("# Do not change the format of this file, just paste in your");
			pw.println("# configs after the '>>'");
			pw.println("");
			pw.println("# DO NOT leave ANY inputs blank. Put a 0 to leave a config blank.");
			pw.println("");
			pw.println("# Discord Token");
			pw.println("token:String>>"+hm.get("token").getString());
			pw.println("# Channel to place the verification message in.");
			pw.println("rules-channel-id:long>>"+hm.get("rules-channel-id").getString());
			pw.println("# The message ID of the 'react to verify' message. ");
			pw.println("# LEAVE THIS value as 0 if you don't already have a message to use");
			pw.println("# for this.");
			pw.println("rules-message-id:long>>"+hm.get("rules-message-id").getString());
			pw.println("# Category ID to place all user's ticket channels under.");
			pw.println("ticket-category-id:long>>"+hm.get("ticket-category-id").getString());
			pw.println("# Role to give users to let mods know they aren't verified");
			pw.println("verify-role-id:long>>"+hm.get("verify-role-id").getString());
			pw.println("# Verified member role");
			pw.println("member-role-id:long>>"+hm.get("member-role-id").getString());
			pw.println("# Moderator role");
			pw.println("mod-role-id:long>>"+hm.get("mod-role-id").getString());
			pw.println("# Admin role");
			pw.println("admin-role-id:long>>"+hm.get("admin-role-id").getString());
			pw.println("# Owner role");
			pw.println("owner-role-id:long>>"+hm.get("owner-role-id").getString());
			/*
			for (int i = 0; i < defaultConfig.size(); i++) {
				pw.println(defaultConfig.get(i)+":"+hm.get(defaultConfig.get(i)).getType().getString()+">>"+hm.get(defaultConfig.get(i)).getString());
			}
			*/
			pw.close();
		}
	} 
	
	private static HashMap<String, ConfigItem> loadConfig(File file) throws FileNotFoundException, BotConfigException {
		Scanner scan = new Scanner(file);
		HashMap<String, ConfigItem> returnMap = new HashMap<String, ConfigItem>();
		String[] a;
		String a2;
		int i = 0;
		while(scan.hasNextLine()) {
			i++;
			a2 = scan.nextLine();
			if (a2.length() < 1 || a2.startsWith("#")) {
				continue;
			}
			a = a2.split(":", -1);
			if (a.length != 2) {
				System.err.println("Skipping invalid line; broken config on line " + i + ": \"" + a2 + "\"");
				continue;
			}
			if (!defaultConfig.contains(a[0])) {
				System.err.println("Skipping invalid line; invalid key " + a[0] + " on line " + i + ": \"" + a2 + "\"");
				continue;
			}
			if (a[1].length() < 1) {
				System.err.println("Skipping invalid line; empty value on line " + i + ": \"" + a2 + "\"");
				continue;
			}
			String[] a3 = a[1].split(">>");
			if (a3.length != 2) {
				System.err.println("Skipping invalid line; broken config on line " + i + ": \"" + a2 + "\"");
				continue;
			}
			returnMap.put(a[0], new ConfigItem(a3[1], ConfigItem.Type.getEnum(a3[0])));
		}
		scan.close();
		return returnMap;
	}
	
	private void checkConfig() throws BotConfigException {
		ArrayList<String> als = new ArrayList<String>();
		{
			ArrayList<Map.Entry<String, ConfigItem>> alme = new ArrayList<Map.Entry<String, ConfigItem>>(this.config.entrySet());
			for (Map.Entry<String, ConfigItem> me : alme) {
				als.add(me.getKey());
			}
		}
		if (!als.containsAll(defaultConfig)) {
			ArrayList<String> als2 = new ArrayList<String>(defaultConfig);
			als2.removeAll(als);
			String string = "";
			for (int i = 0; i < als2.size(); i++) {
				string = string + ("\r\n    "+als2.get(i)+(i<als2.size()-1 ? "," : "\r\n"));
			}
			
			throw new BotConfigException("\r\nInvalid Bot Config! Missing/invalid config values: " + string + "Stacktrace:");
		}
	}
}
