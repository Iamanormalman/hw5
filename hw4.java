import java.io.*;
import java.util.*;

public class hw4 {

    private Player p1;
    private Player p2;
    private int currentRound;
    private final Scanner scanner;

    public hw4(){
        scanner = new Scanner(System.in);
    }

    // 初始化：讀取檔案並建立遊戲狀態
    public void init(String playerInfoPath, String playgroundPath){
        try {
            //讀取 player_info.txt
            BufferedReader brInfo = new BufferedReader(new FileReader(playerInfoPath));
            String line = brInfo.readLine();
            if (line != null){
                this.currentRound = Integer.parseInt(line.trim());
            }

            // 讀取 P1
            p1 = parsePlayerInfo(brInfo);
            // 讀取 P2
            p2 = parsePlayerInfo(brInfo);
            brInfo.close();

            //讀取 playground.txt
            BufferedReader brPlay = new BufferedReader(new FileReader(playgroundPath));
            // 讀取 P1 場地
            parsePlayground(brPlay, p1);
            // 讀取 P2 場地
            parsePlayground(brPlay, p2);
            brPlay.close();

            System.out.println("Game Initialized.");

        } catch (IOException e){
            System.out.println("Error reading files: " + e.getMessage());
            e.printStackTrace();//印出 stack trace，找出檔案讀取失敗的原因
        }
    }

    private Player parsePlayerInfo(BufferedReader br) throws IOException{
        // Line 1: Name, HP, Energy
        String line1 = br.readLine();
        String[] parts1 = line1.split(",");
        String name = parts1[0].trim();
        int hp = Integer.parseInt(parts1[1].trim());
        int energy = Integer.parseInt(parts1[2].trim());

        Player player = new Player(name, hp, energy);

        // Line 2: Hand counts (Priest, Tank, Shooter, Assassin)
        String line2 = br.readLine();
        String[] parts2 = line2.split(",");
        int nPriest = Integer.parseInt(parts2[0].trim());
        int nTank = Integer.parseInt(parts2[1].trim());
        int nShooter = Integer.parseInt(parts2[2].trim());
        int nAssassin = Integer.parseInt(parts2[3].trim());

        for (int i = 0; i < nPriest; i++) player.addHandCard(new Priest());
        for (int i = 0; i < nTank; i++) player.addHandCard(new Tank());
        for (int i = 0; i < nShooter; i++) player.addHandCard(new Shooter());
        for (int i = 0; i < nAssassin; i++) player.addHandCard(new Assassin());

        return player;
    }

    private void parsePlayground(BufferedReader br, Player player) throws IOException {
        String nameLine = br.readLine();

        // 驗證機制：1. 檢查是否讀到檔案結尾 (null) 2. 檢查讀到的名字是否與傳入的 player 物件名稱相符
        if (nameLine == null || !nameLine.trim().equals(player.getName())) {
            // 如果不符，拋出 IOException 中止程式，並帶上錯誤訊息
            throw new IOException("File format error: Expected player name '" + player.getName() +
                    "', but found '" + nameLine + "'");
        }

        for (int i = 1; i <= 3; i++) {
            String line = br.readLine();
            // 預防性檢查：避免因為檔案行數不足導致 NullPointerException
            if (line == null) {
                throw new IOException("File format error: Unexpected end of file while reading playground for " + player.getName());
            }

            String[] parts = line.split(",");
            // 格式: index, cardName, hp, atk OR index, none
            String cardName = parts[1].trim();

            if (!cardName.equals("none")) {
                int hp = Integer.parseInt(parts[2].trim());
                int atk = Integer.parseInt(parts[3].trim());

                Card card = createCard(cardName);
                if (card != null) {
                    if (card.getHp() > hp) {
                        card.takeDamage(card.getHp() - hp);
                    }
                    card.setAttackPower(atk);

                    player.setFieldCard(i, card);
                }
            }
        }
    }
    private Card createCard(String name){
        if (name.equalsIgnoreCase("Priest")) return new Priest();
        if (name.equalsIgnoreCase("Tank")) return new Tank();
        if (name.equalsIgnoreCase("Shooter")) return new Shooter();
        if (name.equalsIgnoreCase("Assassin")) return new Assassin();
        return null;
    }

    // 遊戲主流程
    public void start(){
        System.out.println("Welcome to the Card Battle Game!");

        while (true){
            System.out.println("\n--- Round " + currentRound + " ---");
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

            // 攻擊階段
            runAttackStage(p1, p2);
            runAttackStage(p2, p1);

            // 勝負判定階段
            if (checkWinner()){
                break; // 遊戲結束
            }

            // 存檔與下一回合
            while (true){
                System.out.print("Do you want to save the game? (Y/N): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("Y")){
                    saveGame(currentRound + 1);
                    System.out.println("Game saved. Exiting.");
                    return;
                } else if (input.equalsIgnoreCase("N")){
                    currentRound++;
                    break;
                } else {
                    System.out.println("Please enter Y/y or N/n");
                }
            }        }
    }

