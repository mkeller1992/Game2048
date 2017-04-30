package ch.bfh.game2048.model;

import java.io.Serializable;

public class Player implements Serializable {
	private static final long serialVersionUID = -8873582266184927461L;
	
	String nickName;
	String firstName;
	String lastName;
	String email;
	
	public Player(){		
	}

	public Player(String nickName, String firstName, String lastName) {
		super();
		this.nickName = nickName;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	
	
	
}
