package common;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@Setter
public class PostRequest {
    private String url = "http://localhost:8080/";
    private Object payload;
    private int responseCode = 1000;

    public PostRequest(String url, Object payload) {
        this.url = this.url + url;
        this.payload = payload;
    }

    public Object getResult() {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Set the request payload
            OutputStream outputStream = connection.getOutputStream();
            if(payload != null){
                Gson gson = new Gson();
                String payloadJson = gson.toJson(payload);
                outputStream.write(payloadJson.getBytes());
            }

            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            this.responseCode = responseCode;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder res = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
                reader.close();
                return res.toString();
            } else {
                System.out.println("Request failed. Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}