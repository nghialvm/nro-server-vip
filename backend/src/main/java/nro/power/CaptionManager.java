package nro.power;

import nro.player.Player;
import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;



public class CaptionManager {

    private static final CaptionManager instance = new CaptionManager();

    public static CaptionManager getInstance() {
        return instance;
    }

    @Getter
    private volatile List<Caption> captions;

    public CaptionManager() {
        captions = new ArrayList<>();
    }

    public synchronized void load() {
        List<Caption> loaded = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT id, earth, saiya, namek, power FROM `caption` ORDER BY power ASC, id ASC");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String earth = rs.getString("earth");
                String saiya = rs.getString("saiya");
                String namek = rs.getString("namek");
                long power = rs.getLong("power");
                Caption caption = Caption.builder()
                        .id(id)
                        .earth(earth)
                        .saiya(saiya)
                        .namek(namek)
                        .power(power)
                        .build();
                loaded.add(caption);
            }
            // The index sent to the client is the position in this list, so
            // captions must always be ordered by their required power.
            loaded.sort(Comparator.comparingLong(Caption::getPower)
                    .thenComparingInt(Caption::getId));
            captions = loaded;
        } catch (Exception ex) {
            // Keep the last complete snapshot when the database is not
            // available.  Replacing it with a partially loaded list makes
            // server and client caption levels disagree.
            ex.printStackTrace();
        }
    }

    public synchronized void add(Caption caption) {
        if (caption == null) {
            return;
        }
        List<Caption> updated = new ArrayList<>(captions);
        updated.add(caption);
        updated.sort(Comparator.comparingLong(Caption::getPower)
                .thenComparingInt(Caption::getId));
        captions = updated;
    }

    public synchronized void remove(Caption caption) {
        if (caption == null) {
            return;
        }
        List<Caption> updated = new ArrayList<>(captions);
        updated.remove(caption);
        captions = updated;
    }

    public Caption find(int id) {
        List<Caption> snapshot = captions;
        for (Caption caption : snapshot) {
            if (caption.getId() == id) {
                return caption;
            }
        }
        return null;
    }

    public Caption findLevel(int level) {
        List<Caption> snapshot = captions;
        if (level < 0 || level >= snapshot.size()) {
            return null;
        }
        return snapshot.get(level);
    }

    /**
     * Finds the caption whose threshold is exactly the supplied power.
     * Power-limit NPCs use this to show the name of the next threshold.
     */
    public Caption findByPower(long power) {
        List<Caption> snapshot = captions;
        for (Caption caption : snapshot) {
            if (caption.getPower() == power) {
                return caption;
            }
        }
        return null;
    }

    public int getLevel(Player player) {
        List<Caption> snapshot = captions;
        if (player == null || player.nPoint == null || snapshot.isEmpty()) {
            return 0;
        }
        long power = player.nPoint.power;
        int level = 0;
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            if (power >= snapshot.get(i).getPower()) {
                level = i;
                break;
            }
        }
        return level;
    }
}






