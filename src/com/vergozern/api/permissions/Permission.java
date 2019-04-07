package com.vergozern.api.permissions;

import java.util.Collection;
import java.util.EnumSet;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public interface Permission {
	boolean has(IUser user);

	default Permission and(Permission other) {
		return user -> has(user) && other.has(user);
	}

	default Permission or(Permission other) {
		return user -> has(user) || other.has(user);
	}

	default Permission xor(Permission other) {
		return user -> has(user) ^ other.has(user);
	}

	default Permission not() {
		return user -> !has(user);
	}

	static Permission has(IGuild guild, Permissions... perms) {
		return user -> {
			EnumSet<Permissions> permissionSet = user.getPermissionsForGuild(guild);
			for (Permissions p : perms)
				if (!permissionSet.contains(p))
					return false;
			return true;
		};
	}

	static Permission has(IGuild guild, Permissions perm) {
		return u -> u.getPermissionsForGuild(guild).contains(perm);
	}

	static Permission has(IChannel channel, Permissions... perms) {
		return user -> {
			Collection<Permissions> permissions = channel.getModifiedPermissions(user);
			for (Permissions s : perms)
				if (!permissions.contains(s))
					return false;
			return true;
		};
	}

	static Permission has(IChannel channel, Permissions perm) {
		return u -> channel.getModifiedPermissions(u).contains(perm);
	}
}
