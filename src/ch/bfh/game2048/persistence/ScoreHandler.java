package ch.bfh.game2048.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;

public class ScoreHandler {


	public void writeScores(Highscore highscores, String xmlName)
			throws JAXBException, FileNotFoundException {
		
		System.out.println("HS Size:"+highscores.getHighscore().size());

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
//		System.out.println("Output from our XML File: ");
		Unmarshaller um = context.createUnmarshaller();

		Highscore highscores = (Highscore) um.unmarshal(new FileReader(xmlName));
		
//		for (GameStatistics g : highscores.getHighscore()) {
//			System.out.println(g.getScore());
//		}	
		return highscores;
	}
	
}
