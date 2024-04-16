import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static String translate(String dialog) {
        String kfInfo = "Friends are human girls with animal-like features. They are animals that have transformed into human girls through a substance called Sandstar. While retaining influences from their animal form, such as ears, tail, wings (for bird friends), and personality traits, friends do not have paws, claws, or fur and are fully human. There are friends representing both extant and extinct animal species. Sandstar, a mineral, interacts with animals to create friends in their human form. Japari Park is an island safari that encompasses various biomes and ecosystems, housing the corresponding animal and plant life. At the center of the park is a large mountain, which generates Sandstar at its peak with crystalline structures extending beyond it. Celliens are cell-like alien creatures with the sole goal of consuming friends, causing them to revert to their animal form.";

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        String prompt = "Translate the following story into English. Do not add additional information, keep the story intact. \"セルリアン\" is \"Cellien\". -\n\n";
        JSONArray messages = new JSONArray();

        JSONObject systemPrompt = new JSONObject();
        systemPrompt.put("role", "user");
        systemPrompt.put("content", prompt + dialog);
        messages.put(systemPrompt); //put the system prompt first

        /*JSONObject dialogMessage = new JSONObject();
        dialogMessage.put("role", "user");
        dialogMessage.put("content", "Additional context for the story is as follows -\n" + kfInfo);
        messages.put(dialogMessage); //put the dialog to be summarize second */

        JSONObject parameters = new JSONObject();
        parameters.put("max_tokens", 550);
        parameters.put("messages", messages);

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        String output;
        try {
            URL url = new URL("http://localhost:8080/v1/chat/completions");
            Request request = new Request.Builder().url(url).post(requestBody).build();
            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            JSONObject jsonObject = new JSONObject(responseContent);
            output = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    public static String summarize(String dialog) {
        String kfInfo = "Friends are human girls with animal-like features. They are animals that have transformed into human girls through a substance called Sandstar. While retaining influences from their animal form, such as ears, tail, wings (for bird friends), and personality traits, friends do not have paws, claws, or fur and are fully human. There are friends representing both extant and extinct animal species. Sandstar, a mineral, interacts with animals to create friends in their human form. Japari Park is an island safari that encompasses various biomes and ecosystems, housing the corresponding animal and plant life. At the center of the park is a large mountain, which generates Sandstar at its peak with crystalline structures extending beyond it. Celliens are cell-like alien creatures with the sole goal of consuming friends, causing them to revert to their animal form.";

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        String prompt = "Create a complete summary for the following story. " +
                "Explain what happens, and tell what events occurred within one paragraph -\n\n";
        JSONArray messages = new JSONArray();

        JSONObject systemPrompt = new JSONObject();
        systemPrompt.put("role", "user");
        systemPrompt.put("content", prompt + dialog);
        messages.put(systemPrompt); //put the system prompt first

        /*JSONObject dialogMessage = new JSONObject();
        dialogMessage.put("role", "user");
        dialogMessage.put("content", "Translate the dialog into English.");
        messages.put(dialogMessage); //put the dialog to be summarize second */

        JSONObject parameters = new JSONObject();
        parameters.put("max_tokens", 550);
        parameters.put("messages", messages);

        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        String output;
        try {
            URL url = new URL("http://localhost:8080/v1/chat/completions");
            Request request = new Request.Builder().url(url).post(requestBody).build();
            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            JSONObject jsonObject = new JSONObject(responseContent);
            output = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }
}
