package com.github.severinghams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListeners extends ListenerAdapter {
	
	private BotConfig config;
	
	private TeamProteusBot bot;
	
	public BotListeners(TeamProteusBot bot, BotConfig config) {
		this.config = config;
		this.bot = bot;
	}
	
	@Override 
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.getName().equals("verify")) return;
		if (!event.isFromGuild() || !event.getChannelType().isMessage()) { event.reply("Cannot verify here! This is not a ticket channel.").setEphemeral(true).queue(); return; }
		if (!this.bot.usermap.containsChannel(event.getChannelIdLong())) { event.reply("Cannot verify here! This is not a ticket channel.").setEphemeral(true).queue(); return; }
		if (this.bot.ticketCategory.getChannels().contains(this.bot.discord.getGuildChannelById(event.getChannelIdLong()))) {
			ArrayList<Message> mh = new ArrayList<Message>(this.bot.discord.getTextChannelById(event.getChannelIdLong()).getHistory().getRetrievedHistory());
			long userid = this.bot.usermap.getUserFromChannel(event.getChannelIdLong());
			this.bot.archiver.archiveChannel(mh, this.bot.discord.getUserById(userid).getName());
			this.bot.guild.retrieveMemberById(userid).queue(
				(user) -> {
					this.bot.guild.addRoleToMember(user, this.bot.memberRole).queue();
					this.bot.guild.removeRoleFromMember(user, this.bot.verifyRole).queue();
					this.bot.guild.getTextChannelById(event.getChannelIdLong()).upsertPermissionOverride(user).setDenied(Permission.MESSAGE_SEND,Permission.MESSAGE_HISTORY,Permission.VIEW_CHANNEL).queue();
				}
			);
			event.reply("User verified! Channel deleting in 1 minute.").queue();
			this.bot.usermap.removeChannelMap(event.getChannelIdLong());
			this.bot.guild.getTextChannelById(event.getChannelIdLong()).delete().queueAfter(60, TimeUnit.SECONDS);
		} else {
			event.reply("Cannot verify here! This is not a ticket channel.").setEphemeral(true).queue();
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageIdLong() != this.bot.reactMessageId) {
			return;//this.bot.reactMessage.addReaction(bot.reactEmoji).queue();
		}
		if (event.getUserIdLong() == this.bot.discord.getSelfUser().getIdLong()) {
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
		if (this.bot.ignoreReqCache.doIgnore(event.getUserIdLong())) {
			return;
		}
		if (!event.getEmoji().getAsReactionCode().equals(this.bot.reactEmoji.getAsReactionCode())) {
			return;
		}
		event.retrieveMember().queue(
			(user) -> {
				this.bot.handleVerification(user);
			}
		);
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		if (event.getMessageIdLong() != this.bot.reactMessageId && event.getUser().getIdLong() == this.bot.discord.getSelfUser().getIdLong()) {
			this.bot.reactMessage.addReaction(this.bot.reactEmoji).queue();
		}
	}
	
	@Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
		if (event.getMessageIdLong() == this.bot.reactMessageId) {
			this.bot.reactMessage.addReaction(this.bot.reactEmoji).queue();
		}
	}
	
	@Override
    public void onMessageReactionRemoveEmoji(MessageReactionRemoveEmojiEvent event) {
		if (event.getMessageIdLong() == this.bot.reactMessageId && event.getEmoji().getAsReactionCode().equals(this.bot.reactEmoji.getAsReactionCode())) {
			this.bot.reactMessage.addReaction(this.bot.reactEmoji).queue();
		}
	}
}
