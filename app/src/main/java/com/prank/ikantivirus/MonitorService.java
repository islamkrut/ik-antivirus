package com.prank.ikantivirus;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;

public class MonitorService extends Service {
    private Thread monitorThread;
    private boolean isRunning = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        monitorThread = new Thread(() -> {
            while (isRunning) {
                String topPackage = getForegroundApp();
                String tiktok = "com.zhiliaoapp.musically";
                
                if (topPackage != null && topPackage.equals(tiktok)) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        monitorThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getForegroundApp() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
        if (stats != null) {
            UsageStats recent = null;
            for (UsageStats stat : stats) {
                if (recent == null || stat.getLastTimeUsed() > recent.getLastTimeUsed()) {
                    recent = stat;
                }
            }
            return recent != null ? recent.getPackageName() : null;
        }
        return null;
    }
}
