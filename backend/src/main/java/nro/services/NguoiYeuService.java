//package nro.services;
//
//import nro.inventory.InventoryService;
//import nro.services.Fun.ChangeMapService;
//import Utils.SkillUtil;
//import Utils.Util;
//import consts.ConstFamily;
//import consts.ConstPlayer;
//import models.Item.ItemService;
//import nro.player.NguoiYeu;
//import nro.player.Player;
//import nro.skill.Skill;
//
//public class NguoiYeuService {
//
//    private static NguoiYeuService i;
//
//    public static NguoiYeuService gI() {
//        if (i == null) {
//            i = new NguoiYeuService();
//        }
//        return i;
//    }
////=====================Create Dao Lu V2 by NDQ (Zalo - 0372475179)========================
//
//    public void createDaoLu(Player player, String name, byte typeDaoLu, int gender, byte... limitPower) {
//        new Thread(() -> {
//            try {
//                createNewDaoLu(player, name, typeDaoLu, (byte) gender);
//                if (limitPower != null && limitPower.length == 1) {
//                    player.nguoiyeu.nPoint.limitPower = limitPower[0];
//                }
//                Thread.sleep(1000);
//                Service.getInstance().chatJustForMe(player, player.nguoiyeu, "Ta nguyện đi theo chàng!!");
//            } catch (Exception e) {
//            }
//        }).start();
//    }
//
//    public boolean changeDaoLu(Player player, String name, byte typeDaoLu, int gender) {
//        if (InventoryService.gI().getCountEmptyBody(player.nguoiyeu) == ConstPlayer.QTY_MAX_ITEM_BODY_PET) {
//            byte limitPower = player.nguoiyeu.nPoint.limitPower;
//            ChangeMapService.gI().exitMap(player.nguoiyeu);
//            player.nguoiyeu.dispose();
//            player.nguoiyeu = null;
//            createDaoLu(player, name, typeDaoLu, gender, limitPower);
//            return true;
//        } else {
//            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đạo lữ đang mặc");
//            return false;
//        }
//    }
//
//    private void createNewDaoLu(Player player, String name, byte typeDaoLu, byte... gender) {
//        int[] data = getDataDaoLus(typeDaoLu);
//        NguoiYeu petDaoLu = new NguoiYeu(player);
//        petDaoLu.typeNguoiYeu = (byte) typeDaoLu;
//        petDaoLu.nameDaoLu = name;
//        petDaoLu.name = "$[" + petDaoLu.getTypeString() + "] " + petDaoLu.nameDaoLu;
//        //"$" + getNameDaoLus(typeDaoLu);
//        petDaoLu.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
//        petDaoLu.id = Player.setIdForPet(petDaoLu, player.id);
//        petDaoLu.nPoint.power = typeDaoLu != 1 ? 1500000 : 2000;
//        petDaoLu.nPoint.tiemNang = typeDaoLu != 1 ? 1500000 : 2000;
//        petDaoLu.nPoint.stamina = 1000;
//        petDaoLu.nPoint.maxStamina = 1000;
//        petDaoLu.nPoint.hpg = data[0];
//        petDaoLu.nPoint.mpg = data[1];
//        petDaoLu.nPoint.dameg = data[2];
//        petDaoLu.nPoint.defg = data[3];
//        petDaoLu.nPoint.critg = data[4];
//        for (int i = 0; i < ConstPlayer.QTY_MAX_ITEM_BODY_PET; i++) {
//            petDaoLu.inventory.itemsBody.add(ItemService.gI().createItemNull());
//        }
//        int[] skillsArr = player.gender == 0 ? ConstPlayer.SKILL_TD
//                : petDaoLu.gender == 1 ? ConstPlayer.SKILL_NAMEC
//                        : ConstPlayer.SKILL_XAYDA;
//        for (int idSkill : skillsArr) {
//            Skill skill = SkillUtil.createSkill(idSkill, 1);
//            petDaoLu.playerSkill.skills.add(skill);
//        }
//        petDaoLu.nPoint.setFullHpMp();
//        player.nguoiyeu = petDaoLu;
//    }
//
//    private int[] getDataDaoLus(Byte typePet) {
//        switch (typePet) {
//            case ConstFamily.DAO_LU_TYPE_1:
//                return getDataDaoLuT1();
//            case ConstFamily.DAO_LU_TYPE_2:
//                return getDataDaoLuT2();
//            case ConstFamily.DAO_LU_TYPE_3:
//                return getDataDaoLuT3();
//            default:
//                return getDataDaoLuT1();
//        }
//    }
//
//    private String getNameDaoLus(Byte typePet) {
//        switch (typePet) {
//            case ConstFamily.DAO_LU_TYPE_1:
//                return "Đạo Lữ - Hạng 1";
//            case ConstFamily.DAO_LU_TYPE_2:
//                return "Đạo Lữ - Hạng 2";
//            case ConstFamily.DAO_LU_TYPE_3:
//                return "Đạo Lữ - Hạng 3";
//            default:
//                return "Đạo Lữ";
//        }
//    }
//    //=====================Data Pet Dao Lu V2 by NDQ (Zalo - 0372475179) ========================
//
//    private int[] getDataDaoLuT1() {
//        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
//        int[] daoLuData = new int[5];
//        daoLuData[0] = Util.nextInt(40, 105) * 20; //hp
//        daoLuData[1] = Util.nextInt(40, 105) * 20; //mp
//        daoLuData[2] = Util.nextInt(20, 45); //dame
//        daoLuData[3] = Util.nextInt(9, 50); //def
//        daoLuData[4] = Util.nextInt(0, 2); //crit
//        return daoLuData;
//    }
//
//    private int[] getDataDaoLuT2() {
//        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
//        int[] daoLuData = new int[5];
//        daoLuData[0] = Util.nextInt(40, 105) * 20; //hp
//        daoLuData[1] = Util.nextInt(40, 105) * 20; //mp
//        daoLuData[2] = Util.nextInt(50, 120); //dame
//        daoLuData[3] = Util.nextInt(9, 50); //def
//        daoLuData[4] = Util.nextInt(0, 2); //crit
//        return daoLuData;
//    }
//
//    private int[] getDataDaoLuT3() {
//        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
//        int[] daoLuData = new int[5];
//        daoLuData[0] = Util.nextInt(40, 110) * 20; //hp
//        daoLuData[1] = Util.nextInt(40, 110) * 20; //mp
//        daoLuData[2] = Util.nextInt(50, 130); //dame
//        daoLuData[3] = Util.nextInt(9, 50); //def
//        daoLuData[4] = Util.nextInt(0, 2); //crit
//        return daoLuData;
//    }
//}
