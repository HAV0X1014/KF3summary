import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadScenario {
    public static JSONArray read(File[] files, String folderPath, int startingPoint, int stoppingPoint) {
        JSONArray allScenarios = new JSONArray();
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
            allScenarios.put(scenario);
        }
        return allScenarios;
    }
}
