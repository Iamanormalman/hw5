public abstract class Card {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attackPower;
    protected int defensePower;
    protected int cost;
    protected int tempAttackBonus;
    protected int tempDefenseBonus;
    protected boolean usedPotionThisTurn;
    private Equipment equipment;

    public Card(String name, int hp, int attackPower,int defensePower, int cost){
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.cost = cost;
    }

    public abstract void useSkill(Player own, Player opponent);

    public void attack(Player opponent, int position){
        Card target = opponent.getCard(position);
        int finalDamage = this.attackPower;

        if(Math.random() < 0.1){
            finalDamage *= 2;
            System.out.println("[Critical Hit!]");
        }
        if(target!=null){
            int dmg = finalDamage - target.getDefensePower();
            if (dmg < 0) dmg = 0;
            target.takeDamage(dmg);
        }else{
            opponent.takeDamage(finalDamage);
        }
    }

    public void takeDamage(int damage){
        this.hp -= damage;
    }

    public void heal(int amount){
        this.hp += amount;
        if(this.hp >= this.maxHp){
            this.hp = this.maxHp;
        }
    }

    public void equip(Equipment equipment) {
        if (this.equipment != null) {
            System.out.println("Error: Already equipped.");
            return;
        }
        this.equipment = equipment;

        this.maxHp += equipment.getHpBonus();
        this.hp += equipment.getHpBonus();
        this.attackPower += equipment.getAtkBonus();
        this.defensePower += equipment.getDefBonus();
    }

    public void addTempAttack(int amount){
        this.tempAttackBonus += amount;
    }

    public void addTempDefense(int amount){
        this.tempDefenseBonus += amount;
    }

    public boolean hasUsedPotion() {
        return this.usedPotionThisTurn;
    }

    public void setUsedPotion(boolean used) {
        this.usedPotionThisTurn = used;
    }

    public void resetTurnState() {
        this.usedPotionThisTurn = false;
        this.tempAttackBonus = 0;
        this.tempDefenseBonus = 0;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public boolean isDead(){
        return this.hp <= 0;
    }

    public String getName() {
        return name;
    }

    public int getAttackPower() {
        return attackPower + tempAttackBonus;
    }

    public int getDefensePower() {
        return defensePower + tempDefenseBonus;
    }

    public int getCost() {
        return cost;
    }

    public int getHp() {
        return hp;
    }

    public Equipment getEquipment() {
        return equipment;
    }
}