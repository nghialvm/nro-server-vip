package nro.template;

import java.util.ArrayList;
import java.util.List;
import nro.map.EffectMap;
import nro.map.WayPoint;


public class MapTemplate {
    
    public int id;
    public String name;

    public byte type;
    public byte planetId;
    public byte bgType;
    public byte tileId;
    public byte bgId;
    public byte genderType;

    public byte zones;
    public byte maxPlayerPerZone;
    public List<WayPoint> wayPoints;

    public byte[] mobTemp;
    public byte[] mobLevel;
    public int[] mobHp;
    public short[] mobX;
    public short[] mobY;

    public byte[] npcId;
    public short[] npcX;
    public short[] npcY;
    public List<EffectMap> effectMaps;

    public MapTemplate() {
        this.wayPoints = new ArrayList<>();
        this.effectMaps = new ArrayList<>();
    }
        
}






