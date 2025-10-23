package com.github.severinghams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BotConfig {
	
	private HashMap<String, String> config;
	private static File cfg;
	private static final ArrayList<String> defaultConfig = new ArrayList<String>(Arrays.asList(
		new String[]
		{
			"token", 
			"verify-button-channel-id", 
			"ticket-category-id", 
			"verify-role-id", 
			"mod-role-id"
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
	
	public String getConfig(String s) {
		if (this.config == null) return "";
		String ss = config.get(s);
		return ss != null ? ss : "";
	}
	
	private static void createConfig(File file) throws IOException {
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
			for (int i = 0; i < defaultConfig.size(); i++) {
				pw.println(defaultConfig.get(i)+":");
			}
			pw.close();
		}
	}
	
	private static HashMap<String, String> loadConfig(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		HashMap<String, String> returnMap = new HashMap<String, String>();
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
			if (a.length < 2 || a.length > 2) {
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
			returnMap.put(a[0], a[1]);
		}
		scan.close();
		return returnMap;
	}
	
	private void checkConfig() throws BotConfigException {
		ArrayList<String> als = new ArrayList<String>();
		{
			ArrayList<Map.Entry<String, String>> alme = new ArrayList<Map.Entry<String, String>>(this.config.entrySet());
			for (Map.Entry<String, String> me : alme) {
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
