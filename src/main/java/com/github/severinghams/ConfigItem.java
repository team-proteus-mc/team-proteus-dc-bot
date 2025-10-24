package com.github.severinghams;

public class ConfigItem {
	private Number value;
	private String stringVal;
	
	public ConfigItem(String s, Type t) throws BotConfigException {
		this.stringVal = s;
		try {
			switch (t) {
				case INT:
					value = new Integer(Integer.parseInt(s));
				case LNG:
				default:
					break;
			}
		} catch (NumberFormatException e) {
			throw new BotConfigException("Config format error!\r\n"
					+ "     \""+s+"\" is not a valid " + (t == Type.LNG ? "Long" : "Integer" )+"\r\n"+
					"\r\nStacktrace");
		}
	}
	
	public enum Type {
		INT,
		LNG,
		STR
	}
}	

