public class Tank extends Card {
    public Tank() {
        super("Tank", 300, 60, 20, 2);
    }

    @Override
    public void useSkill(Player own, Player opponent) {
        // 回復自身 20 點
        this.heal(20);

        int myPosition = -1;
        Card[] field = own.getField();
        for (int i = 1; i <= 3; i++) {
            if (field[i] == this) {
                myPosition = i;
                break;
            }
        }

        if (myPosition != -1) {
            System.out.println("area " + myPosition + ": " + own.getName() + "'s " + this.getName() + "'s life restored to " + this.getHp());
        }
    }
}