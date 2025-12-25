public class Priest extends Card{
    public Priest(){
        super("Priest", 200, 50, 20, 2);
    }

    @Override
    public void useSkill(Player own, Player opponent) {
        Card[] field = own.getField();
        for (int i = 1; i <= 3; i++) {
            Card target = field[i];
            if (target != null && !target.isDead()) {
                target.heal(10);

                System.out.println("area " + i + ": " + own.getName() + "'s " + target.getName() + "'s life restored to " + target.getHp());
            }
        }
    }}