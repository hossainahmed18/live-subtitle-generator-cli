package com.practice.live_subtitle_generator_cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "subtitle-gen", mixinStandardHelpOptions = true, version = "0.0.1", description = "Generates subtitles for live video streams.")
public class LiveSubtitleGeneratorCliApplication implements Callable<Integer> {

	@Parameters(index = "0", description = "The input HLS URL.")
	private String input;

	@Option(names = { "-n", "--name" }, description = "Name of the user.")
	private String userName;

	/**
	 * Captures the first video frame from an HLS input
	 * and writes it to local ./output/first_frame.jpg
	 */
	private void captureFirstFrame(String input) {
		try {
			String classPath = LiveSubtitleGeneratorCliApplication.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath();

			File classFile = new File(classPath);
			String classDir = classFile.getParent();

			String ffmpegPath = classDir + File.separator + "ffmpeg" + File.separator + "ffmpeg";

			File ffmpegFile = new File(ffmpegPath);
			if (!ffmpegFile.exists()) {
				throw new FileNotFoundException("ffmpeg binary not found at " + ffmpegPath);
			}
			if (!ffmpegFile.canExecute()) {
				throw new SecurityException("ffmpeg binary is not executable");
			}

			String outputDir = classDir + File.separator + "output";
			String outputFile = outputDir + File.separator + "first_frame.jpg";

			List<String> command = Arrays.asList(
					ffmpegPath,
					"-y", // overwrite output
					"-i", input, // HLS input
					"-frames:v", "1", // capture only 1 frame
					"-q:v", "2", // good JPEG quality
					outputFile);

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.inheritIO(); // show ffmpeg logs
			Process process = processBuilder.start();

			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new RuntimeException("ffmpeg exited with code " + exitCode);
			}

			System.out.println("First frame captured at: " + outputFile);

		} catch (IOException | URISyntaxException | InterruptedException e) {
			throw new RuntimeException("Failed to capture first frame", e);
		}
	}

	@Override
	public Integer call() throws Exception {
		System.out.printf(
				"Hey %s, capturing first frame from %s%n",
				userName,
				input);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("\nStopping...");
		}));

		captureFirstFrame(input);
		return 0;
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new LiveSubtitleGeneratorCliApplication()).execute(args);
		System.exit(exitCode);
	}
}
