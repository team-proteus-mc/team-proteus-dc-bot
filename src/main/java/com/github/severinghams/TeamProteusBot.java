package com.github.severinghams;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;

import java.io.File;
import java.io.IOException;


@SuppressWarnings("unused")
public class TeamProteusBot {
	
	public static TeamProteusBot instance;
	
	private static File dir = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "team-proteus-dc-bot");
	
	public BotConfig config;
	
	public TeamProteusBot() {
		
	}
	
	public static void main(String... args) {
		instance = new TeamProteusBot();
		try {
			instance.main0(args);
		} catch (IOException | BotConfigException e) {
			e.printStackTrace();
			System.out.println("Team Proteus Bot has failed to start up! Check the above logs to diagnose the problem");
		}
	}
	
	public void main0(String... args) throws IOException, BotConfigException {
		this.config = new BotConfig(dir);
		JDABuilder.create(
			this.config.getConfig("token"), 
			GUILD_MEMBERS, 
			GUILD_MESSAGES, 
			GUILD_MESSAGE_REACTIONS,
			MESSAGE_CONTENT
		)
		.addEventListeners(new BotListeners(config));
	}
}