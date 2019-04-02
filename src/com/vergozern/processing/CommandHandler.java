package com.vergozern.processing;

import org.alixia.javalibrary.commands.GenericCommand;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.QuickGenericCommand;
import org.alixia.javalibrary.strings.matching.ManipulableString;
import org.alixia.javalibrary.strings.matching.Matching;

import com.vergozern.api.commands.MessageCommand;
import com.vergozern.api.commands.processing.MessageCommandParser;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public final class CommandHandler implements IListener<MessageReceivedEvent> {
	private CommandHandler() {
	}

	private final GenericCommandManager<MessageCommand> commandManager = new GenericCommandManager<>();

	private interface Match {
		boolean match(MessageCommand cmd);
	}

	private class StringMatch implements Match {

		private final String[] matches;
		private final boolean ignoreCase;

		public StringMatch(boolean ignoreCase, String... matches) {
			this.ignoreCase = ignoreCase;
			this.matches = matches;
		}

		public StringMatch(String... matches) {
			this(true, matches);
		}

		@Override
		public boolean match(MessageCommand cmd) {
			for (String s : matches)
				if (!(ignoreCase ? s.equalsIgnoreCase(cmd.command) : s.equals(cmd.command)))
					return false;
			return true;
		}

	}

	private abstract class CommandHandle implements GenericCommand<MessageCommand> {

		{
			commandManager.addCommand(this);
		}
	}

	private abstract class StringCommandHandle extends CommandHandle {

		private final Match[] matches;

		public StringCommandHandle(Match... matches) {
			this.matches = matches;
		}

		@Override
		public boolean match(MessageCommand data) {
			for (Match m : matches)
				if (m.match(data))
					return true;
			return false;
		}

	}

	private final MessageCommandParser commandParser = new MessageCommandParser();

	@Override
	public void handle(MessageReceivedEvent event) {
		ManipulableString message = new ManipulableString(event.getMessage().getContent());
		if (message.consumeIf(Matching.build("~")
				.or(Matching.ignoreCase(event.getClient().getApplicationName()).possibly(",").then(" "))) != null) {

			commandManager.run(commandParser.parse(event));
		}

	}

}
