package com.thaikaytv.app;

import com.thaikaytv.app.models.Channel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3UParser {

    public static List<Channel> parseFromUrl(String m3uUrl) throws Exception {
        List<Channel> channels = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection) new URL(m3uUrl).openConnection();
        connection.setRequestProperty("User-Agent", "thaikaytv/1.0");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            String currentName = null;
            String currentUrl = null;
            String currentLogo = "";
            String currentGroup = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#EXTINF:")) {
                    Pattern namePattern = Pattern.compile(",(?!.*,)(.*)$");
                    Matcher nameMatcher = namePattern.matcher(line);
                    if (nameMatcher.find()) {
                        currentName = nameMatcher.group(1).trim();
                    }

                    Pattern logoPattern = Pattern.compile("tvg-logo=\"(.*?)\"");
                    Matcher logoMatcher = logoPattern.matcher(line);
                    if (logoMatcher.find()) {
                        currentLogo = logoMatcher.group(1);
                    }

                    Pattern groupPattern = Pattern.compile("group-title=\"(.*?)\"");
                    Matcher groupMatcher = groupPattern.matcher(line);
                    if (groupMatcher.find()) {
                        currentGroup = groupMatcher.group(1);
                    }
                } else if (!line.isEmpty() && !line.startsWith("#")) {
                    currentUrl = line;
                    if (currentName != null) {
                        Channel channel = new Channel(currentName, currentUrl);
                        channel.setLogo(currentLogo);
                        channel.setGroup(currentGroup);
                        channels.add(channel);
                    }
                    currentName = null;
                    currentUrl = null;
                    currentLogo = "";
                    currentGroup = "";
                }
            }
        }
        return channels;
    }
}
