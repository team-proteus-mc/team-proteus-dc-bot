package com.github.severinghams;

public class ConfigItem {
	private long lngValue;
	private int intValue;
	private String strValue;
	private Type t;
	
	public ConfigItem(String s) {
		this.t = Type.STR;
		strValue = s;
	}
	
	public ConfigItem(String s, Type t) {
		this.strValue = s;
		this.t = t;
		try {
			switch (t) {
				case INT:
					intValue = Integer.parseInt(s);
					break;
				case LNG:
					lngValue = Long.parseLong(s);
					break;
				default:
					break;
			}
		} catch (NumberFormatException e) {
		}
	}
	
	public int getInt() {
		if (this.t == Type.INT) {
			return intValue;
		}
		return 0;
	}
	
	public long getLong() {
		if (this.t == Type.LNG) {
			return lngValue;
		}
		return 0;
	}
	
	public String getString() {
		return strValue;
	}
	
	public Type getType() {
		return this.t;
	}
	
	public enum Type {
		INT,
		LNG,
		STR,
		BOOL,
		BYTE;
		
		public String getString() {
			switch (this) {
				case INT:
					return "int";
				case LNG:
					return "long";
				case BOOL:
					return "boolean";
				case BYTE:
					return "byte";
				default:
					return "String";
			}
		}
		
		public static Type getEnum(String s) {
			switch (s) {
				case "int":
					return INT;
				case "long":
					return LNG;
				case "boolean":
					return BOOL;
				case "byte":
					return BYTE;
				default:
					return STR;
			}
		}
	}
}	

