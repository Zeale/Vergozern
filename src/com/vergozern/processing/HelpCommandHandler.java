package com.vergozern.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.alixia.javalibrary.strings.matching.Matching;

public class HelpCommandHandler<DT> {
	private final List<HelpListing> listings = new ArrayList<>();

	public final class HelpListing {

		public void hide() {
			listings.remove(this);
		}

		public void show() {
			if (!listings.contains(this))
				listings.add(this);
		}

		private final Matching matching;
		private final Consumer<DT> input;

		public HelpListing(Matching matching, Consumer<DT> input) {
			this.matching = matching;
			this.input = input;
		}

	}

	public void run(String helpInput, DT event) {
		for (HelpListing listing : listings)
			if (listing.matching.matches(helpInput)) {
				listing.input.accept(event);
				return;
			}
	}
}
