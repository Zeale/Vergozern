package com.vergozern;

import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;

public class Vergözern {
	private final static FileStorage DEFAULT_PROGRAM_STORAGE = FileStorage.create(
			System.getProperty("user.home", "C:/Program Files") + "/Vergözern", "C:/Program Files (x86)/Vergözern");

	/**
	 * Returns a {@link FileStorage} that can be used for saving data.
	 * 
	 * @param name The name of the directory. (This cannot be a full path, and thus
	 *             cannot contain slashes (<code>/</code>).)
	 * @return A new {@link FileStorage}.
	 */
	public static FileStorage getFileStorage(String name) {
		return DEFAULT_PROGRAM_STORAGE.createChild(name);
	}

}
