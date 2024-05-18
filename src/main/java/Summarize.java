import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Summarize {
    public static void summarize(File[] files, String folderPath,String id,String outputPrefix) {
        Arrays.sort(files, Comparator.naturalOrder());
        int half = files.length;
        int stoppingPoint = half;                                   //"half" does not mean division by two.
        for (int iter = 0; iter < 1; iter++) {
            JSONArray charaNames = new JSONArray();                 //holds all of the character names used + duplicates
            int startingPoint = 0;
            if (iter > 0) {
                //if we have already iterated once, start from the old stopping point
                startingPoint = stoppingPoint;
                stoppingPoint = stoppingPoint + half;
            }
            //this reads through all of the scenario files in the allotted space we have (from starting point to stopping point)
            //and adds the contents of each to an array that contains all of the read-through scenarios
            JSONArray allScenarios = new JSONArray();
            for (int f = startingPoint; f < stoppingPoint; f++) {
                File file = files[f];
                String fileContents = FileHandler.read(folderPath + file.getName());
                JSONObject scenario = new JSONObject(fileContents);
                allScenarios.put(scenario);
            }
            //this loop gets the character names from the currently available scenarios and puts them in charaName
            for (int s = 0; s < allScenarios.length(); s++) {
                JSONObject scenario = allScenarios.getJSONObject(s);
                for (int i = 0; i < scenario.getJSONArray("charaDatas").length(); i++) {
                    charaNames.put(scenario.getJSONArray("charaDatas").getJSONObject(i).getString("name"));
                }
            }
            //remove duplicates from the list of charaNames and also remove the brackets from that string
            String names = removeDuplicates(charaNames).toString().replaceAll("[\\[\\]]", "");

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
                                    if (!sentence.toString().isEmpty()) {
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
            FileHandler.write("summary/untranslated/" + outputPrefix + id + ".txt", fullScene.toString());

            System.out.println("---");
            System.out.println("Summarizing " + id + "...");
            //String translatedNames = Translate.translator(names);   //translate the names with google translate first UNUSED
            String summarizedScene = GeminiAI.send(fullScene.toString(), names, "");  //send the scenarios to the AI
            System.out.println(summarizedScene);

            //now write the summary into a file
            String filename = "summary/" + outputPrefix + id + ".txt";
            FileHandler.write(filename, summarizedScene + "\n[End of Summary.]\n[Involved Characters - " + names + "]");
        }
    }
    public static JSONArray removeDuplicates(JSONArray jsonArray) {
        //some nice code that claude 3 generated that works well. basically uses something that already has an
        //"add only if it doesnt exist yet" function then puts it back in a JSONArray. pretty thrifty nifty.
        Set<String> uniqueElements = new HashSet<>();
        JSONArray uniqueArray = new JSONArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            String element = jsonArray.getString(i);
            if (uniqueElements.add(element)) { // add returns true if element is not already present
                uniqueArray.put(element);
            }
        }
        return uniqueArray;
    }
}
