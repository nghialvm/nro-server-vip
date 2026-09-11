package nro.services;

import Utils.Logger;
import nro.inventory.InventoryService;
import models.Item.ItemService;
import consts.ConstPlayer;
import nro.player.PetFollow;
import nro.player.Detu;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstDetu;
import nro.player.DuongTang;

public class DetuService {

    private static DetuService i;

    public static DetuService gI() {
        if (i == null) {
            i = new DetuService();
        }
        return i;
    }
//-------------------------------BOTTTTTTTTTTTTTTTTTTTTTTTTTT-------------------
    private void CheckPlayer(Player player) {
        player.Detu.nPoint.defg = Util.nextInt(1, 200);
        player.Detu.nPoint.def = player.Detu.nPoint.defg;
        player.Detu.nPoint.critg = Util.nextInt(0, 5);
        player.Detu.nPoint.crit = player.Detu.nPoint.critg;
        player.Detu.nPoint.stamina = 1000;
        player.Detu.nPoint.maxStamina = 1000;
        if (player.zone != null) {
            int MapId = player.zone.map.mapId;
            switch (MapId) {
                case 30:
                case 6:
                case 34:
                case 10:
                case 38:
                case 19:
                    player.Detu.nPoint.power = Util.nextInt(2_000_000, 100_000_000);
                    player.Detu.nPoint.hpg = Util.nextInt(5_000, 20_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(5_000, 20_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(300, 1_500) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 68:
                case 69:
                    player.Detu.nPoint.power = Util.nextInt(50_000_000, 140_000_000);
                    player.Detu.nPoint.hpg = Util.nextInt(20_000, 50_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(20_000, 50_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(1_000, 5_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 70:
                case 71:
                case 72:
                case 63:
                case 64:
                case 65:
                    player.Detu.nPoint.power = Util.nextInt(150_000_000, 1_500_000_000);
                    player.Detu.nPoint.hpg = Util.nextInt(50_000, 100_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(50_000, 100_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(3_000, 8_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 66:
                case 67:
                case 73:
                case 74:
                case 75:
                case 76:
                    player.Detu.nPoint.power = Util.nextLong(1_500_000_000L, 10_000_000_000L);
                    player.Detu.nPoint.hpg = Util.nextInt(100_000, 150_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(100_000, 150_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(5_000, 20_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 77:
                case 81:
                case 82:
                case 83:
                case 79:
                case 80:
                case 92:
                case 93:
                    player.Detu.nPoint.power = Util.nextLong(10_000_000_000L, 20_000_000_000L);
                    player.Detu.nPoint.hpg = Util.nextInt(150_000, 220_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(150_000, 220_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(10_000, 25_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 94:
                case 97:
                case 98:
                case 100:
                    player.Detu.nPoint.power = Util.nextLong(20_000_000_000L, 30_000_000_000L);
                    player.Detu.nPoint.hpg = Util.nextInt(220_000, 300_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(220_000, 300_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(15_000, 29_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                    player.Detu.nPoint.power = Util.nextLong(30_000_000_000L, 60_000_000_000L);
                    player.Detu.nPoint.hpg = Util.nextInt(700_000, 1_000_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(700_000, 1_000_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(40_000, 70_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                case 123:
                case 192:
                case 197:
                case 200:
                    player.Detu.nPoint.power = Util.nextLong(30_000_000_000L, 50_000_000_000L);
                    player.Detu.nPoint.hpg = Util.nextInt(300_000, 450_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(300_000, 450_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(19_000, 30_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
                default:
                    player.Detu.nPoint.power = Util.nextInt(2_000, 100_000);
                    player.Detu.nPoint.hpg = Util.nextInt(1_000, 3_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.hp = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.hpMax = player.Detu.nPoint.hpg;
                    player.Detu.nPoint.mpg = Util.nextInt(1_000, 3_000) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.hpg * 10 / 100) : 1);
                    player.Detu.nPoint.mp = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.mpMax = player.Detu.nPoint.mpg;
                    player.Detu.nPoint.dameg = Util.nextInt(29, 100) 
                            + (player.Detu.typeDeTu == ConstDetu.MABU ? (player.Detu.nPoint.dameg * 10 / 100) : 1);
                    player.Detu.nPoint.dame = player.Detu.nPoint.dameg;
                    break;
            }
        }
    }
    public void createNormalBot(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewBot(player, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                CheckPlayer(player);
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Xin hãy thu nhận con làm đệ tử");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
    public void createMabuBot(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewBot(player, true);
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                CheckPlayer(player);
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Oa oa oa...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
    private void createNewBot(Player player, boolean isMabu, byte... gender) {
        int[] data = isMabu ?  getDataPetMabu(): getDataPetNormal();
        Detu pet = new Detu(player);
        pet.name = "$" + (isMabu ? "Mabư" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = player.isPl() ? -player.id : -Math.abs(player.id) - 1_000_000;
        pet.nPoint.power = isMabu ? 1500000 : 2000;
        pet.typeDeTu = (byte) (isMabu ? 1 :0);
        pet.thuctinh = (byte) 0;
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int j = 0; j < ConstPlayer.QTY_MAX_ITEM_BODY_PET ; j++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int j = 0; j < ConstPlayer.QTY_MAX_SKILL_PET; j++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.Detu = pet;
    }
//-------------------------------CREATE DETU------------------------------------
    public void createNormalPetByGender(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false,false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Xin hãy thu nhận con làm đệ tử");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }

    public void createNormalPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Xin hãy thu nhận con làm đệ tử");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }

    public void createMabuPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false, false );
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Oa oa oa...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }

    public void createMabuPetByGender(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, false, false ,(byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.Detu.nPoint.limitPower = limitPower[0];
                    player.Detu.nPoint.initPowerLimit();
                }
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "Oa oa oa...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
    
    public void createUbuPet(Player player) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false, false, false, (byte) 0);
                player.Detu.nPoint.limitPower = 13;
                player.Detu.nPoint.initPowerLimit();
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
    
    public void createKidjirenPet(Player player) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true, false,false, (byte) 1);
                player.Detu.nPoint.limitPower = 13;
                player.Detu.nPoint.initPowerLimit();
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
    
    public void createKidbeerPet(Player player) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, true,false, (byte) 2);
                player.Detu.nPoint.limitPower = 13;
                player.Detu.nPoint.initPowerLimit();
                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.Detu, "...");
            } catch (Exception e) {
                Logger.logException(DetuService.class, e);
            }
        }).start();
    }
public void createBlackPet(Player player) {
    new Thread(() -> {
        try {
            createNewPet(player, false, false, false, false, true, (byte) player.gender);
            player.Detu.nPoint.limitPower = 13;
            player.Detu.nPoint.initPowerLimit();
            Thread.sleep(1000);
            Service.gI().chatJustForMe(player, player.Detu, "...");
        } catch (Exception e) {
            Logger.logException(DetuService.class, e);
        }
    }).start();
}

//-----------------------------------CHANGE DETU--------------------------------
    public void changeNormalPet(Player player, int gender) {
        byte limitPower = player.Detu.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu= null;
        createNormalPetByGender(player, gender, limitPower);
    }

    public void changeNormalPet(Player player) {
        byte limitPower = player.Detu.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu= null;
        createNormalPet(player, limitPower);
    }

    public void changeMabuPet(Player player) {
        byte limitPower = player.Detu.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createMabuPet(player, limitPower);
    }

    public void changeMabuPet(Player player, int gender) {
        byte limitPower = 0;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createMabuPetByGender(player, gender, limitPower);
    }
    
    public void changeUbuPet(Player player) {
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createUbuPet(player);
    }

    public void changeKidjirenPet(Player player) {
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createKidjirenPet(player);
    }

    public void changeKidbeerPet(Player player) {
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createKidbeerPet(player);
    }
    
     public void changeBlackPet(Player player) {
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.Detu.unFusion();
        }
        ChangeMapService.gI().exitMap(player.Detu);
        player.Detu.dispose();
        player.Detu = null;
        createBlackPet(player);
    }
//---------------------------NAME-----------------------------------------------
    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryService.gI().isExistItemBag(player, 400)) {
                Service.gI().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.gI().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.gI().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.Detu);
            player.Detu.name = "$" + name.toLowerCase().trim();
            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 400), 1);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.gI().chatJustForMe(player, player.Detu, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                    Logger.logException(DetuService.class, e);
                }
            }).start();
        } catch (Exception ex) {

        }
    }
//---------------------------DATA DETU------------------------------------------
    private int[] getDataPetNormal() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(20, 45); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    private int[] getDataPetMabu() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(20, 45); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }
     private int[] getDataPetBlackGoku() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(20, 45); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }
    
    private int[] getDataPetNew() {
        int[] petData = new int[5];
        petData[0] = 700000;
        petData[1] = 700000;
        petData[2] = 35000;
        petData[3] = 500;
        petData[4] = Util.nextInt(0, 2);
        for (int j = 0; j < 3; j++) {
            petData[j] += (petData[j] * 30 / 100);
        }
        return petData;
    }
    
    private void createNewPet(Player player, boolean isMabu, boolean isUbu, boolean isKidJiren, boolean isKidBill, boolean isBlackGoku, byte... gender) {
        int[] data = isMabu ? getDataPetMabu() : (isUbu || isKidJiren || isKidBill ) ? getDataPetNew(): isBlackGoku ? getDataPetBlackGoku() : getDataPetNormal();
        Detu pet = new Detu(player);
        pet.name = "$" + (isMabu ? "Mabư" : isUbu ? "Ubu" : isKidJiren ? "Kid jiren" : isKidBill ? "Kid beer" : isBlackGoku ? "Black Goku Rose" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = Player.setIdForPet(pet, player.id);
        pet.nPoint.power = isMabu  ? 1500000 :( isUbu ||isKidJiren || isKidBill || isBlackGoku) ? 1500000L : 2000;
        pet.typeDeTu = (byte) (isMabu ? 1 : isUbu ? 2 : isKidJiren ? 3 : isKidBill ? 4 : isBlackGoku ? 5: 0);
        pet.thuctinh = (byte) 0;
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int j = 0; j < ((isUbu || isKidJiren || isKidBill || isBlackGoku) ? ConstPlayer.QTY_MAX_ITEM_BODY_PET  : ConstPlayer.QTY_MAX_ITEM_BODY_PET) ; j++) { //ConstPlayer.QTY_MAX_ITEM_BODY_PET +5
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int j = 0; j < ((isUbu || isKidJiren || isKidBill || isBlackGoku) ? ConstPlayer.QTY_MAX_SKILL_PET + 1 : ConstPlayer.QTY_MAX_SKILL_PET); j++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.Detu = pet;
        if (isUbu || isKidJiren || isKidBill) {
            player.pointfusion.setHpFusion(30);
            player.pointfusion.setMpFusion(30);
            player.pointfusion.setDameFusion(30);
        }
        if (isBlackGoku) {
            player.pointfusion.setHpFusion(40);
            player.pointfusion.setMpFusion(40);
            player.pointfusion.setDameFusion(40);
        }
    }
    
    public static void PetFollow(Player pl, int h, int b, int l) {
        if (pl.PetFollow != null) {
            pl.PetFollow.dispose();
        }
        pl.PetFollow = new PetFollow(pl, (short) h, (short) b, (short) l);
        pl.PetFollow.name = "$";
        pl.PetFollow.gender = pl.gender;
        pl.PetFollow.nPoint.tiemNang = 1;
        pl.PetFollow.nPoint.power = 1;
        pl.PetFollow.nPoint.limitPower = 1;
        pl.PetFollow.nPoint.hpg = 500000;
        pl.PetFollow.nPoint.mpg = 500000;
        pl.PetFollow.nPoint.hp = 500000;
        pl.PetFollow.nPoint.mp = 500000;
        pl.PetFollow.nPoint.dameg = 1;
        pl.PetFollow.nPoint.defg = 1;
        pl.PetFollow.nPoint.critg = 1;
        pl.PetFollow.nPoint.stamina = 1;
        pl.PetFollow.nPoint.setBasePoint();
        pl.PetFollow.nPoint.setFullHpMp();
    }
    
    public static void DuongTang(Player pl) {
        if (pl.Duongtang != null) {
            pl.Duongtang.dispose();
        }
        pl.Duongtang = new DuongTang(pl);
        pl.Duongtang.name = "Đường tăng";
        pl.Duongtang.gender = pl.gender;
        pl.Duongtang.nPoint.tiemNang = 1;
        pl.Duongtang.nPoint.power = 1;
        pl.Duongtang.nPoint.limitPower = 1;
        pl.Duongtang.nPoint.hpg = 1_000_000;
        pl.Duongtang.nPoint.mpg = 1_000_000;
        pl.Duongtang.nPoint.hp = 1_000_000;
        pl.Duongtang.nPoint.mp = 1_000_000;
        pl.Duongtang.nPoint.dameg = 1;
        pl.Duongtang.nPoint.defg = 1;
        pl.Duongtang.nPoint.critg = 1;
        pl.Duongtang.nPoint.stamina = 1;
        pl.Duongtang.nPoint.setBasePoint();
        pl.Duongtang.nPoint.setFullHpMp();
    }
    
    public void deletePet(Player player) {
        Detu pet = player.Detu;
        if (pet != null) {
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                pet.unFusion();
            }
            ChangeMapService.gI().exitMap(pet);
            pet.dispose();
            player.Detu = null;
        }
    }
}
