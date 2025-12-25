public class Assassin extends Card {
    public Assassin() {
        super("Assassin", 150, 70, 20, 3);
    }

    @Override
    public void useSkill(Player own, Player opponent) {
        opponent.takeDamage(50);

        System.out.println(opponent.getName() + " has " + opponent.getHp() + " life points left");
    }
}