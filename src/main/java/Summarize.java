import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class Summarize {
    public static void summarize(File[] files, String folderPath,String id,String outputPrefix) {
        Arrays.sort(files, Comparator.naturalOrder());
        int half = files.length;
        int stoppingPoint = half;
        for (int iter = 0; iter < 1; iter++) {
            JSONArray charaNames = new JSONArray();                 //holds all of the character names used + duplicates
            int startingPoint = 0;
            if (iter > 0) {
                //if we have already iterated once, start from the old stopping point
                startingPoint = stoppingPoint;
                stoppingPoint = stoppingPoint + half;
            }
            JSONArray allScenarios = ReadScenario.read(files, folderPath, startingPoint, stoppingPoint);

            //this loop gets the character names from the currently available scenarios and puts them in charaName
            for (int s = 0; s < allScenarios.length(); s++) {
                JSONObject scenario = allScenarios.getJSONObject(s);
                for (int i = 0; i < scenario.getJSONArray("charaDatas").length(); i++) {
                    charaNames.put(scenario.getJSONArray("charaDatas").getJSONObject(i).getString("name"));
                }
            }

            JSONArray uniqueNames = RemoveDuplicates.removeDuplicates(charaNames);   //remove duplicate names to not confuse the AI
            String names = uniqueNames.toString().replaceAll("[\\[\\]]", "");    //remove the brackets

            String fullScene = ParseDialog.parse(allScenarios);
            FileHandler.write("summary/untranslated/" + outputPrefix + id + ".txt", fullScene);

            System.out.println("---");
            System.out.println("Summarizing " + id + "...");
            //String translatedNames = Translate.translator(names);   //translate the names with google translate first UNUSED
            String summarizedScene = GeminiAI.send(fullScene, names, "");  //send the scenarios to the AI
            System.out.println(summarizedScene);

            //now write the summary into a file
            String filename = "summary/" + outputPrefix + id + ".txt";
            FileHandler.write(filename, summarizedScene + "\n[End of Summary.]\n[Involved Characters - " + names + "]");
        }
    }
}
