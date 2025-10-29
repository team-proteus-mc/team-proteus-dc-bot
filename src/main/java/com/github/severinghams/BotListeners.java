package com.github.severinghams;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListeners extends ListenerAdapter {
	
	private TeamProteusBot bot;
	
	public BotListeners(TeamProteusBot bot) {
		this.bot = bot;
	}
	
	@Override 
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.isFromGuild() || !event.getChannelType().isMessage()) { event.reply("Cannot do that here! This is not a ticket channel.").setEphemeral(true).queue(); return; }
		if (!this.bot.usermap.containsChannel(event.getChannelIdLong())) { event.reply("Cannot do that here! This is not a ticket channel.").setEphemeral(true).queue(); return; }
		if (this.bot.ticketCategory.getChannels().contains(this.bot.discord.getGuildChannelById(event.getChannelIdLong()))) {
			MessageHistory mh = MessageHistory.getHistoryFromBeginning(this.bot.guild.getTextChannelById(event.getChannelIdLong())).complete();
			List<Message> lm = mh.getRetrievedHistory();
			long userid = this.bot.usermap.getUserFromChannel(event.getChannelIdLong());
			this.bot.archiver.archiveChannel(lm, this.bot.discord.getUserById(userid).getName());
			Member user = this.bot.guild.retrieveMemberById(userid).complete();
			this.bot.guild.removeRoleFromMember(user, this.bot.verifyRole).queue();
			this.bot.guild.getTextChannelById(event.getChannelIdLong()).upsertPermissionOverride(user).setDenied(Permission.MESSAGE_SEND,Permission.MESSAGE_HISTORY,Permission.VIEW_CHANNEL).queue();
			switch (event.getName()) {
				case "verify": 
					this.bot.guild.addRoleToMember(user, this.bot.verifyRole).queue();
					event.reply("User verified! \r\n-# Channel deleting in 5 minutes.").queue();
					break;
				case "deny": 
					this.bot.ignoreReqCache.unIgnoreUser(userid);
					event.reply("User denied! \r\n-# Channel deleting in 5 minutes.").queue();
					break;
				case "denykick":
					this.bot.ignoreReqCache.unIgnoreUser(userid);
					user.kick().reason(event.getOption("reason").getAsString()).complete();
					event.reply("User denied and kicked! \r\n-# Channel deleting in 5 minutes.").queue();
					break;
				case "denyban": 
					this.bot.ignoreReqCache.unIgnoreUser(userid);
					user.ban(0, TimeUnit.SECONDS).reason(event.getOption("reason").getAsString()).complete();
					event.reply("User denied and banned! \r\n-# Channel deleting in 5 minutes.").queue();
					break;
			}
			this.bot.usermap.removeChannelMap(event.getChannelIdLong());
			this.bot.guild.getTextChannelById(event.getChannelIdLong()).delete().queueAfter(5, TimeUnit.MINUTES);
		} else {
			event.reply("Cannot do that here! This is not a ticket channel.").setEphemeral(true).queue();
		}
	}
	
	
	
/**/
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageIdLong() != this.bot.reactMessageId) {
			return;
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
	/* 
	*/
}
