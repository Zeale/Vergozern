package com.vergozern.processing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import org.alixia.javalibrary.commands.GenericCommand;
import org.alixia.javalibrary.commands.GenericCommandConsumer;
import org.alixia.javalibrary.strings.matching.ManipulableString;
import org.alixia.javalibrary.strings.matching.Matching;

import com.vergozern.api.commands.MessageCommand;
import com.vergozern.api.commands.processing.MessageCommandManager;
import com.vergozern.api.commands.processing.MessageCommandParser;
import com.vergozern.api.permissions.Permission;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
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
				if (ignoreCase ? s.equalsIgnoreCase(cmd.command) : s.equals(cmd.command))
					return true;
			return false;
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

		String leftoverMessage = new ManipulableString(event.getMessage().getContent()).consumeIf(Matching.build("~")
				.or(Matching.ignoreCase(event.getClient().getApplicationName()).possibly(",").then(" ")));

		if (leftoverMessage != null) {
			String matchedText = event.getMessage().getContent().substring(0,
					event.getMessage().getContent().length() - leftoverMessage.length());
			commandManager.run(commandParser.parse(event, matchedText));
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

	private final StringCommandHandle ECHO = new StringCommandHandle("echo") {

		@Override
		public void act(MessageCommand data) {
			data.receivedEvent.getChannel().sendMessage(data.receivedEvent.getMessage().getContent());
		}
	};

	private final StringCommandHandle STOP = new StringCommandHandle("stop", "disconnect") {

		@Override
		public void act(MessageCommand data) {
			data.receivedEvent.getChannel().sendMessage("Logging out...");
			data.receivedEvent.getClient().logout();
		}
	};

	private final StringCommandHandle PLAY = new StringCommandHandle("play", "play-music") {

		@Override
		public void act(MessageCommand data) {
			if (data.args.length < 1)
				data.receivedEvent.getChannel().sendMessage("Please give me a link so that I'll know what to play.");
			else
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(
							"https://www.googleapis.com/youtube/v3/search?maxResults=1&type=video&videoDimension=2D&q="
									+ URLEncoder.encode(String.join(" ", data.args), StandardCharsets.UTF_8.name()))
											.openConnection();
					connection.addRequestProperty("User-Agent", data.receivedEvent.getClient().getApplicationName());
					
				} catch (IOException e) {
					// TODO Print error.
					e.printStackTrace();
				}
		}
	};

	private final StringCommandHandle PURGE_SILENT = new StringCommandHandle("purge-silent", "spurge", "silentpurge",
			"purgesilent", "purges") {

		// Syntax: purge-silent [user-reference] [amount]

		@Override
		public void act(MessageCommand data) {
			if (data.args.length == 0) {
				if (data.receivedEvent.getChannel().isPrivate()) {
					// TODO Account for private channels
				} else {
					// First check if the user has permissions.
					if (Permission.has(data.receivedEvent.getChannel(), Permissions.ADMINISTRATOR,
							Permissions.MANAGE_MESSAGES).has(data.receivedEvent.getAuthor()))
						data.receivedEvent.getChannel().bulkDelete();
					else
						data.receivedEvent.getChannel().sendMessage(
								"You don't have permission to execute that. (You need `Administrator` or `Manage Messages` permissions.");
				}
			} else if (data.args.length == 1) {
				if (data.receivedEvent.getChannel().isPrivate()) {
					// TODO Account for private channels
				} else {

				}
			} else if (data.args.length == 2) {
				if (data.receivedEvent.getChannel().isPrivate()) {
					// TODO Account for private channels
				} else {

				}
			}
		}
	};

	private final StringCommandHandle PURGE = new StringCommandHandle("purge") {

		// Syntax: purge [user-reference] [amount]

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

			}
		}
	};

}
