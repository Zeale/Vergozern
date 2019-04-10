package com.vergozern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import branch.alixia.unnamed.Datamap;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage.Data;

public class Vergözern {

	private static final String NAME = "Vergözern";

	/**
	 * Prints the specified debug text out.
	 * 
	 * @param text The debug text to print.
	 */
	public static void debug(String text) {
		System.out.println("[" + NAME + "][Debug]: " + text);
	}

	/**
	 * Prints a separation of some sort to the debug output.
	 */
	public static void debugBreak() {
		System.out.println("\n");
	}

	private final static FileStorage DEFAULT_PROGRAM_STORAGE;
	static {
		List<String> paths = new ArrayList<>(5);
		paths.add("C:/" + NAME);
		paths.add("C:/Program Files/" + NAME);
		paths.add("C:/Program Files (x86)/" + NAME);

		debug("Collecting possible program storage paths.");

		String path = System.getProperty("user.home");
		if (path != null) {
			debug("\"user.home\" property is defined: (" + path
					+ "). Adding based paths to possible program storage path list.");
			paths.add(path + "/AppData/Roaming/" + NAME);
			paths.add(path + "/Vergözern");
		} else
			debug("\"user.home\" system property is undefined; couldn't add paths based off of it to possible program storage path list.");

		debug(((DEFAULT_PROGRAM_STORAGE = FileStorage.create(paths.toArray(new String[paths.size()]))) == null)
				? "Failed to find a suitable path for  program data storage!"
				: "Found a suitable path for program data storage: " + DEFAULT_PROGRAM_STORAGE.getFile() + ".");

		debugBreak();
		debug("Entering program datamap loading phase...");
		Datamap map;
		try {
			File file = new File(DEFAULT_PROGRAM_STORAGE.getFile(), NAME + " Data.vgn");
			if (!file.exists()) {
				debug("A datamap file did not exist. Creating a new datamap now...");
				map = new Datamap();
				debug("...success.");
			} else {
				debug("A datamap was found in the program storage directory. Attempting to load from it.");
				map = Datamap.read(new FileInputStream(file));
				debug("Successfully loaded in program datamap.");
			}
		} catch (Exception e) {
			debug("Failed to read datamap file.");
			debug("The datamap file existed when checked, but could not be read in. Printing stacktrace, then creating a new datamap now...");
			e.printStackTrace();
			map = new Datamap();
			debug("...success.");
		}
		DATAMAP = map;
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

	public final static Datamap DATAMAP;

	public static final Map<Object, Object> PIPE = new HashMap<>();

	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Data file;
			if (DEFAULT_PROGRAM_STORAGE.create()
					&& (file = DEFAULT_PROGRAM_STORAGE.createFile(NAME + " Data.vgn")).create()) {
				try (FileOutputStream out = new FileOutputStream(file.getFile())) {
					Datamap.save(DATAMAP, out);
				} catch (IOException e) {
					System.err.println("Failed to save the program DATAMAP.");
					e.printStackTrace();
					System.out.println("DATAMAP: \n\n" + DATAMAP);
				}
			} else {
				System.err.println("Failed to create the file to write the program datamap to.");
			}
		}));
	}

}
