public class Equipment {
    private String name;
    private int hpBonus;
    private int atkBonus;
    private int defBonus;

    public Equipment(String name, int hpBonus, int atkBonus, int defBonus){
        this.name = name;
        this.hpBonus = hpBonus;
        this.atkBonus = atkBonus;
        this.defBonus = defBonus;
    }

    public String getName(){
        return name;
    }

    public int getAtkBonus(){
        return atkBonus;
    }

    public int getDefBonus(){
        return defBonus;
    }

    public int getHpBonus(){
        return hpBonus;
    }
}
