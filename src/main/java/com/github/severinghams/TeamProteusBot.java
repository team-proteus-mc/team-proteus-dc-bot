package com.github.severinghams;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.Route.Users;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TeamProteusBot {
	
	public static TeamProteusBot instance;
	
	private static File dir = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "team-proteus-dc-bot");
	
	public BotConfig config;
	public JDA discord;
	public BotListeners listeners;
	
	public TextChannel rulesChannel;
	public Message reactMessage;
	public long reactMessageId;
	public Category ticketCategory;
	public Emoji reactEmoji;
	public Role verifyRole;
	public Role memberRole;
	public Role modRole;
	public Role adminRole;
	public Role ownerRole;
	
	private ArrayList<User> userQueue = new ArrayList<User>();
	
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
	
	public void handleVerify(Member user) {
		
	}
	
	public void main0(String... args) throws IOException, BotConfigException, InterruptedException {
		this.config = new BotConfig(dir);
		this.discord = JDABuilder.create(
			this.config.getConfig("token").getString(), 
			GUILD_MEMBERS, 
			GUILD_MESSAGES, 
			GUILD_MESSAGE_REACTIONS,
			MESSAGE_CONTENT
		)
		//.addEventListeners(new BotListeners(config))
		.setActivity(Activity.customStatus("sigma very sigma"))
		.build();
		this.listeners = new BotListeners(this, this.config);
		this.discord.awaitReady();
		this.discord.addEventListener(this.listeners);
		this.rulesChannel = this.discord.getTextChannelById(this.config.getConfig("rules-channel-id").getLong());
		this.ticketCategory = this.discord.getCategoryById(this.config.getConfig("ticket-category-id").getLong());
		this.reactEmoji = Emoji.fromUnicode("✅");
		this.verifyRole = this.discord.getRoleById(this.config.getConfig("verify-role-id").getLong());
		this.memberRole = this.discord.getRoleById(this.config.getConfig("member-role-id").getLong());
		this.modRole = this.discord.getRoleById(this.config.getConfig("mod-role-id").getLong());
		this.adminRole = this.discord.getRoleById(this.config.getConfig("admin-role-id").getLong());
		this.ownerRole = this.discord.getRoleById(this.config.getConfig("owner-role-id").getLong());
		boolean flag0 = false;
		if (this.rulesChannel.getLatestMessageIdLong() == this.config.getConfig("rules-message-id").getLong()) {
			this.reactMessageId = this.config.getConfig("rules-message-id").getLong();
			this.rulesChannel.retrieveMessageById(reactMessageId)
			.queue(
				(message) -> {
					this.reactMessage = message;
					ArrayList<MessageReaction> a = new ArrayList<MessageReaction>(this.reactMessage.getReactions());
					MessageReaction mr = null;
					for (MessageReaction mr0 : a) {
						if (!mr0.getEmoji().getAsReactionCode().equals(reactEmoji.getAsReactionCode())) {
							mr0.clearReactions().queue();
						} else {
							mr = mr0;
						}
					}
					if (mr == null) {
						this.reactMessage.addReaction(reactEmoji).queue();
					} else {
						mr.retrieveUsers().queue(
							(users) -> {
								boolean flag1 = true;
								for (int i = 0; i < users.size(); i++) {
									if (users.get(i).getIdLong() != this.discord.getSelfUser().getIdLong()) {
										this.reactMessage.removeReaction(reactEmoji, users.get(i)).queue();
										this.reactMessage.getGuild().retrieveMemberById(users.get(i).getIdLong()).queue(
											(user) -> {
												if (user.getRoles().contains(this.memberRole)) {
													System.out.println("exit4");
													return;
												}
												this.handleVerify(user);
											}
										);
									} else {
										flag1 = false;
									}
								}
								if (flag1) {
									this.reactMessage.addReaction(reactEmoji).queue();
								}
							},
							(fuck) -> {
								
							}
						);
					}
				},
				(fuck) -> {
					MessageEmbed embed = new EmbedBuilder()
							.setColor(java.awt.Color.GREEN)
							.setTitle("React to this message below to accept the rules")
							.setDescription(
									"By clicking the react button below, you agree to accept the rules." + 
									"\r\n\r\nA private chaannel will be created so that a member of our team " + 
									"can verify you manually.")
							.build();
					this.rulesChannel.sendMessageEmbeds(embed).queue(
						(message) -> {
							this.reactMessage = message;
							this.reactMessageId = message.getIdLong();
							this.config.setMessageId(new ConfigItem(reactMessageId+"", ConfigItem.Type.LNG));
							this.reactMessage.addReaction(reactEmoji).queue();
						}
					);
				}
			);
		} else {
			flag0 = true;
		}
		
		if (flag0) {
			MessageEmbed embed = new EmbedBuilder()
					.setColor(java.awt.Color.GREEN)
					.setTitle("React to this message below to accept the rules")
					.setDescription(
							"By clicking the react button below, you agree to accept the rules." + 
							"\r\n\r\nA private chaannel will be created so that a member of our team " + 
							"can verify you manually.")
					.build();
			this.rulesChannel.sendMessageEmbeds(embed).queue(
				(message) -> {
					this.reactMessage = message;
					this.reactMessageId = message.getIdLong();
					this.config.setMessageId(new ConfigItem(reactMessageId+"", ConfigItem.Type.LNG));
					this.reactMessage.addReaction(reactEmoji).queue();
				}
			);

		}
	}
}