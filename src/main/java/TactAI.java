import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TactAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static String summarize(String dialog) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        String prompt;
        if (ConfigHandler.getString("PromptOverride").isEmpty()) {
            prompt = "Create a complete summary for the following story: \n" +
                    "- Respond only in English.\n" +
                    "- The summary should be 5-8 paragraphs in length, and include all major events.\n" +
                    "- Be thorough describing the events.\n" +
                    "- Specify which characters are involved in each event.\n" +
                    "- Be specific about the tone of the characters. Describe their expressions and tone in detail.\n" +
                    "- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n";
        } else {
            prompt = ConfigHandler.getString("PromptOverride");
        }
        JSONArray messages = new JSONArray();

        JSONObject systemPrompt = new JSONObject();
        systemPrompt.put("role", "user"); //CURRENTLY this sets the "system" prompt to be from the user. change in future
        systemPrompt.put("content", prompt + "\n\n" + dialog + "\n\nSummarize the story, and respond in English.");
        messages.put(systemPrompt); //put the system prompt first
        //below is the actual user prompt
        /*JSONObject dialogMessage = new JSONObject();
        dialogMessage.put("role", "user");
        dialogMessage.put("content", "Translate the dialog into English.");
        messages.put(dialogMessage); //put the dialog to be summarize second */

        JSONObject parameters = new JSONObject();
        parameters.put("max_tokens", 0);
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
