package ch.bfh.game2048.persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.bfh.game2048.model.GameStatistics;

/**
 * 
 * Grüezi Herr Haenni
 * 
 * Wir haben da etwas ausprobiert, es läuft aber nicht wirklich stabil, weshalb
 * es nicht in der Abgabeversion enthalten ist. Trotzdem wollten wir die Klasse
 * nicht entfernen, da wir es nach den Prüfungen mit genügend Zeit nochmals
 * überarbeiten wollen.
 *
 */
public class FirebaseHandler {
	List<GameStatistics> highscore;

	DatabaseReference scoreRef;

	public FirebaseHandler() {
		highscore = new ArrayList<GameStatistics>();

		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addGameStatistic(GameStatistics stats) {

	}

	private void init() throws IOException {
		// Fetch the service account key JSON file contents
		FileInputStream serviceAccount = new FileInputStream("serviceAccountCredentials.json");

		// Initialize the app with a custom auth variable, limiting the server's
		// access
		Map<String, Object> auth = new HashMap<String, Object>();
		auth.put("uid", "my-service-worker");

		FirebaseOptions options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl("https://game2048mm.firebaseio.com/").setDatabaseAuthVariableOverride(auth).build();
		FirebaseApp.initializeApp(options);

		// The app only has access as defined in the Security Rules
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/highscore");

		scoreRef = ref.child("scores");

		scoreRef.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {

				GameStatistics score = arg0.getValue(GameStatistics.class);
				System.out.println(score.getPlayerName() + " - " + score.getScore());

			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	// scoreRef.addValueEventListener(new ValueEventListener() {
	// public void onDataChange(DataSnapshot snapshot) {
	//
	// ArrayList<GameStatistics> tempScoreArray = new
	// ArrayList<GameStatistics>();
	//
	// for (DataSnapshot postSnapshot : snapshot.getChildren()) {
	// GameStatistics score = postSnapshot.getValue(GameStatistics.class);
	// tempScoreArray.add(score);
	// }
	//
	// highscore.setHighscores(tempScoreArray);
	//
	// setChanged();
	// notifyObservers();
	// }
	//
	// @Override
	// public void onCancelled(DatabaseError firebaseError) {
	// System.out.println("The read failed: " + firebaseError.getMessage());
	//
	// }
	// });

	public List<GameStatistics> getHighscore() {
		return highscore;
	}

	public void setHighscore(List<GameStatistics> highscore) {
		this.highscore = highscore;
	}

}
