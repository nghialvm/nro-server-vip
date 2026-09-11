package nro.server;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class SystemMetrics {
    private static String formatMemory(double used, double total) {
        return String.format("(%.1f/%.1f) GB", used, total);
    }
    
    private static String formatPercentage(double percentage) {
        return String.format("%.0f%%", percentage);
    }

    private static double getUsedMemoryGB() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();
        long usedMemory = totalMemory - freeMemory;
        return (double)usedMemory / 1.073741824E9;
    }

    private static double getTotalMemoryGB() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        long totalMemory = osBean.getTotalMemorySize();
        return (double)totalMemory / 1.073741824E9;
    }

    private static double getRAMUsagePercentage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();
        long usedMemory = totalMemory - freeMemory;
        return (double)usedMemory / (double)totalMemory * 100.0;
    }

    private static double getCPUUsagePercentage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getCpuLoad() * 100.0;
        return cpuUsage;
    }

    public static String ToString() {
        double usedMemory = SystemMetrics.getUsedMemoryGB();
        double totalMemory = SystemMetrics.getTotalMemoryGB();
        double ramUsagePercentage = SystemMetrics.getRAMUsagePercentage();
        double cpuUsagePercentage = SystemMetrics.getCPUUsagePercentage();
        String string = "Used Memory: " + SystemMetrics.formatMemory(usedMemory, totalMemory) + "\nRAM Usage Percentage: " + SystemMetrics.formatPercentage(ramUsagePercentage) + "\nCPU Usage Percentage: " + SystemMetrics.formatPercentage(cpuUsagePercentage);
        return string;
    }
}





