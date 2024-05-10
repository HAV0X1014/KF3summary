import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class SummaryMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String folderPath = null;

        System.out.println("Do you want to summarize the Main story or one Character story?\n(input \"main\" or the ID of the Friend, format \"3\" or \"179\".)");
        String choice = sc.nextLine();
        while (!choice.equals("main") && !isInt(choice)) {
            System.out.println("Incorrect selection. Choose \"main\" or \"char\"");
            choice = sc.nextLine();
        }

        /*
        as of now, splitting chapters/character story is disabled. it will only output one
        single text file per chapter and character story. since gemini 1.5 has 1 million context tokens available,
        there really isnt any need to split the story. i think it makes the output a little worse when it is split.
        to re-enable splitting, or to make it split more - make "int half = files.length / 2" and "iter < 2"
        basically just divide the "half" point into 2 parts and edit the amount of iterations accordingly.
        i might spin off the actual summary code that is here into its own class like ReadScenario.
        */
        if (choice.equals("main")) {
            folderPath = "main/";
            for (int loopOverEveryChapter = 0; loopOverEveryChapter < 10; loopOverEveryChapter++) {
                String id = String.format("%02d",loopOverEveryChapter);
                File folder = new File(folderPath);

                File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_m_" + id + "_") && name.endsWith(".prefab.json"));
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
                    Write.write("summary/untranslated/" + id + ".txt", fullScene);

                    System.out.println("---");
                    System.out.println("Summarizing chapter " + iter + "...");
                    //String translatedNames = Translate.translator(names);   //translate the names with google translate first UNUSED
                    String summarizedScene = GeminiAI.send(fullScene.toString(), names, "");  //send the scenarios to the AI
                    System.out.println(summarizedScene);

                    //now write the summary into a file
                    String filename = "summary/" + id + ".txt";
                    Write.write(filename,summarizedScene);
                }
            }
        } else {
            folderPath = "char/";
            int idUnformattedInt = Integer.parseInt(choice);
            String id = String.format("%04d",idUnformattedInt);
            File folder = new File(folderPath);

            File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_c_" + id + "_") && name.endsWith(".prefab.json"));
            Arrays.sort(files, Comparator.naturalOrder());      //this is what puts them in the right order, lol
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
                Write.write("summary/untranslated/" + id + ".txt", fullScene);

                System.out.println("---");
                System.out.println("Summarizing...");
                //String translatedNames = Translate.translator(names);   //translate the names with google translate first UNUSED
                String summarizedScene = GeminiAI.send(fullScene, names, "");  //send the scenarios to the AI
                System.out.println(summarizedScene);

                //now write the summary into a file
                String filename = "summary/" + id + ".txt";
                Write.write(filename,summarizedScene + "\n[End of Summary.]\n[Involved Characters - " + names + "]");
            }
        }
    }
    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
