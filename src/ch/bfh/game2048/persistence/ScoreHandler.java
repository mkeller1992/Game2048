package ch.bfh.game2048.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import ch.bfh.game2048.model.Highscore;

public class ScoreHandler {

	GistUtil gistUtil = new GistUtil();

	/**
	 * Marshaller:
	 * 
	 * > Writes game-scores to xml-file
	 * 
	 * 
	 * @param highscores : containing list with GameStatistics-objects (game-scores)
	 * @param xmlFile : path + name of the xml-file that is to be written (must include ".xml")
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	
	
	
	public void writeScores(Highscore highscores, String xmlFile) throws JAXBException, FileNotFoundException {

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(Highscore.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// to marshal "Umlaute" correctly
		m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

		// Write to System.out
		m.marshal(highscores, System.out);

		// Write to File
		m.marshal(highscores, new File(xmlFile));
	}

	
	
	/**
	 * 
	 * Unmarshaller:
	 * 
	 * > Reads the game-scores from xml-file
	 * 
	 * @param xmlFile : path + name of the xml-file that is to be read (must include ".xml")
	 * @return an instance of Highscore containing the list with GameStatistics-objects (game-scores)
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	
	public Highscore readScores(String xmlFile) throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(Highscore.class);

		// get variables from our xml file, created before
		// System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();

		Highscore highscores = (Highscore) um.unmarshal(new FileReader(xmlFile));

		// for (GameStatistics g : highscores.getHighscore()) {
		// System.out.println(g.getScore());
		// }
		return highscores;
	}
	
	
	/**
	 * 
	 * write Scores to Gist
	 * 
	 * @param highscores
	 * @throws JAXBException
	 */

	public void writeScores(Highscore highscores) throws JAXBException {

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(Highscore.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// to marshal "Umlaute" correctly
		m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

		String content;
		StringWriter writer = new StringWriter();

		// Write to File
		m.marshal(highscores, writer);

		content = writer.toString();

		gistUtil.setHighScore(content);
	}

	/**
	 * 
	 * Read scores from Gist
	 * 
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	
	
	public Highscore readScores() throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(Highscore.class);

		// get variables from our xml file, created before
		// System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();

		String content = gistUtil.readHighScore();
		if (!StringUtils.isEmpty(content)) {
			StringReader reader = new StringReader(content);

			Highscore highscores = (Highscore) um.unmarshal(reader);

			// for (GameStatistics g : highscores.getHighscore()) {
			// System.out.println(g.getScore());
			// }
			return highscores;
		}
		return new Highscore();
	}

}
