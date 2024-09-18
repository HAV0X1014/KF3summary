import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Summarize {
    public static void summarize(File[] files, String folderPath, String id, String outputPrefix) {
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
                JSONObject scenario = new JSONObject(FileHandler.read(folderPath + file.getName()));
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
            StringBuilder names = new StringBuilder();
            List<Object> charaNamesList = charaNames.toList().stream().distinct().collect(Collectors.toList());

            //look up the name of the character in the Map and match that to their respective friend ID and english name
            for (Object name : charaNamesList.toArray()) {
                JSONObject result = SummaryMain.nameMap.get(name.toString());
                if (result != null) {
                    String nameEn = result.getString("nameEn");
                    names.append(name).append("|").append(nameEn).append(", ");
                    //format is "nameJP|nameEN,"
                }
            }

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
                                        //if the name we are looking up (the current speaking character's name) ISNT in
                                        //the name map, then dont add it to the dialog pair. (cellien, human characters, etc)
                                        JSONObject result = SummaryMain.nameMap.get(charName);
                                        if (result == null) {
                                            dialog = charName + ": " + cleanedSentence + "\n";
                                        } else {
                                            dialog = charName + "|" + result.getString("nameEn") + ": " + cleanedSentence + "\n";
                                        }
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
            String summarizedScene = GeminiAI.send(fullScene.toString(), SummaryMain.idMap.get(Integer.parseInt(id)).getString("nameEn"));  //send the scenarios to the AI
            System.out.println(summarizedScene);

            //now write the summary into a file
            String filename = "summary/" + outputPrefix + id + ".txt";
            FileHandler.write(filename, summarizedScene + "\n[End of Summary.]\n[Involved Characters - " + names.toString() + "]");
        }
    }
}
