package com.vergozern.processing;

import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.QuickGenericCommand;
import org.alixia.javalibrary.strings.matching.ManipulableString;
import org.alixia.javalibrary.strings.matching.Matching;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public final class CommandHandler {
	private CommandHandler() {
	}

	private final GenericCommandManager<MessageReceivedEvent> commandManager = new GenericCommandManager<>();

	private abstract class CommandHandle implements QuickGenericCommand<MessageReceivedEvent> {
		{
			commandManager.addCommand(this);
		}
	}

	private abstract class StringCommand extends CommandHandle {
		@Override
		public boolean match(MessageReceivedEvent data) {
			return new ManipulableString(data.getMessage().getContent()).consumeIf(Matching.build("~")
					.or(Matching.ignoreCase(data.getClient().getApplicationName()).possibly(",").then(" "))) != null
							? super.match(data)
							: false;
		}
	}

}
