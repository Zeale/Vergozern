package com.vergozern.api.commands.processing;

import org.alixia.javalibrary.commands.processing.StringCommandParser;

import com.vergozern.api.commands.MessageCommand;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class MessageCommandParser {
	// TODO Commit this
	private final StringCommandParser commandParser = new StringCommandParser("");

	public MessageCommand parse(MessageReceivedEvent event) {
		return new MessageCommand(commandParser.parse(event.getMessage().getContent()), event);
	}

}
