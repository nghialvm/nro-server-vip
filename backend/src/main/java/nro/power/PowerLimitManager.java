package nro.power;

import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;



public class PowerLimitManager {

    private static final long POWER_REQUIRED_TO_OPEN_LIMIT = 180_000_000_000L;

    private static final PowerLimitManager instance = new PowerLimitManager();

    public static PowerLimitManager getInstance() {
        return instance;
    }

    @Getter
    private volatile List<PowerLimit> powers;

    public PowerLimitManager() {
        powers = new ArrayList<>();
    }
    
    public synchronized void load() {
        List<PowerLimit> loaded = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT id, power, hp, mp, damage, defense, critical, tnsm_rate FROM power_limit ORDER BY id ASC");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                long power = rs.getLong("power");
                long hp = rs.getLong("hp");
                long mp = rs.getLong("mp");
                long damage = rs.getLong("damage");
                int defense = rs.getInt("defense");
                int critical = rs.getInt("critical");
                int tnsmRate = rs.getInt("tnsm_rate");
                PowerLimit powerLimit = PowerLimit.builder()
                        .id(id)
                        .power(power)
                        .hp(hp)
                        .mp(mp)
                        .damage(damage)
                        .defense(defense)
                        .critical(critical)
                        .tnsmRate(tnsmRate)
                        .build();
                loaded.add(powerLimit);
            }
            loaded.sort(Comparator.comparingInt(PowerLimit::getId));
            powers = loaded;
        } catch (SQLException ex) {
            // Keep the last complete snapshot if a reload fails.
            ex.printStackTrace();
        }
    }

    public synchronized void add(PowerLimit powerLimit) {
        if (powerLimit == null) {
            return;
        }
        List<PowerLimit> updated = new ArrayList<>(powers);
        updated.add(powerLimit);
        updated.sort(Comparator.comparingInt(PowerLimit::getId));
        powers = updated;
    }

    public synchronized void remove(PowerLimit powerLimit) {
        if (powerLimit == null) {
            return;
        }
        List<PowerLimit> updated = new ArrayList<>(powers);
        updated.remove(powerLimit);
        powers = updated;
    }

    public PowerLimit get(int index) {
        List<PowerLimit> snapshot = powers;
        for (PowerLimit powerLimit : snapshot) {
            if (powerLimit.getId() == index) {
                return powerLimit;
            }
        }
        return null;
    }

    public PowerLimit getNext(int currentId) {
        List<PowerLimit> snapshot = powers;
        if (snapshot == null) {
            return null;
        }
        for (PowerLimit powerLimit : snapshot) {
            if (powerLimit != null && powerLimit.getId() > currentId) {
                return powerLimit;
            }
        }
        return null;
    }

    public boolean hasNext(int currentId) {
        return getNext(currentId) != null;
    }

    public boolean requiresPowerToOpen(int currentId) {
        PowerLimit currentLimit = get(currentId);
        return currentLimit != null && currentLimit.getPower() >= POWER_REQUIRED_TO_OPEN_LIMIT;
    }

    public int getMaxId() {
        List<PowerLimit> snapshot = powers;
        if (snapshot == null || snapshot.isEmpty()) {
            return -1;
        }
        return snapshot.get(snapshot.size() - 1).getId();
    }
}






