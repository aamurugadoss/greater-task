package com.microservice.greaterbank.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toList;

/**
 * @author Murugadoss This is a supporting class used to manipulate batch file.
 */
public class CommonHandler {

	/**
	 * prepare file list
	 * 
	 * @param path
	 * @return list
	 * 
	 */
	public static List<Path> listFile(Path path) {
		try (Stream<Path> paths = Files.walk(path)) {
			return paths.filter(Files::isRegularFile).collect(toList());
		} catch (IOException e) {
			throw new RuntimeException("Error while listing files from " + path, e);
		}
	}

	/**
	 * prepare line list
	 * 
	 * @param path
	 * @return list
	 * 
	 */
	public static List<String> read(Path path) {
		try (BufferedReader lines = Files.newBufferedReader(path)) {
			return lines.lines().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading file " + path, e);
		}
	}

	/**
	 * write report
	 * 
	 * @param path
	 * @param String
	 * 
	 */
	public static void write(Path path, String content) {
		try {
			Files.write(path, content.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("Error writing file " + path, e);
		}
	}

	/**
	 * move files
	 * 
	 * @param path
	 * @param Path
	 * 
	 */
	public static void move(Path source, Path destination) {
		try {
			Files.move(source, destination, REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Error moving file from " + source + " to " + destination, e);
		}
	}

}
