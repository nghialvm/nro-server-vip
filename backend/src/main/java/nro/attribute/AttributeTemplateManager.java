package nro.attribute;

import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class AttributeTemplateManager {

    private static final AttributeTemplateManager instance = new AttributeTemplateManager();

    public static AttributeTemplateManager getInstance() {
        return instance;
    }

    private final List<AttributeTemplate> list = new ArrayList<>();

    public void load() {
        PreparedStatement ps;
        ResultSet rs;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("SELECT * FROM attribute_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                AttributeTemplate at = AttributeTemplate.builder()
                        .id(id)
                        .name(name)
                        .build();
                add(at);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void add(AttributeTemplate at) {
        list.add(at);
    }

    public void remove(AttributeTemplate at) {
        list.remove(at);
    }

    public AttributeTemplate find(int id) {
        for (AttributeTemplate at : list) {
            if (at.getId() == id) {
                return at;
            }
        }
        return null;
    }
}






