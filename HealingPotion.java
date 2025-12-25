public class HealingPotion extends Potion{
    public HealingPotion(){
        super("healing potion");
    }

    @Override
    public void use(Card target){
        target.heal(50);
        System.out.println(target + " uses healing potion.");
    }
}
