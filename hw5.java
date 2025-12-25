import java.io.*;
import java.util.*;

public class hw5 {

    private Player p1;
    private Player p2;
    private int currentRound;
    private final Scanner scanner;

    public hw5() {
        scanner = new Scanner(System.in);
    }

    // 初始化：讀取檔案並建立遊戲狀態
    public void init(String playerInfoPath, String playgroundPath) {
        try {
            // 讀取 player_info.txt
            BufferedReader brInfo = new BufferedReader(new FileReader(playerInfoPath));
            String line = brInfo.readLine();
            if (line != null) {
                this.currentRound = Integer.parseInt(line.trim());
            }

            // 讀取 P1
            p1 = parsePlayerInfo(brInfo);
            // 讀取 P2
            p2 = parsePlayerInfo(brInfo);
            brInfo.close();

            // 讀取 playground.txt
            BufferedReader brPlay = new BufferedReader(new FileReader(playgroundPath));
            // 讀取 P1 場地
            parsePlayground(brPlay, p1);
            // 讀取 P2 場地
            parsePlayground(brPlay, p2);
            brPlay.close();

            System.out.println("Game Initialized.");

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
    }

    private Player parsePlayerInfo(BufferedReader br) throws IOException {
        // 第1行 : Name, HP, Energy
        String line1 = br.readLine();
        String[] parts1 = line1.split(",");
        String name = parts1[0].trim();
        int hp = Integer.parseInt(parts1[1].trim());
        int energy = Integer.parseInt(parts1[2].trim());

        Player player = new Player(name, hp, energy);

        // 第2行 : Hand (Priest, Tank, Shooter, Assassin, Businessman, Bomber)
        String line2 = br.readLine();
        String[] parts2 = line2.split(",");
        int nPriest = Integer.parseInt(parts2[0].trim());
        int nTank = Integer.parseInt(parts2[1].trim());
        int nShooter = Integer.parseInt(parts2[2].trim());
        int nAssassin = Integer.parseInt(parts2[3].trim());
        int nBusinessman = Integer.parseInt(parts2[4].trim());
        int nBomber = Integer.parseInt(parts2[5].trim());

        for (int i = 0; i < nPriest; i++) player.addHandCard(new Priest());
        for (int i = 0; i < nTank; i++) player.addHandCard(new Tank());
        for (int i = 0; i < nShooter; i++) player.addHandCard(new Shooter());
        for (int i = 0; i < nAssassin; i++) player.addHandCard(new Assassin());
        for (int i = 0; i < nBusinessman; i++) player.addHandCard(new Businessman());
        for (int i = 0; i < nBomber; i++) player.addHandCard(new Bomber());

        // 第3行 : Equipment (Armor, Hat, Knife, Mask, Force, Shield)
        String line3 = br.readLine();
        String[] parts3 = line3.split(",");
        String[] equipNames = {"armor", "hat", "knife", "mask", "force", "shield"};
        for (int i = 0; i < parts3.length && i < equipNames.length; i++) {
            int count = Integer.parseInt(parts3[i].trim());
            player.addEquipment(equipNames[i], count);
        }

        // 第4行 : Potion (Healing, Damage, Defense)
        String line4 = br.readLine();
        String[] parts4 = line4.split(",");
        String[] potionNames = {"healing potion", "damage potion", "defense potion"};
        for (int i = 0; i < parts4.length && i < potionNames.length; i++) {
            int count = Integer.parseInt(parts4[i].trim());
            player.addPotion(potionNames[i], count);
        }

        return player;
    }

    private void parsePlayground(BufferedReader br, Player player) throws IOException {
        String nameLine = br.readLine();
        if (nameLine == null || !nameLine.trim().equals(player.getName())) {
            throw new IOException("File format error: Expected player name '" + player.getName() + "', but found '" + nameLine + "'");
        }

        for (int i = 1; i <= 3; i++) {
            String line = br.readLine();
            if (line == null) {
                throw new IOException("Unexpected end of file while reading playground for " + player.getName());
            }

            String[] parts = line.split(",");
            String cardName = parts[1].trim();

            if (!cardName.equals("none")) {
                int hp = Integer.parseInt(parts[2].trim());
                int atk = Integer.parseInt(parts[3].trim());
                int def = Integer.parseInt(parts[4].trim());

                Card card = createCard(cardName);
                if (card != null) {
                    // 設定基本數值
                    if (card.getHp() > hp) {
                        // 如果 createCard 預設是滿血，就要扣血
                        card.takeDamage(card.getHp() - hp);
                    } else if (card.getHp() < hp) {
                        // 如果讀到的血量比預設高，就要補血
                        card.heal(hp - card.getHp());
                    }

                    // 設定場上位置
                    player.setFieldCard(i, card);

                    // 處理裝備或藥水
                    for (int j = 5; j < parts.length; j++) {
                        String item = parts[j].trim();
                        // 判斷是裝備還是藥水
                        Equipment equip = createEquipment(item);
                        if (equip != null) {
                            card.equip(equip);
                        } else if (item.contains("potion")) {
                            card.setUsedPotion(true);
                        }
                    }
                }
            }
        }
    }

    private Card createCard(String name) {
        if (name.equalsIgnoreCase("Priest")) return new Priest();
        if (name.equalsIgnoreCase("Tank")) return new Tank();
        if (name.equalsIgnoreCase("Shooter")) return new Shooter();
        if (name.equalsIgnoreCase("Assassin")) return new Assassin();
        if (name.equalsIgnoreCase("Businessman")) return new Businessman();
        if (name.equalsIgnoreCase("Bomber")) return new Bomber();
        return null;
    }

    // 簡單的裝備工廠方法
    private Equipment createEquipment(String name) {
        String n = name.toLowerCase();
        return switch (n) {
            case "armor" -> new Armor();
            case "hat" -> new Hat();
            case "knife" -> new Knife();
            case "mask" -> new Mask();
            case "force" -> new Force();
            case "shield" -> new Shield();
            default -> null;
        };
    }

    // 遊戲主流程
    public void start() {
        System.out.println("Welcome to the Card Battle Game!");

        while (true) {
            System.out.println("\n--- Round " + currentRound + " ---");

            // 回合開始：重置所有卡牌的單回合狀態
            resetCardsForNewTurn(p1, p2);

            showGameStatus();

            // 佈局階段
            runLayoutStage(p1);
            runLayoutStage(p2);

            // 佈局結束後顯示一次資訊
            showGameStatus();

            // 技能階段
            runSkillStage(p1, p2);
            runSkillStage(p2, p1);

            // 技能結束後顯示一次資訊
            showGameStatus();

            // 攻擊階段 (P1 先)
            runAttackStage(p1, p2);
            runAttackStage(p2, p1);

            // 勝負判定階段
            if (checkWinner()) {
                break; // 遊戲結束
            }

            // 存檔與下一回合
            while (true) {
                System.out.print("Do you want to save the game? (Y/N): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("Y")) {
                    saveGame(currentRound + 1);
                    System.out.println("Game saved. Exiting.");
                    return;
                } else if (input.equalsIgnoreCase("N")) {
                    currentRound++;
                    break;
                } else {
                    System.out.println("Please enter Y/y or N/n");
                }
            }
        }
    }

    private void resetCardsForNewTurn(Player p1, Player p2) {
        for (int i = 1; i <= 3; i++) {
            if (p1.getCard(i) != null) p1.getCard(i).resetTurnState();
            if (p2.getCard(i) != null) p2.getCard(i).resetTurnState();
        }
    }

    // 佈局階段
    private void runLayoutStage(Player player) {
        // 恢復能量
        player.setEnergy(currentRound);

        System.out.println(player.getName() + "'s Layout Stage:");

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");

            if (parts.length == 0) continue;

            String command = parts[0];

            if (command.equals("put")) {
                if (parts.length < 3) {
                    System.out.println("Error: Invalid command format.");
                    continue;
                }

                // 判斷是否為 healing potion (長度為 4: put healing potion pos)
                if (parts[1].equals("healing") && parts[2].equals("potion") && parts.length == 4) {
                    int pos = parsePos(parts[3]);
                    if (pos != -1) {
                        if (player.usePotion("healing potion", pos)) {
                            System.out.println("Used healing potion on position " + pos);
                        }
                    }
                }
                // 判斷其他藥水或裝備/卡牌
                else if (parts.length == 3) {
                    String item = parts[1];
                    int pos = parsePos(parts[2]);
                    if (pos == -1) continue;

                    // 嘗試放置卡牌
                    if (isCardName(item)) {
                        if (player.playCard(item, pos)) {
                            System.out.println("Successfully placed " + item + " to position " + pos);
                        }
                    }
                    // 嘗試使用裝備
                    else if (isEquipmentName(item)) {
                        if (player.useEquipment(item, pos)) {
                            System.out.println("Equipped " + item + " on position " + pos);
                        }
                    }
                    else {
                        System.out.println("Error: Unknown card or equipment '" + item + "'");
                    }
                }
                else if (parts[2].equals("potion")) {
                    // 處理 damage potion, defense potion
                    String potionName = parts[1] + " " + parts[2];
                    int pos = parsePos(parts[3]);
                    if (pos != -1) {
                        if (player.usePotion(potionName, pos)) {
                            System.out.println("Used " + potionName + " on position " + pos);
                        }
                    }
                }
                else {
                    System.out.println("Error: Invalid command.");
                }

            } else if (command.equals("show") && parts.length >= 3 && parts[1].equals("game") && parts[2].equals("status")) {
                showGameStatus();
            } else if (command.equals("end") && parts.length >= 3 && parts[1].equals("layout") && parts[2].equals("stage")) {
                break;
            } else {
                System.out.println("Error: Unknown command.");
            }
        }
    }

