package com.github.severinghams;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.hooks.Nonnull;

public class BotListeners extends ListenerAdapter {
	
	private BotConfig config;
	
	private TeamProteusBot bot;
	
	private TextChannel rulesChannel;
	
	private Category ticketCategory;
	
	public BotListeners(TeamProteusBot bot, BotConfig config) {
		this.config = config;
		this.bot = bot;
		this.rulesChannel = this.bot.discord.getTextChannelById(this.config.getConfig("verify-button-channel-id"));
		this.ticketCategory = this.bot.discord.getCategoryById(this.config.getConfig("ticket-category-id"));
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) return;
		if (this.ticketCategory.getChannels().contains(event.getGuildChannel())) {
			System.out.printf("[%s] %#s: %s\n",
			event.getChannel().getName(),
			event.getAuthor(),
			event.getMessage().getContentDisplay());
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		System.out.printf("[%s] %s: %s\n",
		event.getChannel().getName(),
		event.getUser().getName(),
		event.getMessageIdLong());
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		event.retrieveMember();
		event.retrieveMessage();
		event.retrieveUser();
		System.out.println(event.getMember().getNickname());
		System.out.println(event.getUser().getIdLong());
		this.onRemoveReaction(event.getUser(), this.bot.discord.getSelfUser());
	}
	
	@Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
		//if (event.getMessageIdLong() != )
	}
	
	@Override
    public void onMessageReactionRemoveEmoji(MessageReactionRemoveEmojiEvent event) {

		//this.onRemoveReaction(event., this.bot.discord.getSelfUser());
	}
	
	
	private void onRemoveReaction(User user, User self) {
		if (user.getIdLong() == self.getIdLong()) {
			System.out.println("test2");
			this.rulesChannel.addReactionById(this.config.getConfig("verify-button-message-id"), Emoji.fromUnicode("✅")).queue();
		}
	}
}
