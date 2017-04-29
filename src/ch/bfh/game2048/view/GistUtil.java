package ch.bfh.game2048.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;

import ch.bfh.game2048.model.GameStatistics;

public class GistUtil {

	public void setHighScore(List<GameStatistics> list) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(list);

			String content = bos.toString("UTF-8");
			setHighScore(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<GameStatistics> getHighScore() {
		try {
			String content = readHighScore();

			ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes("UTF-8"));
			ObjectInputStream ois;
			ois = new ObjectInputStream(bis);
			
			return (List<GameStatistics>) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String readHighScore() {

		GitHubClient client = new GitHubClient().setOAuth2Token("76725398b1da39e06604a3d6e49497a414a6661b");
		GistService gistService = new GistService(client);

		try {

			Gist g = gistService.getGist("cc5c464caba2742d2194c971b5330251");

			GistFile f = g.getFiles().get("Highscore");

			return f.getContent();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void setHighScore(String content) {

		GitHubClient client = new GitHubClient().setOAuth2Token("76725398b1da39e06604a3d6e49497a414a6661b");
		GistService gistService = new GistService(client);

		try {

			Gist g = gistService.getGist("cc5c464caba2742d2194c971b5330251");

			GistFile f = g.getFiles().get("Highscore");

			f.setContent(content);

			g.getFiles().put("Highscore", f);

			gistService.updateGist(g);
			System.out.println(g.getUpdatedAt());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
