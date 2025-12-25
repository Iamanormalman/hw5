public class EquipmentManager {
    public static Equipment createEquipment(String name){
        return switch (name.toLowerCase()) {
            case "armor" -> new Armor();
            case "hat" -> new Hat();
            case "knife" -> new Knife();
            case "mask" -> new Mask();
            case "force" -> new Force();
            case "shield" -> new Shield();
            default -> null;
        };
    }
}