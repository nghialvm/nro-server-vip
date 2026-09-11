package nro.npc.ListNpc;



import nro.services.TaskService;
import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;

public class DauThan extends Npc {

    public DauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            player.magicTree.openMenuTree();
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA: {
                    switch (select) {
                        case 0:
                            player.magicTree.harvestPea();
                            break;
                        case 1: {
                            if (player.magicTree.level == 10) {
                                player.magicTree.fastRespawnPea();
                            } else {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        }
                        case 2:
                            player.magicTree.fastRespawnPea();
                            break;
                        default: {
                            break;
                        }
                    }
                    break;
                }

                case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA: {
                    if (select == 0) {
                        player.magicTree.harvestPea();
                    } else if (select == 1) {
                        player.magicTree.showConfirmUpgradeMagicTree();
                    }
                    break;
                }
                case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE: {
                    if (select == 0) {
                        player.magicTree.upgradeMagicTree();
                    }
                    break;
                }
                case ConstNpc.MAGIC_TREE_UPGRADE: {
                    if (select == 0) {
                        player.magicTree.fastUpgradeMagicTree();
                    } else if (select == 1) {
                        player.magicTree.showConfirmUnuppgradeMagicTree();
                    }
                    break;
                }
                case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE: {
                    if (select == 0) {
                        player.magicTree.unupgradeMagicTree();
                    }
                    break;
                }
            }
        }
    }
}
