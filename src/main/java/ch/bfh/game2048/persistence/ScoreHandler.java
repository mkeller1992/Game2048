package ch.bfh.game2048.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import ch.bfh.game2048.model.Highscore;

public class ScoreHandler {

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
	public void writeScores(Highscore highscores, String xmlFile) throws JAXBException, URISyntaxException {

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(Highscore.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// to marshal "Umlaute" correctly
		m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

		// Write to System.out
		m.marshal(highscores, System.out);

		// Write to File
		m.marshal(highscores, new File(this.getClass().getResource("/highscore/"+xmlFile).toURI()));
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

	public Highscore readScores(String xmlFile) throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(Highscore.class);

		// get variables from our xml file, created before
		// System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();

		InputStreamReader in = new InputStreamReader(this.getClass().getResourceAsStream("/highscore/" + xmlFile));
		Highscore highscores = (Highscore) um.unmarshal(in);

		// for (GameStatistics g : highscores.getHighscore()) {
		// System.out.println(g.getScore());
		// }
		return highscores;
	}
}
