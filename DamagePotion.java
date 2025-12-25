public class DamagePotion extends Potion{
    public DamagePotion(){
        super("damage potion");
    }

    @Override
    public void use(Card target){
        target.addTempAttack(30);
        System.out.println(target.getName() + " uses damage potion.");
    }
}
