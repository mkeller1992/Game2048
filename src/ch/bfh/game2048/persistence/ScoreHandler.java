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

	public void writeScores(Highscore highscores, String xmlName) throws JAXBException, FileNotFoundException {

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(Highscore.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// to marshal "Umlaute" correctly
		m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

		// Write to System.out
		m.marshal(highscores, System.out);

		// Write to File
		m.marshal(highscores, new File(xmlName));
	}

	public Highscore readScores(String xmlName) throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(Highscore.class);

		// get variables from our xml file, created before
		// System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();

		Highscore highscores = (Highscore) um.unmarshal(new FileReader(xmlName));

		// for (GameStatistics g : highscores.getHighscore()) {
		// System.out.println(g.getScore());
		// }
		return highscores;
	}

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
