package nro.config;



import java.util.HashMap;
import java.util.Map;
import nro.skill.Skill;

/**
 * Config hệ số tăng/giảm damage cho từng skill.
 * Hệ số mặc định = 1.0 (100%)
 */
public class SkillDamageConfig {

    // Dùng Byte thay vì Integer để khớp với kiểu skillId trong Skill
    private static final Map<Byte, Double> skillDamageMultipliers = new HashMap<>();

    static {
        // ===== Trái đất =====
        skillDamageMultipliers.put(Skill.DRAGON, 1.0);       // dame chuẩn
        skillDamageMultipliers.put(Skill.KAMEJOKO, 0.8);
        skillDamageMultipliers.put(Skill.KAIOKEN, 1.2);
        
        skillDamageMultipliers.put(Skill.DICH_CHUYEN_TUC_THOI, 0.8);// giam 20%
        
        
        

        // ===== Xayda =====
        skillDamageMultipliers.put(Skill.GALICK, 1.2);
        skillDamageMultipliers.put(Skill.ANTOMIC, 1.5);      // tăng 50%
        
        

        // ===== Namek =====
        skillDamageMultipliers.put(Skill.DEMON, 1.0);
        skillDamageMultipliers.put(Skill.MASENKO, 1.0);  
        skillDamageMultipliers.put(Skill.LIEN_HOAN, 1.1);// Tang 10%

        // ===== Skill đặc biệt =====
        skillDamageMultipliers.put(Skill.TU_SAT, 3.0);         
        skillDamageMultipliers.put(Skill.QUA_CAU_KENH_KHI, 2.5);
        skillDamageMultipliers.put(Skill.MAKANKOSAPPO, 3.0);

        // ... bạn có thể thêm skill khác ở đây
    }

    /**
     * Lấy hệ số dame cho skill.
     * @param skillId id của skill (byte)
     * @return hệ số dame (mặc định = 1.0)
     */
    public static double getMultiplier(byte skillId) {
        return skillDamageMultipliers.getOrDefault(skillId, 1.0);
    }

    /**
     * Có thể set lại multiplier trong runtime
     * @param skillId
     * @param value
     */
    public static void setMultiplier(byte skillId, double value) {
        skillDamageMultipliers.put(skillId, value);
    }
}