    private int parsePos(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Error: Position must be a number.");
            return -1;
        }
    }

    private boolean isCardName(String name) {
        String n = name.toLowerCase();
        return n.equals("priest") || n.equals("tank") || n.equals("shooter") ||
                n.equals("assassin") || n.equals("businessman") || n.equals("bomber");
    }

    private boolean isEquipmentName(String name) {
        String n = name.toLowerCase();
        return n.equals("armor") || n.equals("hat") || n.equals("knife") ||
                n.equals("mask") || n.equals("force") || n.equals("shield");
    }

    // 技能階段
    private void runSkillStage(Player current, Player opponent) {
        System.out.println(current.getName() + "'s Skill Stage:");
        // HW5 新增角色與順序: 炸彈人 > 牧師 > 坦克 > 射手 > 刺客 > 商人
        String[] order = {"Bomber", "Priest", "Tank", "Shooter", "Assassin", "Businessman"};
        Card[] field = current.getField();

        for (String type : order) {
            for (int i = 1; i <= 3; i++) {
                Card c = field[i];
                if (c != null && c.getName().equalsIgnoreCase(type) && !c.isDead()) {
                    // 注意：HW5 商人和炸彈人的技能描述可能需要不同的輸出格式，請依需求調整
                    // 這裡使用通用的 useSkill，具體輸出由 Card 子類別處理
                    c.useSkill(current, opponent);
                }
            }
        }

        // 清理已死亡的卡牌
        current.removeDeadCards();
        opponent.removeDeadCards();
    }

    // 攻擊階段
    private void runAttackStage(Player current, Player opponent) {
        System.out.println(current.getName() + "'s Attack Stage:");
        Card[] field = current.getField();

        for (int i = 1; i <= 3; i++) {
            Card attacker = field[i];
            if (attacker != null) {
                Card target = opponent.getCard(i);

                if (target != null) {
                    // 攻擊卡牌
                    attacker.attack(opponent, i);
                    int hpLeft = Math.max(target.getHp(), 0);
                    System.out.printf("In area %d: %s(%s)'s %s attack %s(%s)'s %s. The %s has %d life points left.\n",
                            i, current.getName(), "P" + (current == p1 ? "1" : "2"), attacker.getName(),
                            opponent.getName(), "P" + (opponent == p1 ? "1" : "2"), target.getName(),
                            target.getName(), hpLeft);
                } else {
                    // 攻擊玩家
                    attacker.attack(opponent, i);
                    int hpLeft = Math.max(opponent.getHp(), 0);
                    System.out.printf("In area %d: %s(%s)'s %s attack %s(%s). %s has %d life points left.\n",
                            i, current.getName(), "P" + (current == p1 ? "1" : "2"), attacker.getName(),
                            opponent.getName(), "P" + (opponent == p1 ? "1" : "2"),
                            opponent.getName(), hpLeft);
                }
            }
        }
        current.removeDeadCards();
        opponent.removeDeadCards();
    }

    // 顯示遊戲狀態
    private void showGameStatus() {
        String border = "==========================================================================================";
        String divider = "------------------------------------------------------------------------------------------";

        System.out.println("\n" + border);
        System.out.printf("%45s %d%n", "ROUND", currentRound);
        System.out.println(border);

        printDualColumn(
                String.format("P1: %s [Energy: %d]", p1.getName(), p1.getEnergy()),
                String.format("P2: %s [Energy: %d]", p2.getName(), p2.getEnergy())
        );
        printDualColumn(
                String.format("HP: %d", p1.getHp()),
                String.format("HP: %d", p2.getHp())
        );

        System.out.println(divider);
        printDualColumn("[Field Area]", "[Field Area]");

        Card[] f1 = p1.getField();
        Card[] f2 = p2.getField();

        for (int i = 1; i <= 3; i++) {
            printDualColumn(formatFieldCard(f1[i], i), formatFieldCard(f2[i], i));
        }

        System.out.println(divider);

        printDualColumn("[Hand]", "[Hand]");
        printDualColumn(getHandString(p1), getHandString(p2));

        // 顯示裝備與藥水庫存
        printDualColumn(getInventoryString(p1), getInventoryString(p2));

        System.out.println(border + "\n");
    }

    private String formatFieldCard(Card c, int pos) {
        if (c == null) return "Pos " + pos + ": [ Empty ]";

        String info = String.format("%s (HP:%d/ATK:%d/DEF:%d)",
                c.getName(), c.getHp(), c.getAttackPower(), c.getDefensePower());

        if (c.getEquipment() != null) {
            info += " [" + c.getEquipment().getName() + "]";
        }
        if (c.hasUsedPotion()) {
            info += " [Potion]";
        }
        return "Pos " + pos + ": " + info;
    }

    private void printDualColumn(String left, String right) {
        System.out.printf("%-40s | %-40s\n", left, right);
    }

    private String getHandString(Player p) {
        List<Card> hand = p.getHand();
        if (hand.isEmpty()) return "Cards: None";
        StringBuilder sb = new StringBuilder("Cards: ");
        for (Card c : hand) {
            sb.append(c.getName()).append(" ");
        }
        return sb.toString();
    }

    // 顯示裝備與藥水庫存字串
    private String getInventoryString(Player p) {
        StringBuilder sb = new StringBuilder();
        // 裝備
        Map<String, Integer> equips = p.getEquipmentInventory();
        boolean first = true;
        for (String key : equips.keySet()) {
            if (equips.get(key) > 0) {
                if (!first) sb.append(", ");
                sb.append(key).append("*").append(equips.get(key));
                first = false;
            }
        }
        if (sb.isEmpty()) sb.append("Equips: None");
        else sb.insert(0, "Equips: ");

        sb.append(" | ");

        // 藥水
        Map<String, Integer> potions = p.getPotionInventory();
        StringBuilder sb2 = new StringBuilder();
        first = true;
        for (String key : potions.keySet()) {
            if (potions.get(key) > 0) {
                if (!first) sb2.append(", ");
                sb2.append(key).append("*").append(potions.get(key));
                first = false;
            }
        }
        if (sb2.isEmpty()) sb2.append("Potions: None");
        else sb2.insert(0, "Potions: ");

        return sb.append(sb2).toString();
    }

    // 勝負判定
    private boolean checkWinner() {
        boolean p1Dead = p1.getHp() <= 0;
        boolean p2Dead = p2.getHp() <= 0;

        if (p1Dead && p2Dead) {
            System.out.println("Draw! Both players died.");
            return true;
        } else if (p1Dead) {
            System.out.println(p2.getName() + " wins!");
            return true;
        } else if (p2Dead) {
            System.out.println(p1.getName() + " wins!");
            return true;
        }
        return false;
    }

    // 存檔
    private void saveGame(int nextRound) {
        try {
            PrintWriter pwInfo = new PrintWriter(new FileWriter("player_info.txt"));
            pwInfo.println(nextRound);
            savePlayerInfo(pwInfo, p1);
            savePlayerInfo(pwInfo, p2);
            pwInfo.close();

            PrintWriter pwPlay = new PrintWriter(new FileWriter("playground.txt"));
            savePlayground(pwPlay, p1);
            savePlayground(pwPlay, p2);
            pwPlay.close();

        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    private void savePlayerInfo(PrintWriter pw, Player p) {
        pw.println(p.getName() + "," + p.getHp() + "," + p.getEnergy());

        // 第2行: Card counts (Priest, Tank, Shooter, Assassin, Businessman, Bomber)
        int[] counts = new int[6];
        for (Card c : p.getHand()) {
            if (c instanceof Priest) counts[0]++;
            else if (c instanceof Tank) counts[1]++;
            else if (c instanceof Shooter) counts[2]++;
            else if (c instanceof Assassin) counts[3]++;
            else if (c instanceof Businessman) counts[4]++;
            else if (c instanceof Bomber) counts[5]++;
        }
        pw.println(counts[0] + "," + counts[1] + "," + counts[2] + "," + counts[3] + "," + counts[4] + "," + counts[5]);

        // 第3行: Equipment counts
        Map<String, Integer> equips = p.getEquipmentInventory();
        String[] equipNames = {"armor", "hat", "knife", "mask", "force", "shield"};
        StringBuilder sbEquip = new StringBuilder();
        for (int i = 0; i < equipNames.length; i++) {
            sbEquip.append(equips.getOrDefault(equipNames[i], 0));
            if (i < equipNames.length - 1) sbEquip.append(",");
        }
        pw.println(sbEquip);

        // 第4行: Potion counts
        Map<String, Integer> potions = p.getPotionInventory();
        String[] potionNames = {"healing potion", "damage potion", "defense potion"};
        StringBuilder sbPotion = new StringBuilder();
        for (int i = 0; i < potionNames.length; i++) {
            sbPotion.append(potions.getOrDefault(potionNames[i], 0));
            if (i < potionNames.length - 1) sbPotion.append(",");
        }
        pw.println(sbPotion);
    }

    private void savePlayground(PrintWriter pw, Player p) {
        pw.println(p.getName());
        Card[] field = p.getField();
        for (int i = 1; i <= 3; i++) {
            Card c = field[i];
            if (c == null) {
                pw.println(i + ",none");
            } else {
                // index, name, hp, atk, def
                StringBuilder line = new StringBuilder();
                line.append(i).append(",")
                        .append(c.getName()).append(",")
                        .append(c.getHp()).append(",")
                        .append(c.getAttackPower()).append(",")
                        .append(c.getDefensePower());

                // append equipment
                if (c.getEquipment() != null) {
                    line.append(",").append(c.getEquipment().getName());
                }

                pw.println(line);
            }
        }
    }

    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Usage: java hw5 player_info.txt playground.txt");
            return;
        }
        hw5 game = new hw5();
        game.init(args[0], args[1]);
        game.start();
    }
}