import org.json.JSONArray;
import org.json.JSONObject;

public class ParseDialog {
    public static String parse(JSONArray allScenarios) {
        StringBuilder fullScene = new StringBuilder();
        String dialog = null;
        for (int i = 0; i < allScenarios.length(); i++) {
            JSONObject scenarioData = allScenarios.getJSONObject(i);
            JSONArray rowDatas = scenarioData.getJSONArray("rowDatas");
            for (Object rowData : rowDatas) {
                JSONObject jsonObject = (JSONObject) rowData;
                if (jsonObject.has("mSerifCharaName")) {
                    String charName = jsonObject.getString("mSerifCharaName");
                    if (!charName.isEmpty()) {
                        JSONArray dialogArray = jsonObject.getJSONArray("mStrParams");
                        for (Object sentence : dialogArray) {
                            if (!sentence.toString().equals("none")) {
                                if (!sentence.toString().isBlank()) {
                                    String cleanedSentence = sentence.toString().replaceAll("<.*?>", "");
                                    dialog = charName + ": " + cleanedSentence + "\n";
                                }
                            }
                        }
                        fullScene.append(dialog).append("\n");
                    }
                }
            }
        }
        return fullScene.toString();
    }
}
