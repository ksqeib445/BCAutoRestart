package fun.ksmc.bungee.extend.server;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class AutoRestart extends Plugin {
    Timer timer = null;
    Configuration config;
    String alert;
    String min;
    String sec;

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdir();
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("fun/ksmc/bungee/extend/server/config.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        alert = config.getString("alert");
        min = config.getString("min");
        sec = config.getString("sec");
        String retime = config.getString("reTime");
        String[] sp = retime.split(":");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sp[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(sp[1]));
        long today3 = calendar.getTimeInMillis();
        long now = System.currentTimeMillis();
        long delay;
        if (now > today3) {
            calendar.add(Calendar.DATE, 1);
            delay = calendar.getTimeInMillis() - now;
        } else {
            delay = today3 - now;
        }
        timer = new Timer("AutoRestart");
        timer.schedule(new TimerTask() {
            int i = 5;

            @Override
            public void run() {
                getProxy().broadcast(new TextComponent(alert.replace("{0}", String.valueOf(i)).replace("{1}", min)));
                i--;
                if (i < 0) cancel();
            }
        }, Math.max(delay - 5 * 60 * 1000, 1L), 60 * 1000);
        timer.schedule(new TimerTask() {
            int i = 20;

            @Override
            public void run() {
                getProxy().broadcast(new TextComponent(alert.replace("{0}", String.valueOf(i)).replace("{1}", sec)));
                i--;
                if (i < 0) cancel();
            }
        }, Math.max(delay - 20 * 1000, 1L), 1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getProxy().stop();
            }
        }, delay);
    }
}
