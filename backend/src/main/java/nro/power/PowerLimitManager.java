package nro.power;

import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;



public class PowerLimitManager {

    private static final PowerLimitManager instance = new PowerLimitManager();

    public static PowerLimitManager getInstance() {
        return instance;
    }

    @Getter
    private List<PowerLimit> powers;

    public PowerLimitManager() {
        powers = new ArrayList<>();
    }
    
    public void load() {
        PreparedStatement ps;
        ResultSet rs;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("SELECT * FROM power_limit");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getShort("id");
                long power = rs.getLong("power");
                long hp = rs.getInt("hp");
                long mp = rs.getInt("mp");
                long damage = rs.getInt("damage");
                int defense = rs.getInt("defense");
                int critical = rs.getInt("critical");
                PowerLimit powerLimit = PowerLimit.builder()
                        .id(id)
                        .power(power)
                        .hp(hp)
                        .mp(mp)
                        .damage(damage)
                        .defense(defense)
                        .critical(critical)
                        .build();
                add(powerLimit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void add(PowerLimit powerLimit) {
        powers.add(powerLimit);
    }

    public void remove(PowerLimit powerLimit) {
        powers.remove(powerLimit);
    }

    public PowerLimit get(int index) {
        if (index < 0 || index >= powers.size()) {
            return null;
        }
        return powers.get(index);
    }
}






