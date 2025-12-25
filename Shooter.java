public class Shooter extends Card {
    public Shooter() {
        super("Shooter", 150, 100, 20, 3);
    }

    @Override
    public void useSkill(Player own, Player opponent) {
        Card[] enemyField = opponent.getField();

        for (int i = 1; i <= 3; i++) {
            Card target = enemyField[i];
            if (target != null) {
                target.takeDamage(10);

                System.out.println("area " + i + ": " + opponent.getName() + "'s " + target.getName() + " has " + target.getHp() + " life points left");
            }
        }
    }
}