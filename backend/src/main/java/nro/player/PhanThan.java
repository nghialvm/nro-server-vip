package nro.player;

import nro.inventory.Inventory;
import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.skill.PlayerSkill;
import nro.skill.Skill;
import Utils.Util;
import nro.skill.SkillService;

public class PhanThan extends Player {
    
    public Player master;
    private long lastSpawnTime = 0;
    private long lastTimeDie = 0;

    public PhanThan(Player master) {
        super();
        this.master = master;
        this.isPhanThan = true;
        this.id = Util.nextInt(1_800_000_000, 1_900_000_000);
        this.gender = master.gender;
        this.name = "[PhÃ¢n ThÃ¢n] " + master.name;
        this.nPoint.hpMax = master.nPoint.hpMax;
        this.nPoint.mpMax = master.nPoint.mpMax;
        this.nPoint.dame = master.nPoint.dame;
        this.nPoint.def = master.nPoint.def;
        this.nPoint.crit = master.nPoint.crit;
        this.nPoint.power = master.nPoint.power;
        this.nPoint.tiemNang = master.nPoint.tiemNang;
        this.nPoint.stamina = master.nPoint.stamina;
        this.nPoint.maxStamina = master.nPoint.maxStamina;
        this.inventory = new Inventory();
        this.inventory.itemsBody = InventoryService.gI().copyItemsBody(master);
        this.playerSkill = new PlayerSkill(this);
        this.cloneSkill();
        this.nPoint.calPoint();
        this.nPoint.setFullHpMp();
        this.lastSpawnTime = System.currentTimeMillis();
    }

    private void cloneSkill() {
        for (Skill skill : master.playerSkill.skills) {
            Skill cloneSkill = new Skill(skill);
            this.playerSkill.skills.add(cloneSkill);
        }
    }

    @Override
    public void update() {
        super.update();
        if (isDie() && canRespawn()) {
            Service.getInstance().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
        if (master != null && (this.zone == null || this.zone != master.zone)) {
            joinMapMaster();
        }
        if (this.newSkill != null && this.newSkill.isStartSkillSpecial) {
            SkillService.gI().newSkillNotFocus(this, 20);
            return;
        }
        if (Util.canDoWithTime(lastSpawnTime, 120000) || (lastTimeDie != 0 && Util.canDoWithTime(lastTimeDie, 3_000))) {
            dispose();
        }
    }

    @Override
    public void setDie(Player plAtt) {
        super.setDie(plAtt);
        lastTimeDie = System.currentTimeMillis();
    }

    private boolean canRespawn() {
        return lastTimeDie == 0;
    }

    public void joinMapMaster() {
        this.location.x = master.location.x + Util.nextInt(-10, 10);
        this.location.y = master.location.y;
        MapService.gI().goToMap(this, master.zone);
        this.zone.load_Me_To_Another(this);
    }
     
    public void followMaster() {
        int mX = master.location.x;
        int mY = master.location.y;
        int disX = this.location.x - mX;
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= 40) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(0, 40);
            } else {
                this.location.x = mX + Util.nextInt(0, 40);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    @Override
    public short getHead() {
        return master.getHead();
    }

    @Override
    public short getBody() {
        return master.getBody();
    }

    @Override
    public short getLeg() {
        return master.getLeg();
    }
    
    @Override
    public byte getEffFront() {
        return master.getEffFront();
    }

    @Override
    public byte getAura() {
        return master.getAura();
    }
    
    @Override
    public short getFlagBag() {
        return master.getFlagBag();
    }

    @Override
    public short getMount() {
        return master.getMount();
    }

    @Override
    public void dispose() {
        ChangeMapService.gI().exitMap(this);
        if (this.master != null) {
            this.master.PhanThan = null;
        }
        this.master = null;
        super.dispose();
    }
}





