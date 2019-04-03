package com.vergozern.api.commands.processing;

import java.util.WeakHashMap;

import org.alixia.javalibrary.commands.GenericCommand;
import org.alixia.javalibrary.commands.GenericCommandConsumer;
import org.alixia.javalibrary.commands.GenericCommandManager;
import org.alixia.javalibrary.commands.OptionalGenericCommandConsumer;
import org.alixia.javalibrary.commands.WeakReferencingGenericCommandManager;

import com.vergozern.api.commands.MessageCommand;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class MessageCommandManager extends GenericCommandManager<MessageCommand> {

	private final WeakHashMap<IChannel, WeakReferencingGenericCommandManager<MessageCommand>> channelManagers = new WeakHashMap<>();
	private final WeakHashMap<IUser, WeakReferencingGenericCommandManager<MessageCommand>> userManagers = new WeakHashMap<>();
	// TODO Make this support Channels AND Users together.

	public WeakReferencingGenericCommandManager<MessageCommand> getChannelManager(IChannel channel) {
		return channelManagers.containsKey(channel) ? channelManagers.get(channel)
				: channelManagers.put(channel, new WeakReferencingGenericCommandManager<>());
	}

	public WeakReferencingGenericCommandManager<MessageCommand> getUserManager(IUser user) {
		return userManagers.containsKey(user) ? userManagers.get(user)
				: userManagers.put(user, new WeakReferencingGenericCommandManager<>());
	}

	public void addCommand(GenericCommand<MessageCommand> cmd, IChannel channel) {
		getChannelManager(channel).addCommand(cmd);
	}

	public void addConsumer(GenericCommandConsumer<MessageCommand> cnsm, IChannel channel) {
		getChannelManager(channel).addConsumer(cnsm);
	}

	public void addOptionalConsumer(OptionalGenericCommandConsumer<MessageCommand> cnsm, IChannel channel) {
		getChannelManager(channel).addOptionalConsumer(cnsm);
	}

	public void removeConsumer(GenericCommandConsumer<MessageCommand> cnsm, IChannel channel) {
		if (channelManagers.containsKey(channel))
			channelManagers.get(channel).removeConsumer(cnsm);
	}

	public void removeCommand(GenericCommand<MessageCommand> cmd, IChannel channel) {
		if (channelManagers.containsKey(channel))
			channelManagers.get(channel).removeCommand(cmd);
	}

	public void removeOptionalCommand(OptionalGenericCommandConsumer<MessageCommand> cnsm, IChannel channel) {
		if (channelManagers.containsKey(channel))
			channelManagers.get(channel).removeOptionalConsumer(cnsm);
	}

	public void addCommand(GenericCommand<MessageCommand> cmd, IUser user) {
		getUserManager(user).addCommand(cmd);
	}

	public void addConsumer(GenericCommandConsumer<MessageCommand> cnsm, IUser user) {
		getUserManager(user).addConsumer(cnsm);
	}

	public void addOptionalConsumer(OptionalGenericCommandConsumer<MessageCommand> cnsm, IUser user) {
		getUserManager(user).addOptionalConsumer(cnsm);
	}

	public void removeConsumer(GenericCommandConsumer<MessageCommand> cnsm, IUser user) {
		if (userManagers.containsKey(user))
			userManagers.get(user).removeConsumer(cnsm);
	}

	public void removeCommand(GenericCommand<MessageCommand> cmd, IUser user) {
		if (userManagers.containsKey(user))
			userManagers.get(user).removeCommand(cmd);
	}

	public void removeOptionalCommand(OptionalGenericCommandConsumer<MessageCommand> cnsm, IUser user) {
		if (userManagers.containsKey(user))
			userManagers.get(user).removeOptionalConsumer(cnsm);
	}

	@Override
	public boolean run(MessageCommand data) {
		return userManagers.containsKey(data.receivedEvent.getAuthor())
				? userManagers.get(data.receivedEvent.getAuthor()).run(data)
				: channelManagers.containsKey(data.receivedEvent.getChannel())
						? channelManagers.get(data.receivedEvent.getChannel()).run(data)
						: super.run(data);
	}

}
