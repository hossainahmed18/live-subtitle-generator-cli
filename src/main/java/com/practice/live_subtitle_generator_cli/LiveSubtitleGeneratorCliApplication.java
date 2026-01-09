package com.practice.live_subtitle_generator_cli;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "subtitle-gen", mixinStandardHelpOptions = true, version = "0.0.1", description = "Generates subtitles for live video streams.")
public class LiveSubtitleGeneratorCliApplication implements Callable<Integer> {

	@Parameters(index = "0", description = "The input video file or stream URL.")
	private String input;

	@Option(names = { "-n", "--name" }, description = "Name of the user.")
	private String userName;

	@Override
	public Integer call() throws Exception {
		System.out.printf("Hey %s, you are generating subtitles for %s%n", userName, input);
		System.out.println("Press Ctrl+C to stop...");

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("\nStopping subtitle generation...");
		}));

		while (!Thread.currentThread().isInterrupted()) {
			System.out.print(".");
			System.out.flush();
			Thread.sleep(2000);
		}

		return 0;
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new LiveSubtitleGeneratorCliApplication()).execute(args);
		System.exit(exitCode);
	}

}