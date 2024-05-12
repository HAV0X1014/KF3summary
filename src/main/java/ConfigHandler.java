import org.json.JSONObject;

public class ConfigHandler {
    public static String getString(String option) {
        return SummaryMain.config.getString(option);
    }

    public static void createConfig() {
        JSONObject emptyConfig = new JSONObject();
        emptyConfig.put("GeminiAPIKey","Put your Gemini API key here");
        emptyConfig.put("PromptOverride","");
        FileHandler.write("config.json",emptyConfig.toString(2));
    }
}