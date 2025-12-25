public abstract class Card {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attackPower;
    protected int defensePower;
    protected int cost;

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
            target.takeDamage(finalDamage);
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
        return attackPower;
    }

    public int getCost() {
        return cost;
    }

    public int getHp() {
        return hp;
    }
}