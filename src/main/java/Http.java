import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Http implements HttpClient {

    private String urlParams(Map<String, String> params, String url) {
        url += "?";
        for (String key : params.keySet()) {
            url += key + "=" + params.get(key) + "&";
        }
        return url;
    }

    public String get(String url, Map<String, String> headers, Map<String, String> params) {
        try {
            URL getUrl = new URL(urlParams(params,url));
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            System.out.println(connection.getResponseCode());


            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            )) {
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
            }
            connection.disconnect();
            return content.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    String jsonInputString(Map<String, String> params){
        int count = 1;
        String jsonInputString = "{";
        for (String key : params.keySet()) {
            jsonInputString += "\"" + key + "\"" + ":" + "\"" + params.get(key) + "\"";
            if (count < params.keySet().size()) {
                jsonInputString += ",";
            } else {
                jsonInputString += "}";
            }
            count++;
        }
        return jsonInputString;
    }

    public String post (String url, Map < String, String > headers, Map < String, String > params){
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

            connection.setRequestMethod("POST");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            String jsonInputString = jsonInputString(params);
            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

