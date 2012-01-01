package control;

import java.util.List;

/**
 * This class represents a suggestion - i.e. a card triple of a person, weapon
 * and room. This class is not only used to define suggestion announced by
 * players, but also in other contexts like defining the solution envelope or
 * solution attempts of players.
 */
public class Suggestion {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return 
			(person != null ? person : "Someone")
			+ " with " + (weapon != null ? ("the " + weapon) : "some weapon")
			+ " in " + (room != null ? ("the " + room) : "some room");
	}

	private Card person;
	private Card weapon;
	private Card room;
	
	/**
	 * Constructor. Initializes cards with null.
	 */
	public Suggestion() {
		this.person = null;
		this.weapon = null;
		this.room = null;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param person the suggested suspect
	 * @param weapon the suggested weapon
	 * @param room the suggested room
	 * @throws IllegalArgumentException if one of the cards has wrong kind
	 */
	public Suggestion(Card person, Card weapon, Card room) 
			throws IllegalArgumentException {
		this.setPerson(person);
		this.setWeapon(weapon);
		this.setRoom(room);
	}

	/**
	 * @return the person
	 */
	public Card getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 * @throws IllegalArgumentException if person has not kind Kind.PERSON
	 */
	public void setPerson(Card person) throws IllegalArgumentException {
		if (person != null && person.getKind() != Kind.PERSON) {
			throw new IllegalArgumentException();
		}
		this.person = person;
	}

	/**
	 * @return the weapon
	 */
	public Card getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon the weapon to set
	 * @throws IllegalArgumentException if weapon has not kind Kind.WEAPON
	 */
	public void setWeapon(Card weapon) throws IllegalArgumentException {
		if (weapon != null && weapon.getKind() != Kind.WEAPON) {
			throw new IllegalArgumentException();
		}
		this.weapon = weapon;
	}

	/**
	 * @return the room
	 */
	public Card getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 * @throws IllegalArgumentException if room has not kind Kind.ROOM
	 */
	public void setRoom(Card room) throws IllegalArgumentException {
		if (room != null && room.getKind() != Kind.ROOM) {
			throw new IllegalArgumentException();
		}
		this.room = room;
	}
	
	/**
	 * Returns true if none of the cards are null.
	 * 
	 * @return whether one of the cards are null
	 */
	public boolean isComplete() {
		return person != null && weapon != null && room != null; 
	}
	
	/**
	 * Use the given card in the suggestion. Calls setRoom(), setWeapon() or
	 * setPerson().
	 * 
	 * @param card
	 * @throws NullPointerException if card is null
	 */
	public void setCard(Card card) {
		switch (card.getKind()) {
		case PERSON:
			setPerson(card);
			break;
		case WEAPON:
			setWeapon(card);
			break;
		case ROOM:
			setRoom(card);
			break;
		}
	}
	
	/**
	 * Get a random suggestion from all possible cards.
	 * 
	 * @return a suggestion randomly chosen from all possible cards 
	 */
	static public Suggestion getRandomSuggestion() {
		return getRandomSuggestion(Card.generateAllCards());
	}
	
	/**
	 * Get a random suggestion from the given set of cards. Remove the chosen
	 * cards from the set.
	 * 
	 * @return a suggestion randomly chosen from the given set of cards 
	 */
	static public Suggestion getRandomSuggestion(List<Card> cards) {
		Suggestion sugg = new Suggestion();
		while (!sugg.isComplete()) {
			final int index = (int) (Math.random()*cards.size());
			boolean addCard = false;
			switch (cards.get(index).getKind()) {
			case PERSON:
				if (sugg.getPerson() == null) {
					addCard = true;
				}
				break;
			case WEAPON:
				if (sugg.getWeapon() == null) {
					addCard = true;
				}
				break;
			case ROOM:
				if (sugg.getRoom() == null) {
					addCard = true;
				}
				break;
			}
			if (addCard) {
				sugg.setCard(cards.get(index));
				cards.remove(index);
			}
		}
		return sugg;
	}
}
