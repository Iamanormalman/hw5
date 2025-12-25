public class Equipment {
    private String name;
    private int hpBonus;
    private int atkBouns;
    private int defBouns;

    public Equipment(String name, int hpBonus, int atkBouns, int defBouns){
        this.name = name;
        this.hpBonus = hpBonus;
        this.atkBouns = atkBouns;
        this.defBouns = defBouns;
    }

    public String getName(){
        return name;
    }

    public int getAtkBonus(){
        return atkBouns;
    }

    public int getDefBonus(){
        return defBouns;
    }

    public int getHpBonus(){
        return hpBonus;
    }
}
