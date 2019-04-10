package com.vergozern;

import java.util.ArrayList;
import java.util.List;

import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;

public class Vergözern {

	private final static FileStorage DEFAULT_PROGRAM_STORAGE;
	static {
		List<String> paths = new ArrayList<>(5);
		paths.add("C:/Vergözern");
		paths.add("C:/Program Files/Vergözern");
		paths.add("C:/Program Files (x86)/Vergözern");

		String path = System.getProperty("user.home");
		if (path != null) {
			paths.add(path + "/AppData/Roaming/Vergözern");
			paths.add(path + "/Vergözern");
		}

		DEFAULT_PROGRAM_STORAGE = FileStorage.create(paths.toArray(new String[paths.size()]));
	}

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
