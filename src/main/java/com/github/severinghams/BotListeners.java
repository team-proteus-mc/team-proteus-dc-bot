package com.github.severinghams;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
	
	//private TextChannel rulesChannel;
	
	//private Category ticketCategory;
	
	//private Message reactMessage;
	
	public BotListeners(TeamProteusBot bot, BotConfig config) {
		this.config = config;
		this.bot = bot;
		//this.rulesChannel = this.bot.discord.getTextChannelById(this.config.getConfig("rules-channel-id").getLong());
		//this.ticketCategory = this.bot.discord.getCategoryById(this.config.getConfig("ticket-category-id").getLong());
		//this.reactMessage = this.rulesChannel.getHistory().getMessageById(this.config.getConfig("rules-message-id").getLong());
	}
	/*
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) return;
		if (this.bot.ticketCategory.getChannels().contains(event.getGuildChannel())) {
			System.out.printf("[%s] %#s: %s\n",
			event.getChannel().getName(),
			event.getAuthor(),
			event.getMessage().getContentDisplay());
		}
	}
	*/
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		System.out.printf("[%s] %s: %s\n",
		event.getChannel().getName(),
		event.getUser().getName(),
		event.getMessageIdLong());
		
		if (event.getMessageIdLong() != this.bot.reactMessageId) {
			return;//this.bot.reactMessage.addReaction(bot.reactEmoji).queue();
		}
		if (event.getUserIdLong() == this.bot.discord.getSelfUser().getIdLong()) {
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
		if (!event.getEmoji().getAsReactionCode().equals(this.bot.reactEmoji.getAsReactionCode())) {
			return;
		}
		event.retrieveMember().queue(
			(user) -> {
				if (user.getRoles().contains(this.bot.memberRole)) {
					return;
				}
				this.bot.handleVerify(user);
			}
		);
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		System.out.println(event.getMember().getNickname());
		System.out.println(event.getUser().getEffectiveName());
		this.onRemoveReaction(event.getUser(), this.bot.discord.getSelfUser());
	}
	
	private void onRemoveReaction(User user, User self) {
		if (user.getIdLong() == self.getIdLong()) {
			System.out.println("test2");
			this.bot.rulesChannel.addReactionById(this.config.getConfig("rules-message-id").getLong(), Emoji.fromUnicode("✅")).queue();
		}
	}
	
	@Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
		if (event.getMessageIdLong() == this.bot.reactMessageId) {
			this.bot.reactMessage.addReaction(bot.reactEmoji).queue();
		}
	}
	
	@Override
    public void onMessageReactionRemoveEmoji(MessageReactionRemoveEmojiEvent event) {
		if (event.getMessageIdLong() == this.bot.reactMessageId && event.getEmoji().getAsReactionCode().equals(this.bot.reactEmoji.getAsReactionCode())) {
			this.bot.reactMessage.addReaction(bot.reactEmoji).queue();
		}
	}
}
