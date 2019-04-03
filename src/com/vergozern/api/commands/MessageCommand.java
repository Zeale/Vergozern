package com.vergozern.api.commands;

import org.alixia.javalibrary.commands.StringCommand;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

// This logically does not extend StringCommand, but it does here bc it eases
// coding. :)
public class MessageCommand extends StringCommand {

	public final MessageReceivedEvent receivedEvent;
	public final String commandInitiatorText;

	public MessageCommand(String command, String inputText, String commandInitiatorText,
			MessageReceivedEvent receivedEvent, String... args) {
		super(command, inputText, args);
		this.receivedEvent = receivedEvent;
		this.commandInitiatorText = commandInitiatorText;
	}

	public MessageCommand(StringCommand command, String commandInitiatorText, MessageReceivedEvent event) {
		this(command.command, command.inputText, commandInitiatorText, event, command.args);
	}

}
