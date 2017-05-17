package ch.bfh.game2048.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"nickName", "firstName", "lastName", "email"})
public class Player {		
	private String nickName = "";
	private String firstName= "";
	private String lastName="";
	private String email="";

	

	public Player(){		
	}

	public Player(String nickName, String firstName, String lastName) {
		super();
		this.nickName = nickName;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@XmlElement(name="Nickname")
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@XmlElement(name="FirstName")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name="LastName")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(name="EMail")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
	
}
