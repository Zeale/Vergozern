package com.vergozern.launch;

import com.vergozern.processing.CommandHandler;

import sx.blah.discord.api.ClientBuilder;

public class Launch {
	public static void main(String[] args) {
		new ClientBuilder().withToken("NTYwNDQ3MDg2NTE3Mjg4OTgw.D33R6g.2yxvykkFpF-ocw-ECyyM9Mmmnwo")
				.registerListener(new CommandHandler()).build();
	}
}
