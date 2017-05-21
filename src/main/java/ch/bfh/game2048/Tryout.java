package ch.bfh.game2048;

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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Tryout {

	public void tryOut(){
		//		gson();
		
		try {
			firebase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void firebase() throws IOException{
		// Fetch the service account key JSON file contents
		FileInputStream serviceAccount = new FileInputStream("serviceAccountCredentials.json");

		// Initialize the app with a custom auth variable, limiting the server's access
		Map<String, Object> auth = new HashMap<String, Object>();
		auth.put("uid", "my-service-worker");

		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
		    .setDatabaseUrl("https://game2048mm.firebaseio.com/")
		    .setDatabaseAuthVariableOverride(auth)
		    .build();
		FirebaseApp.initializeApp(options);

		// The app only has access as defined in the Security Rules
		DatabaseReference ref = FirebaseDatabase
		    .getInstance()
		    .getReference("/highscore");
		
		
		
		
		DatabaseReference scoreRef = ref.child("scores");		
		
		
		scoreRef.push().setValue(new Score(234, "alds", 23));
		
		scoreRef.orderByChild("score").addChildEventListener(new ChildEventListener() {
			
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
				
				Score score = arg0.getValue(Score.class);
				System.out.println(score.name +" - "+score.score);
				
			}
			
			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		System.out.println("done...");
		
	}
	
	public void gson(){
		Gson gson = new Gson();
		

		String json = "[{\"score\":829834,\"name\":\"ludi\",\"time\":234},{\"score\":12312,\"name\":\"asdf\",\"time\":23},{\"score\":342,\"name\":\"vbdfg\",\"time\":123},{\"score\":12334234,\"name\":\"asdf\",\"time\":54},{\"score\":123,\"name\":\"ycv\",\"time\":34},{\"score\":546564,\"name\":\"adsf\",\"time\":34}]";
		List<Score> hmm = gson.fromJson(json, new TypeToken<List<Score>>(){}.getType());
		
		System.out.println(hmm.size());
		for(Score s : hmm){
			System.out.println(s.name);
		}
	}
	

}
