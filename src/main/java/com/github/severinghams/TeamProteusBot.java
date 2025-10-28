package com.github.severinghams;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TeamProteusBot {
	
	public static TeamProteusBot instance;
	
	private static File dir = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "team-proteus-dc-bot");
	
	public BotConfig config;
	public ChannelToUserMap usermap;
	public IgnoreRequestCache ignoreReqCache;
	public ChannelArchiver archiver;
	public JDA discord;
	public BotListeners listeners;
	
	public Guild guild;
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
	
	// if you are a programmer looking at my code, please forgive the stupid amount of nesting.
	// i needed to nest the lambdas. perhaps there's a better way to do this, and i might find it, 
	// but i don't know it right now.
	// i have provided as much commenting as i can, hopefully it helps.
	// names in all caps in comments are just supposed to help identify which curly braces go to what
	public void main0(String... args) throws IOException, BotConfigException, InterruptedException {
		this.config = new BotConfig(dir);
		this.usermap = new ChannelToUserMap(dir);
		this.ignoreReqCache = new IgnoreRequestCache();
		this.archiver = new ChannelArchiver(dir);
		this.discord = JDABuilder.create(
			this.config.getConfig("token").getString(), 
			GUILD_MEMBERS, 
			GUILD_MESSAGES, 
			GUILD_MESSAGE_REACTIONS,
			MESSAGE_CONTENT
		)
		.setActivity(Activity.customStatus("sigma very sigma"))
		.build();

		CommandListUpdateAction commands = discord.updateCommands();
		
		commands.addCommands(
			Commands
				.slash("verify", "Verify the user in the current channel.")
				.setContexts(InteractionContextType.GUILD)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
		);
		commands.queue();
		
		this.listeners = new BotListeners(this, this.config);
		this.discord.awaitReady();
		if (this.config.getConfig("guild-id").getLong() == 0) {
			this.config.setGuildId(new ConfigItem((this.discord.getGuilds().get(0).getIdLong()+""), ConfigItem.Type.LNG));
		}
		this.guild = this.discord.getGuildById(this.config.getConfig("guild-id").getLong());
		this.discord.addEventListener(this.listeners);
		this.rulesChannel = this.discord.getTextChannelById(this.config.getConfig("rules-channel-id").getLong());
		this.ticketCategory = this.discord.getCategoryById(this.config.getConfig("ticket-category-id").getLong());
		this.reactEmoji = Emoji.fromUnicode("✅");
		this.verifyRole = this.discord.getRoleById(this.config.getConfig("verify-role-id").getLong());
		this.memberRole = this.discord.getRoleById(this.config.getConfig("member-role-id").getLong());
		this.modRole = this.discord.getRoleById(this.config.getConfig("mod-role-id").getLong());
		this.adminRole = this.discord.getRoleById(this.config.getConfig("admin-role-id").getLong());
		this.ownerRole = this.discord.getRoleById(this.config.getConfig("owner-role-id").getLong());
		if (this.rulesChannel.getLatestMessageIdLong() == this.config.getConfig("rules-message-id").getLong()) {
		// IFMESSAGEID0 -> if latest message id is equal to messa
			this.reactMessageId = this.config.getConfig("rules-message-id").getLong();
			this.rulesChannel.retrieveMessageById(reactMessageId).queue( 
				// RETRIEVEM0 -> get message from id in config
				(message) -> { // RETRIEVEM0 -> on success 
					this.reactMessage = message;
					ArrayList<MessageReaction> a = new ArrayList<MessageReaction>(this.reactMessage.getReactions());
					MessageReaction mr = null;
					
					// BEGIN clear irrelevant emojis
					for (MessageReaction mr0 : a) {
						if (!mr0.getEmoji().getAsReactionCode().equals(reactEmoji.getAsReactionCode())) {
							mr0.clearReactions().queue();
						} else {
							mr = mr0;
						}
					}
					// END clear irrelevant emojis
					
					if (mr == null) { // if after clearing irrelevant emojis, the emoji is not found, add the emoji.
						this.reactMessage.addReaction(reactEmoji).queue();
					} else { // else, if the relevant emoji does exist on the message
						mr.retrieveUsers().queue(
						// RETRIEVEU1 -> get users of relevant emoji reaction
							(users) -> { // RETRIEVEU1 -> on success
								boolean flag1 = true; // flag for if emoji created by the bot
								for (int i = 0; i < users.size(); i++) {
									if (users.get(i).getIdLong() != this.discord.getSelfUser().getIdLong()) {
										this.reactMessage.removeReaction(reactEmoji, users.get(i)).queue();
										this.reactMessage.getGuild().retrieveMemberById(users.get(i).getIdLong()).queue(
											(user) -> {
												this.handleVerification(user);
											}
										);
									} else {
										flag1 = false; // set flag to true if bot's reaction found
									}
								}
								if (flag1) { // if flag isnt set (if bot's reaction isnt found) 
											 // then add the reaction. idk why i made this redundant.
									this.reactMessage.addReaction(reactEmoji).queue();
								}
							},
							(fail) -> { // RETRIEVEU1 -> on fail, i guess do nothing
								//what would i do here tbh
								
							}
						);
					}
				},
				(fail) -> { // RETRIEVEM0 -> on fail, create message because message not found.
					this.createReactMessage();
				}
			);
		} else { // IFMESSAGEID0 -> else, if the id in config is not the id of the latest message, create a new message
			this.createReactMessage();
		}
	}
	
	private void createReactMessage() {
		MessageEmbed embed = new EmbedBuilder()
				.setColor(java.awt.Color.GREEN)
				.setTitle("React to this message below to accept the rules")
				.setDescription(
						"By clicking the react button below, you agree to accept the rules." + 
						"\r\n\r\nA private channel will be created so that a member of our team " + 
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
	
	
	public void handleVerification(Member user) {
		if (user.getRoles().contains(this.memberRole) || user.getRoles().contains(this.verifyRole)) {
			this.ignoreReqCache.ignoreUser(user.getIdLong());
			return;
		}
		try {
			long userId = user.getIdLong();
			this.guild.addRoleToMember(user, verifyRole).queue();
			this.ticketCategory.createTextChannel(user.getUser().getName()+"-ticket").queue(
				(channel) -> {
					channel.upsertPermissionOverride(user).setAllowed(Permission.MESSAGE_SEND,Permission.MESSAGE_HISTORY,Permission.VIEW_CHANNEL).queue(
						(a) -> {
							MessageEmbed embed = new EmbedBuilder()
									.setColor(java.awt.Color.BLUE)
									.setTitle("Verification Ticket for "+user.getEffectiveName())
									.setDescription(
											"This is a ticket created for verifying that you are a good fit for our community before granting access. Staff "
											+ "may ask a few things about yourself, like interests and about how you discovered our server."
											+ "\r\n\r\n"
											+ "You will NOT be asked for any personally identifiable information. If staff ask you for such information, "
											+ "either in DMs or in this channel, DO NOT provide such information, and please ping or DM the Server Owner "
											+ "about this immediately. "
											+ "\r\n\r\n"
											+ "Plaintext-only records of this channel will be kept for moderation purposes only. For your privacy, it will "
											+ "never be shared with anyone beyond the staff of this server."
											+ "\r\n\r\n"
											+ "Please give staff time to respond, as we are a small team and we may be busy."
											+ "\r\n\r\n")
									.setFooter("Thank you for joining Team Proteus")
									.setThumbnail(user.getAvatarUrl())
									.build();
							channel.sendMessageEmbeds(embed).queue();
							channel.sendMessage(this.ownerRole.getAsMention()+" "+this.adminRole.getAsMention()+" "+this.modRole.getAsMention()+" "+user.getAsMention()).queue();
						}
							
					);
					this.usermap.addChannelMap(channel.getIdLong(), userId);
				}
			);
			this.ignoreReqCache.ignoreUser(user.getIdLong()); 
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}