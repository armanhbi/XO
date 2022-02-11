import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.Scanner;

public class Settings {

	public static void writeSettings(int endGameAt, int resetTimes, int resetDelay, boolean winnerContinuesPlaying,
			boolean showShadow, boolean showResetText) {
		createFile();
		try {
			FileWriter writer = new FileWriter("rcs/settings.txt");
			writer.write("endGameAt:" + endGameAt + "\n");
			writer.write("resetTimes:" + resetTimes + "\n");
			writer.write("resetDelay:" + resetDelay + "\n");
			writer.write("winnerContinuesPlaying:" + winnerContinuesPlaying + "\n");
			writer.write("showShadow:" + showShadow + "\n");
			writer.write("showResetText:" + showResetText + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] readSettings() {
		try {
			File settings = new File("rcs/settings.txt");
			try {
				if (settings.createNewFile()) {
					defaultSettings();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Scanner reader = new Scanner(settings);
			String[] values = new String[6];
			int i = 0;
			while (reader.hasNextLine()) {
				String data = reader.nextLine();
				values[i] = data.split(":")[1];
				i++;
			}
			reader.close();
			return values;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return null;
		} catch (FileSystemNotFoundException e) {
			createFile();
			defaultSettings();
			return readSettings();
		}
	}

	private static void createFile() {
		File settings = new File("rcs/settings.txt");
		try {
			settings.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void defaultSettings() {
		writeSettings(3, 3, 2000, true, true, true);
	}

}