    // 佈局階段
    private void runLayoutStage(Player player){
        // 恢復能量
        player.setEnergy(currentRound);

        System.out.println(player.getName() + "'s Layout Stage:");

        //處理指令
        while (true){
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String command = parts[0];

            if (command.equals("put")){
                if (parts.length < 3){
                    System.out.println("Error: Invalid command format. Use 'put [card] [pos]'");
                    continue;
                }
                String cardName = parts[1];
                int pos;
                try {
                    pos = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Position must be a number.");
                    continue;
                }

                if (player.playCard(cardName, pos)){
                    System.out.println("Successfully placed " + cardName + "to position " + pos);
                }

            } else if (command.equals("show") && parts.length >= 3 && parts[1].equals("game") && parts[2].equals("status")){
                showGameStatus();
            } else if (command.equals("end") && parts.length >= 3 && parts[1].equals("layout") && parts[2].equals("stage")){
                break;
            } else {
                System.out.println("Error: Unknown command.");
            }
        }
    }

    // 技能階段
    private void runSkillStage(Player current, Player opponent){
        System.out.println(current.getName() + "'s Skill Stage:");
        // 順序: 牧師 > 坦克 > 射手 > 刺客
        String[] order = {"Priest", "Tank", "Shooter", "Assassin"};
        Card[] field = current.getField();

        for (String type : order){
            for (int i = 1; i <= 3; i++){
                Card c = field[i];
                if (c != null && c.getName().equalsIgnoreCase(type) && !c.isDead()){
                    System.out.println("In area " + i + ": " + current.getName() + "'s " + c.getName() + " use skill");
                    c.useSkill(current, opponent);
                }
            }
        }

        // 清理已死亡的卡牌
        current.removeDeadCards();
        opponent.removeDeadCards();
    }

    // 攻擊階段
    private void runAttackStage(Player current, Player opponent){
        System.out.println(current.getName() + "'s Attack Stage:");
        Card[] field = current.getField();

        for (int i = 1; i <= 3; i++){
            Card attacker = field[i];
            if (attacker != null){
                // 執行攻擊前先判斷目標
                Card target = opponent.getCard(i);

                if (target != null){
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
        // 攻擊結束後清理死亡卡牌
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

        // 顯示玩家資訊
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
            String leftCard = (f1[i] == null) ? "[ Empty ]" : String.format("%s (HP:%d/ATK:%d)", f1[i].getName(), f1[i].getHp(), f1[i].getAttackPower());
            String rightCard = (f2[i] == null) ? "[ Empty ]" : String.format("%s (HP:%d/ATK:%d)", f2[i].getName(), f2[i].getHp(), f2[i].getAttackPower());

            printDualColumn("Pos " + i + ": " + leftCard, "Pos " + i + ": " + rightCard);
        }

        System.out.println(divider);

        printDualColumn("[Hand]", "[Hand]");
        printDualColumn(getHandString(p1), getHandString(p2));

        System.out.println(border + "\n");
    }
    // 印出左右兩欄
    private void printDualColumn(String left, String right) {
        System.out.printf("%-40s | %-40s\n", left, right);
    }

    // 取得手牌字串
    private String getHandString(Player p) {
        List<Card> hand = p.getHand();
        if (hand.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (Card c : hand) {
            sb.append(c.getName()).append(" ");
        }
        return sb.toString();
    }

    // 勝負判定
    private boolean checkWinner(){
        boolean p1Dead = p1.getHp() <= 0;
        boolean p2Dead = p2.getHp() <= 0;

        if (p1Dead && p2Dead){
            System.out.println("Draw! Both players died.");
            return true;
        } else if (p1Dead){
            System.out.println(p2.getName() + " wins!");
            return true;
        } else if (p2Dead){
            System.out.println(p1.getName() + " wins!");
            return true;
        }

        if (currentRound >= 10){
            if (p1.getHp() > p2.getHp()){
                System.out.println(p1.getName() + " wins! (Higher HP at round 10)");
            } else if (p2.getHp() > p1.getHp()){
                System.out.println(p2.getName() + " wins! (Higher HP at round 10)");
            } else {
                System.out.println("Draw! (Same HP at round 10)");
            }
            return true;
        }

        return false;
    }

    // 存檔
    private void saveGame(int nextRound){
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

        } catch (IOException e){
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    private void savePlayerInfo(PrintWriter pw, Player p){
        pw.println(p.getName() + "," + p.getHp() + "," + p.getEnergy());
        // 數剩餘卡牌
        int priest = 0, tank = 0, shooter = 0, assassin = 0;
        for (Card c : p.getHand()){
            if (c instanceof Priest) priest++;
            else if (c instanceof Tank) tank++;
            else if (c instanceof Shooter) shooter++;
            else if (c instanceof Assassin) assassin++;
        }
        pw.println(priest + "," + tank + "," + shooter + "," + assassin);
    }

    private void savePlayground(PrintWriter pw, Player p){
        pw.println(p.getName());
        Card[] field = p.getField();
        for (int i = 1; i <= 3; i++){
            Card c = field[i];
            if (c == null){
                pw.println(i + ",none");
            } else {
                pw.println(i + "," + c.getName() + "," + c.getHp() + "," + c.getAttackPower());
            }
        }
    }

    // 主程式
    public static void main(String[] args){
        if (args.length < 2){
            System.out.println("Usage: java hw4 player_info.txt playground.txt");
            return;
        }
        hw4 game = new hw4();
        game.init(args[0], args[1]);
        game.start();
    }
}