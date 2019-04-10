package com.vergozern.processing;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import com.vergozern.Vergözern;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage.SubStorage;

public class SpamHandler implements IListener<MessageReceivedEvent> {

	private final FileStorage output = Vergözern.getFileStorage("Spam Handling");

	private long secsTillDiscard;

	private final WeakHashMap<IGuild, WeakHashMap<IChannel, WeakHashMap<IUser, List<IMessage>>>> messages = new WeakHashMap<>();

	public void clear() {
		// TODO Write
	}

	public void flush(OutputStream output) {
		// TODO Write
	}

	public void flush() {

	}

	private void writeMessage(IMessage msg) {
		String guildHsh = msg.getGuild().getName() + "#" + msg.getGuild().getStringID(),
				channelHsh = msg.getChannel().getName() + "#" + msg.getChannel().getStringID(),
				userHsh = msg.getAuthor().getName() + "#" + msg.getStringID();
		SubStorage folder = output.createChild(guildHsh);
		if (!(folder.isAvailable() && (folder = folder.createChild(channelHsh)).isAvailable())
				&& (folder = folder.createChild(userHsh)).isAvailable()) {
			System.err.println("Failed to write a message to a file.");
			System.err.println("\tGuild: " + guildHsh);
			System.err.println("\tChannel: " + channelHsh);
			System.err.println("\tAuthor: " + userHsh);
			System.err.println("\tMessage:" + msg.getContent());
			return;
		}
		try (PrintWriter writer = new PrintWriter(
				folder.createFile(msg.getTimestamp().toString() + ".txt").getFile())) {
			writer.print(msg.getContent());
		} catch (FileNotFoundException e) {
			System.err.println("Failed to write a message to a file.");
			System.err.println("\tGuild: " + guildHsh);
			System.err.println("\tChannel: " + channelHsh);
			System.err.println("\tAuthor: " + userHsh);
			System.err.println("\tMessage:" + msg.getContent());
			System.err.println("Printing stacktrace:");
			e.printStackTrace();
		}

	}

	private synchronized WeakHashMap<IChannel, WeakHashMap<IUser, List<IMessage>>> getGuildData(IGuild guild) {
		WeakHashMap<IChannel, WeakHashMap<IUser, List<IMessage>>> guildData = messages.get(guild);
		if (guildData == null)
			messages.put(guild, guildData = new WeakHashMap<>());
		return guildData;
	}

	private synchronized WeakHashMap<IUser, List<IMessage>> getChannelData(IGuild guild, IChannel channel) {
		WeakHashMap<IChannel, WeakHashMap<IUser, List<IMessage>>> guildData = getGuildData(guild);
		WeakHashMap<IUser, List<IMessage>> channelData = guildData.get(channel);
		if (channelData == null)
			guildData.put(channel, channelData = new WeakHashMap<>());
		return channelData;
	}

	private synchronized List<IMessage> getUserData(IGuild guild, IChannel channel, IUser user) {
		WeakHashMap<IUser, List<IMessage>> channelData = getChannelData(guild, channel);
		List<IMessage> userData = channelData.get(user);
		if (userData == null)
			channelData.put(user, userData = new LinkedList<>());
		return userData;
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		// Save data
		// TODO Check for spam in different channels.
		List<IMessage> msgs = getUserData(event.getGuild(), event.getChannel(), event.getAuthor());
		msgs.add(event.getMessage());
		System.out.println(event.getMessage().getContent());
		// TODO Check vowel separation

		// If 4 messages were sent within 3 seconds, and each message was > 20 chars,
		// mark as spam.
		COND: if (msgs.size() >= 4) {
			for (IMessage m : msgs.subList(msgs.size() - 4, msgs.size()))
				if (m.getContent().length() <= 20)
					break COND;
			Duration between = Duration.between(event.getMessage().getTimestamp(),
					msgs.get(msgs.size() - 4).getTimestamp());
			long millis = between.getSeconds() * 1000 + between.getNano() / 1000000;
			if (millis < 3000) {
				handleSpam(4, event);
				return;
			}
		}

	}

	private void handleSpam(int count, MessageReceivedEvent event) {
		for (IRole r : event.getAuthor().getRolesForGuild(event.getGuild()))
			event.getAuthor().removeRole(r);
		// TODO Add gag role.
	}
}
