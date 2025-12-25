import java.util.ArrayList;
import java.util.List;

public class Player{
    private final String name;
    private final List<Card> hand;
    private final Card[] field;
    private int hp;
    private int energy;


    public Player(String name, int hp, int energy){
        this.name = name;
        this.hp = hp;
        this.energy = energy;
        this.hand = new ArrayList<>();
        this.field = new Card[4];
    }

    public boolean playCard(String cardName, int position){
        if(position < 1 || position > 3){
            System.out.println("Error: Invalid position.");
            return false;
        }

        if(field[position] != null){
            System.out.println("Error: Position " + position + " is already occupied.");
            return false;
        }

        Card targetCard = null;
        for(Card c : hand){
            if(c.getName().equalsIgnoreCase(cardName)){
                targetCard = c;
                break;
            }
        }

        if(targetCard == null){
            System.out.println("Error: You don't have " + cardName + " in your hand.");
            return false;
        }

        if(this.energy < targetCard.getCost()){
            System.out.println("Error: Not enough energy.");
            return false;
        }

        this.energy -= targetCard.getCost();
        hand.remove(targetCard);
        field[position] = targetCard;
        return true;
    }

    public void takeDamage(int damage){
        this.hp -= damage;
    }

    public Card getCard(int position){
        if(position >= 1 && position <= 3){
            return field[position];
        }
        return null;
    }

    public void setFieldCard(int position, Card card){
        if(position >= 1 && position <= 3){
            field[position] = card;
        }
    }

    public void addHandCard(Card card){
        if (hand.size() <= 4){
            hand.add(card);
        }
    }

    public void removeDeadCards() {
        for (int i = 1; i <= 3; i++) {
            if (field[i] != null && field[i].isDead()) {
                field[i] = null;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card[] getField() {
        return field;
    }
}