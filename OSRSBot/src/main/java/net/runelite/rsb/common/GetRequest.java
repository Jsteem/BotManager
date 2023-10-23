package net.runelite.rsb.common;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest<T> {
    private String url = "http://localhost:8080/";
    private Class<T> type;

    public GetRequest(String path, Class<T> type){
        this.url = this.url + path;
        this.type = type;
    }

    public T getResult(){
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder res = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                T info = gson.fromJson(res.toString(), type);

                return info;

            } else {
                System.out.println("Request failed. Response Code: " + responseCode);
            }

            connection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
