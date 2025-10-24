package com.github.severinghams;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

import java.io.File;
import java.io.IOException;

public class TeamProteusBot {
	
	public static TeamProteusBot instance;
	
	private static File dir = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "team-proteus-dc-bot");
	
	public BotConfig config;
	
	public JDA discord;
	
	public BotListeners listeners;
	
	public TeamProteusBot() {
		
	}
	
	
	public static void main(String... args) throws InterruptedException {
		instance = new TeamProteusBot();
		try {
			instance.main0(args);
		} catch (IOException | BotConfigException e) {
			e.printStackTrace();
			Thread.sleep(3);			
			System.out.println("Team Proteus Bot has failed to start up! Check the above logs to diagnose the problem");
		}
	}
	
	public void main0(String... args) throws IOException, BotConfigException, InterruptedException {
		this.config = new BotConfig(dir);
		this.discord = JDABuilder.create(
			this.config.getConfig("token"), 
			GUILD_MEMBERS, 
			GUILD_MESSAGES, 
			GUILD_MESSAGE_REACTIONS,
			MESSAGE_CONTENT
		)
		//.addEventListeners(new BotListeners(config))
		.setActivity(Activity.watching("you"))
		.build();
		this.listeners = new BotListeners(this, this.config);
		this.discord.awaitReady();
		this.discord.addEventListener(this.listeners);
		/*MessageEmbed embed = new EmbedBuilder()
				.setColor(java.awt.Color.GREEN)
				.setTitle("React to this message below to accept the rules")
				.setDescription(
						"By clicking the react button below, you agree to accept the rules, " + 
						"and a private channel will be created, so a member of our team " + 
						"can verify you manually.")
				.build();*/
		//this.discord.getTextChannelById(this.config.getConfig("verify-button-channel-id")).sendMessageEmbeds(embed).queue();
		this.discord.getTextChannelById(this.config.getConfig("verify-button-channel-id")).addReactionById(this.config.getConfig("verify-button-message-id"), Emoji.fromUnicode("✅")).queue();
	}
}