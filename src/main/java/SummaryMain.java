import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class SummaryMain {
    public static JSONObject config;
    public static final File SummaryDirs = new File("summary/untranslated/");
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String folderPath;
        //setup process. if the config doesnt exist, make it. if the directories dont exist, make them.
        if (!new File("config.json").exists()) {
            ConfigHandler.createConfig();
            System.out.println("**The config file has been created. Place your API key in config.json to use the summarizer!**\n");
        }
        if (!SummaryDirs.exists()) {
            SummaryDirs.mkdirs();
        }

        //user input and validation
        System.out.println("Choose which story you want to summarize. Type \"main\", \"main2\", \"main3\", \"main4\", \"another\", \"event\" or a Friend ID.\n" +
                "(To summarize a Friend story, input the ID of the Friend, format \"3\" or \"179\". \"event\" requires an ID for the event.)");
        String choice = sc.nextLine();
        while (!choice.equals("main") && !choice.equals("main2") && !choice.equals("main3") && !choice.equals("main4") && !choice.equals("another") && !choice.equals("event") && !isInt(choice)) {
            System.out.println("Incorrect selection. Choose one of the above choices.");
            choice = sc.nextLine();
        }

        //read the config here, as the user should have entered it by now.
        String configString = FileHandler.read("config.json");
        config = new JSONObject(configString.trim());

        switch (choice) {
            case "main":
                folderPath = "main/";
                for (int loopOverEveryChapter = 0; loopOverEveryChapter < 10; loopOverEveryChapter++) {
                    String id = String.format("%02d", loopOverEveryChapter);
                    File folder = new File(folderPath);
                    File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_m_" + id + "_") && name.endsWith(".prefab.json"));
                    Summarize.summarize(files, folderPath, id, "main_");
                }
                break;
            case "main2":
                folderPath = "main/";
                for (int loopOverEveryChapter = 1; loopOverEveryChapter < 6; loopOverEveryChapter++) {
                    String id = String.format("%02d", loopOverEveryChapter);
                    File folder = new File(folderPath);
                    File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_m_002_" + id + "_") && name.endsWith(".prefab.json"));
                    Summarize.summarize(files, folderPath, id, "main2_");
                }
                break;
            case "main3":
                folderPath = "main/";
                for (int loopOverEveryChapter = 0; loopOverEveryChapter < 8; loopOverEveryChapter++) {
                    String id = String.format("%02d", loopOverEveryChapter);
                    File folder = new File(folderPath);
                    File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_m_003_" + id + "_") && name.endsWith(".prefab.json"));
                    Summarize.summarize(files, folderPath, id, "main3_");
                }
                break;
            case "main4":
                folderPath = "main/";
                for (int loopOverEveryChapter = 0; loopOverEveryChapter < 5; loopOverEveryChapter++) {
                    String id = String.format("%02d", loopOverEveryChapter);
                    File folder = new File(folderPath);
                    File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_m_004_" + id + "_") && name.endsWith(".prefab.json"));
                    Summarize.summarize(files, folderPath, id, "main4_");
                }
                break;
            case "another":
                folderPath = "another/";
                for (int loopOverEveryChapter = 1; loopOverEveryChapter < 7; loopOverEveryChapter++) {
                    String id = String.format("%02d", loopOverEveryChapter);
                    File folder = new File(folderPath);
                    File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_a_" + id + "_") && name.endsWith(".prefab.json"));
                    Summarize.summarize(files, folderPath, id, "another_");
                }
                break;
            case "event": {
                folderPath = "event/";
                System.out.println("Input the ID of the event you want to summarize.");
                String id = sc.nextLine();
                
                File folder = new File(folderPath);
                File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_e_" + id + "_") && name.endsWith(".prefab.json"));
                Summarize.summarize(files, folderPath, id, "event_");
                break;
            }
            default: {
                folderPath = "char/";
                int idUnformattedInt = Integer.parseInt(choice);
                String id = String.format("%04d", idUnformattedInt);
                File folder = new File(folderPath);
                File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_c_" + id + "_") && name.endsWith(".prefab.json"));
                Summarize.summarize(files, folderPath, id, "char_");
                break;
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
