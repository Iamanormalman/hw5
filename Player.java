import java.util.*;

public class Player{
    private final String name;
    private final List<Card> hand;
    private final Map<String, Integer> equipmentInventory;
    private final Map<String, Integer> potionInventory;
    private final Card[] field;
    private int hp;
    private int energy;


    public Player(String name, int hp, int energy){
        this.name = name;
        this.equipmentInventory = new HashMap<>();
        this.potionInventory = new HashMap<>();
        this.hp = hp;
        this.energy = energy;
        this.hand = new ArrayList<>();
        this.field = new Card[4];
    }

    public boolean playCard(String cardName, int position){
        if(position < 1 || position > 3){
            System.out.println("Error: Invalid position.");
            return false;
        }

        if(field[position] != null){
            System.out.println("Error: Position " + position + " is already occupied.");
            return false;
        }

        Card targetCard = null;
        for(Card c : hand){
            if(c.getName().equalsIgnoreCase(cardName)){
                targetCard = c;
                break;
            }
        }

        if(targetCard == null){
            System.out.println("Error: You don't have " + cardName + " in your hand.");
            return false;
        }

        if(this.energy < targetCard.getCost()){
            System.out.println("Error: Not enough energy.");
            return false;
        }

        this.energy -= targetCard.getCost();
        hand.remove(targetCard);
        field[position] = targetCard;
        return true;
    }

    public void takeDamage(int damage){
        this.hp -= damage;
    }

    public Card getCard(int position){
        if(position >= 1 && position <= 3){
            return field[position];
        }
        return null;
    }

    public void setFieldCard(int position, Card card){
        if(position >= 1 && position <= 3){
            field[position] = card;
        }
    }

    public void addHandCard(Card card){
        if (hand.size() <= 10){
            hand.add(card);
        }
    }

    public void removeDeadCards() {
        for (int i = 1; i <= 3; i++) {
            if (field[i] != null && field[i].isDead()) {
                field[i] = null;
            }
        }
    }

    public void addEquipment(String name, int count){
        equipmentInventory.put(name.toLowerCase(), count);
    }

    public void addPotion(String name, int count){
        potionInventory.put(name.toLowerCase(), count);
    }

    // 回傳 boolean 代表是否成功，方便主程式顯示訊息
    public boolean useEquipment(String equipName, int position) {
        String key = equipName.toLowerCase();

        // 1. 檢查庫存
        if (!equipmentInventory.containsKey(key) || equipmentInventory.get(key) <= 0) {
            System.out.println("Error: You don't have " + equipName);
            return false;
        }

        // 2. 檢查位置與目標
        Card target = getCard(position);
        if (target == null) {
            System.out.println("Error: No card at position " + position);
            return false;
        }

        // 3. 檢查是否已裝備 (需要在 Card 類別實作 getEquipment())
        if (target.getEquipment() != null) {
            System.out.println("Error: Target already has equipment.");
            return false;
        }

        // 4. 執行裝備
        Equipment equip = EquipmentFactory.createEquipment(key); // 假設你有實作 Factory
        if (equip != null) {
            target.equip(equip); // 呼叫 Card 的 equip 方法

            // 扣除庫存
            equipmentInventory.put(key, equipmentInventory.get(key) - 1);
            return true;
        }
        return false;
    }

    public boolean usePotion(String potionName, int position){
        String key = potionName.toLowerCase();

        // 1. 檢查庫存
        if (!potionInventory.containsKey(key) || potionInventory.get(key) <= 0){
            System.out.println("Error: You don't have " + potionName);
            return false;
        }

        // 2. 檢查位置與目標
        Card target = getCard(position);
        if (target == null) {
            System.out.println("Error: No card at position " + position);
            return false;
        }

        // 3. 檢查本回合是否已使用 (假設 Card 有 usedPotionThisTurn 屬性)
        /* 注意：需要在 Card.java 新增方法 hasUsedPotion()
           和 setUsedPotion(boolean)
        */
        if (target.hasUsedPotion()) {
            System.out.println("Error: This card already used a potion this turn.");
            return false;
        }

        // 4. 執行藥水效果
        Potion potion = null;
        // 簡單判斷或使用 Factory
        if (key.contains("healing")) potion = new HealingPotion();
        else if (key.contains("damage")) potion = new DamagePotion();
        else if (key.contains("defense")) potion = new DefensePotion();

        if (potion != null) {
            potion.use(target); // 多型呼叫
            target.setUsedPotion(true); // 標記已使用

            // 扣除庫存
            potionInventory.put(key, potionInventory.get(key) - 1);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card[] getField() {
        return field;
    }

    public Map<String, Integer> getEquipmentInventory() {return equipmentInventory;}

    public Map<String, Integer> getPotionInventory() {return potionInventory;}
}