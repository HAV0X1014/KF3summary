import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class SummaryMain {
    public static void main(String[] args) {

        String folderPath = "char/";                            //the folder to look for the scenario files in

        Scanner sc = new Scanner(System.in);
        System.out.println("Input the ID of the chapter you want summarized. (format \"3\" or \"179\")");
        String idUnformatted = sc.nextLine();
        int idUnformattedInt = Integer.parseInt(idUnformatted);
        String id = String.format("%04d",idUnformattedInt);


        //for (int loopOverEveryChapter = 0; loopOverEveryChapter < 10; loopOverEveryChapter++) {
            //String id = String.format("%02d",loopOverEveryChapter);
            //this is used when iterating over story chapters

            //Essentially this reads all of the scenario files in order for the correct character ID. Edit the character ID.
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_c_" + id + "_") && name.endsWith(".prefab.json"));
            Arrays.sort(files, Comparator.naturalOrder());      //this is what puts them in the right order, lol

            int half = files.length / 2;
            int stoppingPoint = half;
            for (int iter = 0; iter < 2; iter++) {
                String dialog = null;
                StringBuilder fullScene = new StringBuilder();          //holds all of the scenarios put together and formatted
                JSONArray allScenarios = new JSONArray();               //holds all of the JSONs for the scenarios
                JSONArray charaNames = new JSONArray();                 //holds all of the character names used + duplicates
                int startingPoint = 0;
                if (iter > 0) {
                    //if we have already iterated once, start from the old stopping point
                    startingPoint = stoppingPoint;
                    stoppingPoint = stoppingPoint + half;
                }
                for (int f = startingPoint; f < stoppingPoint; f++) {
                    File file = files[f];
                    String fileContents = "";
                    try (BufferedReader br = new BufferedReader(new FileReader(folderPath + file.getName()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            fileContents += line;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //for the scenario we just got, save the character names and then add this scenario to all of the other scenarios
                    JSONObject scenario = new JSONObject(fileContents);
                    for (int i = 0; i < scenario.getJSONArray("charaDatas").length(); i++) {
                        charaNames.put(scenario.getJSONArray("charaDatas").getJSONObject(i).getString("name"));
                    }
                    allScenarios.put(scenario);
                }

                JSONArray uniqueNames = removeDuplicates(charaNames);   //remove duplicate names to not confuse the AI
                String names = uniqueNames.toString().replaceAll("[\\[\\]]", "");    //remove the brackets

                //this hunk of shit parses the dialog down to only the spoken words and formats it like "name: text." it works.
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
                try (FileWriter tfw = new FileWriter("summary/untranslated/" + id + "(" + (iter + 1) + ").txt")) {
                    BufferedWriter bw = new BufferedWriter(tfw);
                    bw.write(fullScene.toString());
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println(fullScene);                          //mostly here for debug
                System.out.println("---");
                System.out.println("Summarizing half " + (iter + 1) + "...");
                //String translatedScene = TactAI.translate(fullScene.toString());
                String translatedNames = Translate.translator(names);   //translate the names with google translate first
                String summarizedScene = GeminiAI.send(fullScene.toString(), names, translatedNames);  //send the scenarios to the AI
                System.out.println(summarizedScene);

                //now write the summary into a file
                String filename = "summary/" + id + "(" + (iter + 1) + ").txt";
                try (FileWriter fw = new FileWriter(filename)) {
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(summarizedScene);
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            //}
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
