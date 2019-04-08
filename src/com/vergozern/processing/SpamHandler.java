package com.vergozern.processing;

import java.io.OutputStream;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

import com.vergozern.Verg�zern;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;

public class SpamHandler implements IListener<MessageReceivedEvent> {

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

	}

	@Override
	public void handle(MessageReceivedEvent event) {

	}

}
