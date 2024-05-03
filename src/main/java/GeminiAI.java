import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class GeminiAI {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static String send(String dialog, String untranslatedNames, String translatedNames) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        JSONObject payload = new JSONObject();          //holds the whole object of JSON sent to google
        JSONArray contents = new JSONArray();           //holds the text content sent to the AI
        //how gemini works is that the turns HAVE to be user -> model -> user -> model -> user and ALWAYS start and end with user
        //its really stupid, and making the JSON for this is a pain in the ass.

        /*
        //this would have been for a "system" prompt but apparently gemini doesnt have that.
        //not going to delete this but instead i shall comment it out.
        JSONObject modelPrompt = new JSONObject();
        modelPrompt.put("role", "model");
        JSONArray parts = new JSONArray();
        JSONObject text = new JSONObject();
        text.put("text","Answer the request.");
        parts.put(text);
        modelPrompt.put("parts", parts);
        contents.put(modelPrompt);
        */

        JSONObject userPrompt = new JSONObject();
        userPrompt.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        userText.put("text", "Create a complete summary for the following story: \n" +
                "- Respond only in English.\n" +
                "- The summary should be 8-12 paragraphs in length, and include all major events.\n" +
                "- Be thorough describing the events.\n" +
                "- Specify which characters are involved in each event.\n" +
                "- Be specific about the tone of the characters. Describe their expressions and tone in detail.\n" +
                "- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n" +
                "\n\n" + dialog + "\n\nSummarize the story, and respond in English.");

                /*"This story happens in the Kemono Friends 3 mobile game, where human girls with animal features go on adventures, and fight against Celliens. " +
                "Respond in english. The characters involved are " + names + ". " +
                "Be specific about actions and events that occurred. Create a complete summary for the following story.  ---\n\n" + dialog +
                "\n\n---\n\nSummarize the story, and respond in English."); */

        userParts.put(userText);
        userPrompt.put("parts", userParts);
        contents.put(userPrompt);
        //this hunk of shit puts the text into googles special little array with an object inside or something
        //i hate google's stupid fucking API formatting

        payload.put("contents",contents);

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        String output;
        try {
            //to change model, change the gemini-1.5-pro-latest thing to whichever model you want.
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + ReadKey.read("APIKey"));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            String responseContent;
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            //you know what i hate more than googles INPUT json?????? OUTPUT JSON!!!!
            JSONObject response = new JSONObject(responseContent);
            //System.out.println(responseContent);
            output = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }
}
