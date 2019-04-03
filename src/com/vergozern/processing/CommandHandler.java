package com.vergozern.processing;

import java.util.EnumSet;

import org.alixia.javalibrary.commands.GenericCommand;
import org.alixia.javalibrary.commands.GenericCommandConsumer;
import org.alixia.javalibrary.strings.matching.ManipulableString;
import org.alixia.javalibrary.strings.matching.Matching;

import com.vergozern.api.commands.MessageCommand;
import com.vergozern.api.commands.processing.MessageCommandManager;
import com.vergozern.api.commands.processing.MessageCommandParser;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

public final class CommandHandler implements IListener<MessageReceivedEvent> {
	public CommandHandler() {
	}

	private final MessageCommandManager commandManager = new MessageCommandManager();

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

		public StringCommandHandle(String... matches) {
			this(new StringMatch(matches));
		}

		public StringCommandHandle(boolean ignorecase, String... matches) {
			this(new StringMatch(ignorecase, matches));
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

	// TODO Make this use the HelpCommandHandler that I just remembered existed.
	private final StringCommandHandle HELP = new StringCommandHandle("help") {

		@Override
		public void act(MessageCommand data) {
			StringBuilder msg = new StringBuilder();
			if (data.args.length == 0) {
				msg.append("This command is ready yet. :P");
			} else if (data.args.length > 0) {
				if (data.args.length > 1)
					msg.append(
							data.command + "'s syntax is `" + data.command + " [page-number|command-name]`. You gave "
									+ data.args.length + " arguments. Ignoring unnecessary args.\n\n");

				try {
					int page = Integer.parseInt(data.args[0]);
					msg.append("Help menu #" + page + ": \n");
					msg.append("This command doesn't actually display anything, since it's a WIP rn.");
				} catch (NumberFormatException e) {
					if (data.args[0].equalsIgnoreCase("help")) {
						data.receivedEvent.getChannel().sendMessage(new EmbedBuilder().appendDesc(msg.toString())
								.appendField('"' + data.command + "\" help...",
										"Help for the command, \"" + data.command + '"', false)
								.appendField("\"" + data.command + "\" Syntax",
										"`" + data.command + " [page-number|command-name]`", false)
								.appendField("Description",
										"Shows help for a page number, (`page-number`), or command, (`command-name`).",
										true)
								.build());
						return;
					}
				}
			}
			data.receivedEvent.getChannel().sendMessage(msg.toString());
		}
	};

	private final StringCommandHandle PURGE = new StringCommandHandle("purge") {

		// Syntax: purge ["-silent"] [user-reference] [amount]

		@Override
		public void act(MessageCommand data) {
			if (data.args.length == 0) {
				if (data.receivedEvent.getChannel().isPrivate()) {
					data.receivedEvent.getChannel().sendMessage(
							"I can't purge everyone's messages in a private channel. Would you like me to delete my own messages instead? (Yes/No)");

					// This opens an exploit. Someone could respond "yes" to deleting all of the
					// bot's messages in one channel, after the original command was issued in the
					// other channel.
					commandManager.addConsumer(new GenericCommandConsumer<MessageCommand>() {

						@Override
						public void act(MessageCommand data) {
							if (data.args.length == 0)
								if (data.command.equalsIgnoreCase("yes")) {

									return;
								} else {
									data.receivedEvent.getChannel().sendMessage("Ok.");
									return;
								}
							data.receivedEvent.getChannel()
									.sendMessage("Please answer with either `Yes` or `No`, (or, `Y` or `N`).");
							commandManager.addConsumer(this, data.receivedEvent.getChannel());
						}
					}, data.receivedEvent.getChannel());
				} else {
					EnumSet<Permissions> perms = data.receivedEvent.getChannel()
							.getModifiedPermissions(data.receivedEvent.getClient().getOurUser());
					if (perms.contains(Permissions.MANAGE_MESSAGES) || perms.contains(Permissions.ADMINISTRATOR)) {
						data.receivedEvent.getChannel().bulkDelete();
					}
				}

			} else if (data.args.length == 1) {
				// TODO The remainder of this command.
			}
		}
	};

}
