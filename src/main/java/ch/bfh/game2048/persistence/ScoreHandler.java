package ch.bfh.game2048.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.view.model.HighscoreEntry;

public class ScoreHandler {
	private static ScoreHandler instance = null;

	private Highscore highscore = null;

	/**
	 * Marshaller:
	 * 
	 * > Writes game-scores to xml-file
	 * 
	 * 
	 * @param highscores
	 *            : containing list with GameStatistics-objects (game-scores)
	 * @param xmlFile
	 *            : path + name of the xml-file that is to be written (must
	 *            include ".xml")
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 */
	public void writeScores(String xmlFile) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(Highscore.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// to marshal "Umlaute" correctly
			m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

			// Write to System.out
			m.marshal(highscore, System.out);

			// Write to File
			m.marshal(highscore, new File(this.getClass().getClassLoader().getResource("/highscore/" + xmlFile).toURI()));
		} catch (Exception e) {
			System.out.println("Could not save Highscores...");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Unmarshaller:
	 * 
	 * > Reads the game-scores from xml-file
	 * 
	 * @param xmlFile
	 *            : path + name of the xml-file that is to be read (must include
	 *            ".xml")
	 * @return an instance of Highscore containing the list with
	 *         GameStatistics-objects (game-scores)
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */

	public void readScores(String xmlFile) {
		try {
			JAXBContext context = JAXBContext.newInstance(Highscore.class);

			// get variables from our xml file, created before
			// System.out.println("Output from our XML File: ");
			Unmarshaller um = context.createUnmarshaller();

			InputStreamReader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/highscore/" + xmlFile));
			highscore = (Highscore) um.unmarshal(in);

			// for (GameStatistics g : highscores.getHighscore()) {
			// System.out.println(g.getScore());
			// }
		} catch (Exception e) {
			System.out.println("Could not load Highscores...");
			e.printStackTrace();
		}

	}

	/**
	 * Returns the Highscore List converted to a HighscoreEntry-List so that the JavaFX Table can handle it.
	 * Sorts the list, and adds a rank...
	 * @return
	 */
	public List<HighscoreEntry> getHighscoreEntryList() {
		List<HighscoreEntry> hsList = new ArrayList<HighscoreEntry>();

		int i = 1;
		List<GameStatistics> sortedList = highscore.getHighscoreList().stream().sorted((h1,h2) -> h2.compareTo(h1)).collect(Collectors.toList());
		for (GameStatistics stats :  sortedList) {
			HighscoreEntry highscoreEntry = new HighscoreEntry(stats);
			highscoreEntry.setRank(i);
			i++;
			hsList.add(highscoreEntry);
		}

		return hsList;
	}

	public Highscore getHighscore() {
		return highscore;
	}

	public void setHighscore(Highscore highscore) {
		this.highscore = highscore;
	}

	protected ScoreHandler() {

	}

	public static ScoreHandler getInstance() {
		if (instance == null) {
			instance = new ScoreHandler();
			instance.readScores(Config.getInstance().getPropertyAsString("highscoreFileName"));
		}
		return instance;
	}
}
