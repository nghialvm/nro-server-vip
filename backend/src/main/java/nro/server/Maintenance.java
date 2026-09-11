package nro.server;

import Utils.Logger;
import nro.services.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Maintenance implements Runnable {

    public static volatile boolean isRunning = false;
    private static volatile boolean autoRestartRequested = false;
    private static Maintenance i;

    private int time;

    private Maintenance() {
    }

    public static Maintenance gI() {
        if (i == null) {
            i = new Maintenance();
        }
        return i;
    }

    public static boolean isAutoRestartRequested() {
        return autoRestartRequested;
    }

    public static void setAutoRestartRequested(boolean requested) {
        autoRestartRequested = requested;
    }

    /**
     * min = phút
     */
    public synchronized void start(int min) {
        Logger.log(Logger.YELLOW, "[MAINT] start(" + min + ") called | isRunning=" + isRunning + "\n");

        if (isRunning) {
            Logger.log(Logger.RED, "[MAINT] bỏ qua vì bảo trì đang chạy\n");
            return;
        }

        isRunning = true;
        this.time = min * 60;
        Logger.log(Logger.YELLOW, "[MAINT] thread starting | time=" + this.time + " seconds\n");

        new Thread(this, "Thread Bao Tri").start();
    }

    /**
     * sec = giây
     */
    public synchronized void startSeconds(int sec) {
        Logger.log(Logger.YELLOW, "[MAINT] startSeconds(" + sec + ") called | isRunning=" + isRunning + "\n");

        if (isRunning) {
            Logger.log(Logger.RED, "[MAINT] bỏ qua vì bảo trì đang chạy\n");
            return;
        }

        isRunning = true;
        this.time = sec;
        Logger.log(Logger.YELLOW, "[MAINT] thread starting | time=" + this.time + " seconds\n");

        new Thread(this, "Thread Bao Tri").start();
    }

    public void startNew(int min) {
        start(min);
    }

    private void autoRestartProcess() {
        try {
            if (!Boolean.parseBoolean(System.getProperty("awn.selfRestart", "true"))) {
                Logger.log(Logger.YELLOW, "[RESTART] self restart disabled by runtime configuration\n");
                return;
            }

            int seconds = 5;
            String currentDir = System.getProperty("user.dir");
            String os = System.getProperty("os.name").toLowerCase();
            boolean windows = os.contains("win");
            Path restartScript = Paths.get(currentDir, "scripts", windows ? "run-backend.bat" : "run-backend.sh");

            Logger.log(Logger.YELLOW, "[RESTART] begin | dir=" + currentDir + " | os=" + os + "\n");

            if (!Files.isRegularFile(restartScript)) {
                Logger.log(Logger.RED, "[RESTART] restart script not found: " + restartScript + "\n");
                return;
            }

            ProcessBuilder pb;

            if (windows) {
                String cmd = "timeout /t " + seconds + " /nobreak > nul && call \""
                        + restartScript.toAbsolutePath() + "\"";
                Logger.log(Logger.YELLOW, "[RESTART] windows cmd=" + cmd + "\n");

                pb = new ProcessBuilder(
                        "cmd", "/c",
                        "start", "",
                        "cmd", "/c",
                        cmd
                );
            } else {
                String cmd = "sleep " + seconds + "; exec \"" + restartScript.toAbsolutePath() + "\"";
                Logger.log(Logger.YELLOW, "[RESTART] linux cmd=" + cmd + "\n");

                pb = new ProcessBuilder(
                        "bash", "-c",
                        cmd
                );
            }

            pb.directory(Paths.get(currentDir).toFile());
            pb.redirectErrorStream(true);

            Logger.log(Logger.YELLOW, "[RESTART] processBuilder directory="
                    + pb.directory().getAbsolutePath() + "\n");

            pb.start();

            Logger.log(Logger.YELLOW, "[RESTART] Process launched OK\n");

        } catch (Exception e) {
            Logger.log(Logger.RED, "[RESTART] FAIL: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public synchronized void startImmediately() {
        if (isRunning) {
            Logger.log(Logger.RED, "[MAINT] startImmediately bỏ qua vì đang chạy\n");
            return;
        }

        isRunning = true;

        try {
            Logger.log(Logger.YELLOW, "[MAINT] BEGIN MAINTENANCE IMMEDIATELY\n");
            Logger.log(Logger.YELLOW, "[MAINT] autoRestart=" + autoRestartRequested + "\n");

            if (autoRestartRequested) {
                Logger.log(Logger.YELLOW, "[MAINT] calling autoRestartProcess() before close\n");
                autoRestartProcess();
                Thread.sleep(1000);
            } else {
                Logger.log(Logger.RED, "[MAINT] skip auto restart because autoRestartRequested=false\n");
            }

            Logger.log(Logger.YELLOW, "[MAINT] calling ServerManager.gI().close()\n");
            ServerManager.gI().close();
            Logger.log(Logger.YELLOW, "[MAINT] after close\n");

            Logger.log(Logger.YELLOW, "[MAINT] calling System.exit(0)\n");
            System.exit(0);

        } catch (Exception e) {
            Logger.log(Logger.RED, "[MAINT] startImmediately error: " + e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }

    @Override
    public void run() {
        try {
            Logger.log(Logger.YELLOW, "[MAINT] run() entered | time=" + this.time
                    + " | autoRestart=" + autoRestartRequested + "\n");

            while (this.time > 0) {
                Logger.log(Logger.YELLOW, "[MAINT] countdown=" + this.time + "\n");

                if (this.time == 60) {
                    Service.gI().sendThongBaoAllPlayer(
                            "Hệ thống sẽ bảo trì sau 1 phút nữa, hãy thoát game ngay để tránh mất mát vật phẩm."
                    );
                } else if (this.time < 60) {
                    Service.gI().sendThongBaoAllPlayer(
                            "Hệ thống sẽ bảo trì sau " + this.time + " giây nữa"
                    );
                } else {
                    int hour = this.time / 3600;
                    int min = (this.time - hour * 3600) / 60;
                    int sec = this.time % 60;

                    String hourStr = (hour > 0) ? hour + " giờ " : "";
                    String minStr = (min > 0) ? min + " phút " : "";
                    String secStr = (sec > 0) ? sec + " giây " : "";

                    Service.gI().sendThongBaoAllPlayer(
                            "Hệ thống sẽ bảo trì sau " + hourStr + minStr + secStr
                    );
                }

                Thread.sleep(1000);
                this.time--;
            }

            Logger.log(Logger.YELLOW, "[MAINT] countdown done -> BEGIN MAINTENANCE\n");
            Logger.log(Logger.YELLOW, "[MAINT] before close | autoRestart="
                    + autoRestartRequested + "\n");

            if (autoRestartRequested) {
                Logger.log(Logger.YELLOW, "[MAINT] calling autoRestartProcess()\n");
                autoRestartProcess();
                Logger.log(Logger.YELLOW, "[MAINT] autoRestartProcess() returned\n");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.log(Logger.RED, "[MAINT] sleep after restart interrupted\n");
                }
            } else {
                Logger.log(Logger.RED, "[MAINT] skip auto restart because autoRestartRequested=false\n");
            }

            Logger.log(Logger.YELLOW, "[MAINT] calling ServerManager.gI().close()\n");
            ServerManager.gI().close();
            Logger.log(Logger.YELLOW, "[MAINT] after close\n");

            Logger.log(Logger.YELLOW, "[MAINT] calling System.exit(0)\n");
            System.exit(0);

        } catch (Exception e) {
            Logger.log(Logger.RED, "[MAINT] ERROR: " + e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            isRunning = false;
            Logger.log(Logger.YELLOW, "[MAINT] finished | isRunning=false\n");
        }
    }
}
