package com.vergozern.api.commands;

import org.alixia.javalibrary.commands.StringCommand;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

// This logically does not extend StringCommand, but it does here bc it eases
// coding. :)
public class MessageCommand extends StringCommand {

	public final MessageReceivedEvent receivedEvent;

	public MessageCommand(String command, String inputText, MessageReceivedEvent receivedEvent, String... args) {
		super(command, inputText, args);
		this.receivedEvent = receivedEvent;
	}

	public MessageCommand(StringCommand command, MessageReceivedEvent event) {
		super(command.command, command.inputText, command.args);
		receivedEvent = event;
	}

}
