public class DefensePotion extends Potion{
    public DefensePotion(){
        super("defense potion");
    }

    @Override
    public void use(Card target){
        target.addTempDefense(20);
        System.out.println(target.getName() + " uses defense potion.");
    }
}
