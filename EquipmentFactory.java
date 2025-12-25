public class EquipmentFactory {
    public static Equipment createEquipment(String name){
        switch (name.toLowerCase()){
            case "armor": return new Armor();
            case "hat": return new Hat();
            case "knife": return new Knife();
            case "mask": return new Mask();
            case "force": return new Force();
            case "shield": return new Shield();
            default: return null;
        }
    }
}