package com.khubla.hsinflux;

import org.apache.commons.cli.*;

public class Main {
	private static final String HOMESEER_URL = "hsurl";
	private static final String HOMESEER_USERNAME = "hsuser";
	private static final String HOMESEER_PASSWORD = "hspassword";
	private static final String INFLUX_URL = "influxurl";
	private static final String INFLUX_USERNAME = "influxuser";
	private static final String INFLUX_PASSWORD = "influxpassword";

	public static void main(String... args) {
		System.out.println("HomeSeer Influx Importer");
		/*
		 * options
		 */
		final Options options = new Options();
		Option oo = Option.builder().argName(HOMESEER_URL).longOpt(HOMESEER_URL).type(String.class).hasArg().required(true).desc("HS URL").build();
		options.addOption(oo);
		oo = Option.builder().argName(HOMESEER_USERNAME).longOpt(HOMESEER_USERNAME).type(String.class).hasArg().required(true).desc("HS Username").build();
		options.addOption(oo);
		oo = Option.builder().argName(HOMESEER_PASSWORD).longOpt(HOMESEER_PASSWORD).type(String.class).hasArg().required(true).desc("HS Password").build();
		options.addOption(oo);
		oo = Option.builder().argName(INFLUX_URL).longOpt(INFLUX_URL).type(String.class).hasArg().required(true).desc("Influx URL").build();
		options.addOption(oo);
		oo = Option.builder().argName(INFLUX_USERNAME).longOpt(INFLUX_USERNAME).type(String.class).hasArg().required(true).desc("Influx Username").build();
		options.addOption(oo);
		oo = Option.builder().argName(INFLUX_PASSWORD).longOpt(INFLUX_PASSWORD).type(String.class).hasArg().required(true).desc("Influx Password").build();
		options.addOption(oo);
		/*
		 * parse
		 */
		final CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			/*
			 * go
			 */
			final Importer importer = new Importer(cmd.getOptionValue(HOMESEER_URL), cmd.getOptionValue(HOMESEER_USERNAME), cmd.getOptionValue(HOMESEER_PASSWORD), cmd.getOptionValue(INFLUX_URL),
					cmd.getOptionValue(INFLUX_USERNAME), cmd.getOptionValue(INFLUX_PASSWORD));
			importer.run();
		} catch (final Exception e) {
			e.printStackTrace();
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("posix", options);
		}
	}
}