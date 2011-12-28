package control.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import control.Card;
import control.Player;

/**
 * This class maintains the knowledge about hand cards of other players. It
 * stores a list of certain and possible hand cards and notifies its observers
 * whenever a new hand card is certainly derived. Observers are other instances
 * of PlayerAssumption or the SearchSpace. The methods removePossibleCard() and
 * addCertainCard() contain some simple hard-coded logic for direct conclusions
 * (for example if all hand cards are known, there are no possible hand cards
 * anymore).
 * 
 * @see control.Player
 * @see #removePossibleCard(Card)
 * @see #addCertainHandCard(Card)
 * @see java.util.Observable
 */
public class PlayerAssumption extends Observable implements Observer {
	
	private Player player;
	private Set<Card> possibleHandCards;
	private Set<Card> certainHandCards;
	
	/* (non-javadoc)
	 * This CNF represents the knowledge base for the hand cards of this
	 * player. Note that one literal clauses are automatically removed
	 * (that means removed from possibleHandCards or added to certainHandCards)
	 */
	private CNF<Card> kb;
	
	/**
	 * Sole constructor. Initialize internal data structures. 
	 * 
	 * @param player associated player instance
	 * @throws NullPointerException if player is null
	 */
	public PlayerAssumption(Player player) throws NullPointerException {
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
		this.possibleHandCards = new HashSet<Card>();
		this.certainHandCards = new HashSet<Card>();
		this.possibleHandCards.addAll(Card.generateAllCards());
		this.kb = new CNF<Card>();
	}
	
	/**
	 * Remove a card from the set of possible cards. If this set does not
	 * contain this card, do nothing. If after adding this card, we have as 
	 * much possible cards as free player's hand card slots, add them to
	 * certainHandCards and remove them from possible hand cards.
	 * 
	 * @param card card to remove
	 * @throws NullPointerException if card is null
	 */
	public void removePossibleCard(Card card) throws NullPointerException {
		if (card == null) {
			throw new NullPointerException();
		}
		this.possibleHandCards.remove(card);
		if (this.certainHandCards.size() + this.possibleHandCards.size() 
				== this.player.countHandCards()) {
			this.certainHandCards.addAll(possibleHandCards);
			possibleHandCards.clear();
			// Notify about more cards than necessary, but otherwise we have
			// conflicts with the removal mechanism in addCertainHandCard()
			for (Card certainCard : certainHandCards) {
				this.setChanged();
				this.notifyObservers(certainCard);
			}
		}
		
		kb.addNewFact(new Literal<Card>(card, false));
		/* TODO
		List<Literal<Card>> facts = kb.getNewFacts();
		while (!facts.isEmpty()) {
			List<Literal<Card>> addFacts = new ArrayList<Literal<Card>>();
			for (Literal<Card> l : facts) {
				if (l.getSign()) {
					// we have found new certain hand card
					addCertainHandCard(l.getValue());
					addFacts.addAll(kb.getNewFacts());
				}
			}
			facts.addAll(addFacts);
		}*/
	}
	
	/**
	 * Add a certain hand card. Assert that this card is a possible hand card.
	 * If after adding this card we have as much certain cards as player hand
	 * cards, remove all possible hand cards.
	 *  
	 * @param card
	 * @throws NullPointerException if card is null
	 */
	public void addCertainHandCard(Card card) throws NullPointerException {
		if (card == null) {
			throw new NullPointerException();
		}
		if (!this.possibleHandCards.contains(card)) {
			return; // Already added / not possible
		}
		this.certainHandCards.add(card);
		//this.removePossibleCard(card); <- not necessary
		this.possibleHandCards.remove(card);
		this.setChanged();
		this.notifyObservers(card);
		if (this.certainHandCards.size() == this.player.countHandCards()) {
			this.possibleHandCards.clear();
		}
		
		// remove all clauses with card = true
		kb.addNewFact(card, true);
	}
	
	/**
	 * This method is called whenever a certain card is added somewhere else.
	 * We can then exclude this card from our possible cards pool.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * @throws NullPointerException if arg is NULL
	 * @throws IllegalArgumentException if arg is not a Card
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg == null) {
			throw new NullPointerException();
		}
		if (!(arg instanceof Card)) {
			throw new IllegalArgumentException();
		}
		this.removePossibleCard((Card) arg);
	}

	/**
	 * Returns if this player is fully explored, meaning that we know each of
	 * his/her hand cards.
	 * 
	 * @return TRUE if we know all hand cards, FALSE otherwise
	 */
	public boolean isFullyExplored() {
		return possibleHandCards.isEmpty();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the knowledge base for players hand cards 
	 */
	public CNF<Card> getKb() {
		return kb;
	}

	/**
	 * @return the possibleHandCards
	 */
	public Set<Card> getPossibleHandCards() {
		return possibleHandCards;
	}

	/**
	 * @return the certainHandCards
	 */
	public Set<Card> getCertainHandCards() {
		return certainHandCards;
	}
}
