import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.json.JSONArray;

public class SafeBrowsingChecker {

    private static final String API_KEY = "AIzaSyAIoPDPBZsDTvq-qt4gkMBhf8siCn5m3gg"; // <<< Paste your real API key

    public static boolean isPhishingURL(String urlToCheck) {
        try {
            JSONObject client = new JSONObject();
            client.put("clientId", "yourcompanyname"); // anything
            client.put("clientVersion", "1.5.2");

            JSONObject threatInfo = new JSONObject();
            threatInfo.put("threatTypes", new JSONArray(new String[]{"MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"}));
            threatInfo.put("platformTypes", new JSONArray(new String[]{"ANY_PLATFORM"}));
            threatInfo.put("threatEntryTypes", new JSONArray(new String[]{"URL"}));

            JSONArray entries = new JSONArray();
            JSONObject urlEntry = new JSONObject();
            urlEntry.put("url", urlToCheck);
            entries.put(urlEntry);
            threatInfo.put("threatEntries", entries);

            JSONObject requestBody = new JSONObject();
            requestBody.put("client", client);
            requestBody.put("threatInfo", threatInfo);

            URL url = new URL("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + API_KEY);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseStrBuilder.append(responseLine.trim());
            }

            JSONObject response = new JSONObject(responseStrBuilder.toString());

            return response.has("matches");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
