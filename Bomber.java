public class Bomber extends Card {
    public Bomber() {
        super("Bomber", 999, 1, 20, 3);
    }

    @Override
    public void useSkill(Player own, Player opponent) {
        int myIndex = -1;
        Card[] myField = own.getField();
        for (int i = 1; i <= 3; i++) {
            if (myField[i] == this) {
                myIndex = i;
                break;
            }
        }

        Card[] oppField = opponent.getField();
        Card target = oppField[myIndex];

        if (target != null) {
            System.out.println("In area " + myIndex + ": " + own.getName() + "'s bomber use skill");

            this.hp = 0;
            target.takeDamage(target.getHp());

            System.out.println("area " + myIndex + ": " + own.getName() + "'s none");
            System.out.println("area " + myIndex + ": " + opponent.getName() + "'s none");
        }
    }
}