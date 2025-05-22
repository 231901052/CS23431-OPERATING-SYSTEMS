import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class VirusTotalChecker {
    public static final String API_KEY = "3b33fa889aacffa5dc0272a430ad8a845d8d206eeff34c805495058404a4afea"; // Replace with your key

    public static Map<String, Object> getScanDetails(String urlToCheck) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // Submit URL (POST)
        URL submitUrl = new URL("https://www.virustotal.com/api/v3/urls");
        HttpURLConnection postConn = (HttpURLConnection) submitUrl.openConnection();
        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("x-apikey", API_KEY);
        postConn.setDoOutput(true);

        String postData = "url=" + URLEncoder.encode(urlToCheck, "UTF-8");
        try (OutputStream os = postConn.getOutputStream()) {
            os.write(postData.getBytes());
        }

        if (postConn.getResponseCode() != 200) {
            throw new IOException("Failed to submit URL: " + postConn.getResponseCode());
        }

        InputStream postResponse = postConn.getInputStream();
        JSONObject postJson = new JSONObject(new JSONTokener(postResponse));
        String analysisId = postJson.getJSONObject("data").getString("id");

        // Wait briefly (1s) to let VT process it
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Get Analysis Result
        URL reportUrl = new URL("https://www.virustotal.com/api/v3/analyses/" + analysisId);
        HttpURLConnection getConn = (HttpURLConnection) reportUrl.openConnection();
        getConn.setRequestProperty("x-apikey", API_KEY);

        InputStream getResponse = getConn.getInputStream();
        JSONObject reportJson = new JSONObject(new JSONTokener(getResponse));
        JSONObject data = reportJson.getJSONObject("data");
        JSONObject attr = data.getJSONObject("attributes");

        JSONObject stats = attr.getJSONObject("stats");
        int harmless = stats.optInt("harmless", 0);
        int malicious = stats.optInt("malicious", 0);
        int suspicious = stats.optInt("suspicious", 0);
        int undetected = stats.optInt("undetected", 0);

        // Engine detection summary
        JSONObject results = attr.getJSONObject("results");
        List<String> flaggedEngines = new ArrayList<>();

        for (String engine : results.keySet()) {
            JSONObject engineResult = results.getJSONObject(engine);
            String category = engineResult.optString("category");
            if (category.equals("malicious") || category.equals("suspicious")) {
                flaggedEngines.add(engine);
            }
        }

        // Set results
        result.put("malicious", malicious);
        result.put("suspicious", suspicious);
        result.put("harmless", harmless);
        result.put("undetected", undetected);
        result.put("engines", flaggedEngines);

        boolean isSafe = (malicious == 0 && suspicious == 0);
        result.put("isSafe", isSafe);
        return result;
    }
}
