//package nro.services;
//
//import nro.server.Manager;
//import models.Item.ItemService;
//import consts.ConstPlayer;
//import nro.player.Player;
//import nro.services.Fun.ChangeMapService;
//import Utils.Logger;
//import Utils.SkillUtil;
//import Utils.Util;
//import models.Item.Item;
//import models.Item.ItemOption;
//import network.io.Message;
//import nro.player.Bo;
//
//public class FamilyService {
//
//    private static FamilyService i;
//
//    public static FamilyService gI() {
//        if (i == null) {
//            i = new FamilyService();
//        }
//        return i;
//    }
//    
//    public void showInfoFather(Player pl) {
//        if (pl != null && pl.bo != null) {
//            Message msg;
//            try {
//                msg = new Message(3);
//                msg.writer().writeByte(2);
//                msg.writer().writeShort(pl.bo.getAvatar());
//                msg.writer().writeByte(pl.bo.inventory.itemsBody.size());
//
//                for (Item item : pl.bo.inventory.itemsBody) {
//                    if (!item.isNotNullItem()) {
//                        msg.writer().writeShort(-1);
//                    } else {
//                        msg.writer().writeShort(item.template.id);
//                        msg.writer().writeInt(item.quantity);
//                        msg.writer().writeUTF(item.getInfo());
//                        msg.writer().writeUTF(item.getContent());
//
//                        int countOption = item.itemOptions.size();
//                        msg.writer().writeByte(countOption);
//                        for (ItemOption iop : item.itemOptions) {
//                            msg.writer().writeInt(iop.optionTemplate.id);
//                            msg.writer().writeInt(iop.param);
//                        }
//                    }
//                }
//
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.hp), Manager.readInt); //hp
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.hpMax), Manager.readInt); //hpfull
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.mp), Manager.readInt); //mp
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.mpMax), Manager.readInt); //mpfull
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.dame), Manager.readInt); //damefull
//                msg.writer().writeUTF(pl.bo.name); //name
//                msg.writer().writeUTF(pl.bo.getCanhGioiPhuMauFather(pl.bo.CapcanhGioi)); //curr level
//                msg.writer().writeLong(pl.bo.nPoint.power); //power
//                msg.writer().writeLong(pl.bo.nPoint.tiemNang); //tiềm năng
//                msg.writer().writeByte(pl.bo.getStatus()); //status
//                msg.writer().writeShort(pl.bo.nPoint.stamina); //stamina
//                msg.writer().writeShort(pl.bo.nPoint.maxStamina); //stamina full
//                msg.writer().writeByte(pl.bo.nPoint.crit); //crit
//                msg.writer().writeInt(pl.bo.nPoint.def); //def
//                int sizeSkill = pl.bo.playerSkill.skills.size();
//                msg.writer().writeByte(sizeSkill); //count pet skill
//                for (int i = 0; i < sizeSkill; i++) {
//                    if (pl.bo.playerSkill.skills.get(i).skillId != -1) {
//                        msg.writer().writeShort(pl.bo.playerSkill.skills.get(i).skillId);
//                    } else {
//                        switch (i) {
//                            default: {
//                                msg.writer().writeShort(-1);
//                                msg.writer().writeUTF("Đột Phá Để Mở Khoá!");
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                pl.sendMessage(msg);
//                msg.cleanup();
//            } catch (Exception e) {
//                Logger.logException(Service.class, e);
//            }
//        }
//    }
//    
//    public void InfoFatherGoc(Player pl) {
//        if (pl != null && pl.bo != null) {
//            Message msg;
//            try {
//                msg = new Message(-109);
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.hpg), Manager.readInt); //hp
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.mpg), Manager.readInt); //hpfull
//                msg.writeCris(Util.CrisGH(pl.bo.nPoint.dameg), Manager.readInt); //mp
//                msg.writer().writeShort(pl.bo.nPoint.defg);
//                msg.writer().writeByte(pl.bo.nPoint.critg);
//                pl.sendMessage(msg);
//                msg.cleanup();
//            } catch (Exception e) {
//                Logger.logException(Service.class, e);
//            }
//        }
//    }
//
//    public void showInfoMother(Player pl) {
//        if (pl != null && pl.me != null) {
//            Message msg;
//            try {
//                msg = new Message(3);
//                msg.writer().writeByte(2);
//                msg.writer().writeShort(pl.me.getAvatar());
//                msg.writer().writeByte(pl.me.inventory.itemsBody.size());
//
//                for (Item item : pl.me.inventory.itemsBody) {
//                    if (!item.isNotNullItem()) {
//                        msg.writer().writeShort(-1);
//                    } else {
//                        msg.writer().writeShort(item.template.id);
//                        msg.writer().writeInt(item.quantity);
//                        msg.writer().writeUTF(item.getInfo());
//                        msg.writer().writeUTF(item.getContent());
//
//                        int countOption = item.itemOptions.size();
//                        msg.writer().writeByte(countOption);
//                        for (ItemOption iop : item.itemOptions) {
//                            msg.writer().writeInt(iop.optionTemplate.id);
//                            msg.writer().writeInt(iop.param);
//                        }
//                    }
//                }
//
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.hp), Manager.readInt); //hp
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.hpMax), Manager.readInt); //hpfull
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.mp), Manager.readInt); //mp
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.mpMax), Manager.readInt); //mpfull
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.dame), Manager.readInt); //damefull
//                msg.writer().writeUTF(pl.me.name); //name
//                msg.writer().writeUTF(pl.me.getCanhGioiPhuMauMother(pl.me.CapcanhGioi)); //curr level
//                msg.writer().writeLong(pl.me.nPoint.power); //power
//                msg.writer().writeLong(pl.me.nPoint.tiemNang); //tiềm năng
//                msg.writer().writeByte(pl.me.getStatus()); //status
//                msg.writer().writeShort(pl.me.nPoint.stamina); //stamina
//                msg.writer().writeShort(pl.me.nPoint.maxStamina); //stamina full
//                msg.writer().writeByte(pl.me.nPoint.crit); //crit
//                msg.writer().writeInt(pl.me.nPoint.def); //def
//                int sizeSkill = pl.me.playerSkill.skills.size();
//                msg.writer().writeByte(sizeSkill); //count pet skill
//                for (int i = 0; i < sizeSkill; i++) {
//                    if (pl.me.playerSkill.skills.get(i).skillId != -1) {
//                        msg.writer().writeShort(pl.me.playerSkill.skills.get(i).skillId);
//                    } else {
//                        switch (i) {
//                            default: {
//                                msg.writer().writeShort(-1);
//                                msg.writer().writeUTF("Đột Phá Để Mở Khoá!");
//                                break;
//                            }
//                        }
//                    }
//                }
//                pl.sendMessage(msg);
//                msg.cleanup();
//            } catch (Exception e) {
//                Logger.logException(Service.class, e);
//            }
//        }
//    }
//    
//    public void InfoMotherGoc(Player pl) {
//        if (pl != null && pl.me != null) {
//            Message msg;
//            try {
//                msg = new Message(-109);
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.hpg), Manager.readInt); //hp
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.mpg), Manager.readInt); //hpfull
//                msg.writeCris(Util.CrisGH(pl.me.nPoint.dameg), Manager.readInt); //mp
//                msg.writer().writeShort(pl.me.nPoint.defg);
//                msg.writer().writeByte(pl.me.nPoint.critg);
//                pl.sendMessage(msg);
//                msg.cleanup();
//            } catch (Exception e) {
//                Logger.logException(Service.class, e);
//            }
//        }
//    }
//
//    public void createFather(Player player, int gender, byte... limitPower) {
//        new Thread(() -> {
//            try {
//                createNewPet(player, false, false, false, false, false, (byte) gender);
//                if (limitPower != null && limitPower.length == 1) {
//                    player.bo.nPoint.limitPower = limitPower[0];
//                    player.bo.nPoint.initPowerLimit();
//                }
//                Thread.sleep(1000);
//                Service.gI().chatJustForMe(player, player.bo, "Ta là cha ngươi đây, thu nhận ta đi!");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//    
//    public void createMother(Player player, int gender, byte... limitPower) {
//        new Thread(() -> {
//            try {
//                createNewPet(player, false, false, false, false, false, (byte) gender);
//                if (limitPower != null && limitPower.length == 1) {
//                    player.me.nPoint.limitPower = limitPower[0];
//                    player.me.nPoint.initPowerLimit();
//                }
//                Thread.sleep(1000);
//                Service.gI().chatJustForMe(player, player.me, "Mẹ ở đây rồi, hãy cho mẹ theo con!");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    public void changeFather(Player player, int gender) {
//        byte limitPower = player.bo.nPoint.limitPower;
//        ChangeMapService.gI().exitMap(player.bo);
//        player.bo.dispose();
//        player.bo = null;
//        createFather(player, gender, limitPower);
//    }
//
//    public void changeMother(Player player, int gender) {
//        byte limitPower = player.me.nPoint.limitPower;
//        ChangeMapService.gI().exitMap(player.me);
//        player.me.dispose();
//        player.me = null;
//        createMother(player, gender, limitPower);
//    }
//
////---------------------------DATA DETU------------------------------------------
//    private int[] getDataPetNormal() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//
//    private int[] getDataPetMabu() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//
//
//    private int[] getDataPetBerus() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//    
//    private int[] getDataPetNgokhong() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//    
//    private int[] getDataPetNgokhong2() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//    
//    private int[] getDataPetNgokhong3() {
//        int[] petData = new int[5];
//        petData[0] = Util.nextInt(40, 105) * 20; //hp
//        petData[1] = Util.nextInt(40, 105) * 20; //mp
//        petData[2] = Util.nextInt(20, 45); //dame
//        petData[3] = Util.nextInt(9, 50); //def
//        petData[4] = Util.nextInt(0, 2); //crit
//        return petData;
//    }
//
//    private void createNewPet(Player player, boolean isMabu, boolean isBerus, boolean Ngokhong, boolean Ngokhong2, boolean Ngokhong3, byte... gender) {
//        int[] data = isMabu ?  getDataPetMabu():
//                isBerus ? getDataPetBerus():
//                Ngokhong ? getDataPetNgokhong():
//                Ngokhong2 ? getDataPetNgokhong2():
//                Ngokhong3 ? getDataPetNgokhong3():
//                getDataPetNormal();
//        Bo pet = new Bo(player);
//        pet.name = "Bố " + player.NameFather + " - " + player.name;
//        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
//        pet.id = Player.setIdForPet(pet, player.id);
//        pet.nPoint.power = isMabu || Ngokhong || Ngokhong2 || Ngokhong3 || isBerus ? 1500000 : 2000;
//        pet.CapcanhGioi = (byte) (isMabu ? 1 : isBerus ? 2 : Ngokhong ? 3 : Ngokhong2 ? 4 : Ngokhong3 ? 5 :0);
//        pet.CapcanhGioi = (byte) 0;
//        pet.nPoint.stamina = 1000;
//        pet.nPoint.maxStamina = 1000;
//        pet.nPoint.hpg = data[0];
//        pet.nPoint.mpg = data[1];
//        pet.nPoint.dameg = data[2];
//        pet.nPoint.defg = data[3];
//        pet.nPoint.critg = data[4];
//        for (int i = 0; i < ConstPlayer.QTY_MAX_ITEM_BODY_PET ; i++) {
//            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
//        }
//        pet.openSkill1();
//        for (int i = 0; i < 5; i++) {
//            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
//        }
//        pet.nPoint.setFullHpMp();
//        player.bo = pet;
//    }
//    
//    
//    public void deleteBo(Player player) {
//        Bo pet = player.bo;
//        if (pet != null) {
//            ChangeMapService.gI().exitMap(pet);
//            pet.dispose();
//            player.bo = null;
//        }
//    }
//}
