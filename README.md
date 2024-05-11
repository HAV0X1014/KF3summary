# KF3summary
A Java program to summarize the character (and possibly main) stories.

## Building
Clone or download the repo.

Run `gradlew shadowJar` to compile the program into an executable JAR.

Get the JAR from `build/libs/KF3summary-0.x-all.jar`.

## Usage
In the same directory as the JAR, create a folder named `summary`, and inside that, create a folder named `untranslated`.

Place the `char` .prefab.json scenario files in a folder named `char` in the same directory as the JAR.
Place the `main` .prefab.json scenario files in a folder named `main` in the same directory as the JAR.

Create a file named ``APIKey`` in the same directory as the JAR. It should not have a file extension. Put your Google Gemini API key in this file. You can get a free API key from https://aistudio.google.com/app/apikey .

Your file structure should look like this -
```
APIKey
KF3summary-0.x-all.jar
summary/
└── untranslated/
char/
├── scenario_c_0001_01_1_a.prefab.json
├── scenario_c_0001_01_2_a.prefab.json
├── scenario_c_0001_01_3_a.prefab.json
└── scenario_c_0001_01_1_b.prefab.json
main/
├── scenario_m_00_01_1_a.prefab.json
├── scenario_m_00_01_1_b.prefab.json
├── scenario_m_00_02_1_a.prefab.json
└── scenario_m_01_01_1_a.prefab.json
```
Once you have the correct folder structure and API key, use `java -jar KF3summary-0.x-all.jar` to run the program.

Type `main` or a character ID. If you want to summarize the main or a specific character's story. `main` will summarize the entire main story.

If you chose to summarize a character story, input the character ID for the character story you want to summarize. For example, `322` is Serval, `69` is Cheetah, `1` is Dhole. Do not add any leading zeroes.

## How it works
This works by putting the scenarios together in order, getting the relevant character names, parsing out only the dialog in a "name: text" format, saving the untranslated scenario, summarizing the untranslated story, and saving the summarized story to a file.

The summarizer AI used is Google Gemini 1.5 for its large context size. In the future, other AI services may be supported.

The summary prompt provided to the AI is as follows -
```
Create a complete summary for the following story: \n
- Respond only in English.\n
- The summary should be 4-8 paragraphs in length, and include all major events.\n
- Be thorough describing the events.\n
- Specify which characters are involved in each event.\n
- Use the characters' Japanese names when they are referred to.\n
- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n
\n\n
[dialog]
\n\nSummarize the story, and respond in English.
```
## Known issues
Only Gemini is supported.

~Rarely, the AI will ignore the input dialog. This might be a fault on the AI's side.~ This hasn't happened in a while.

Obtaining the proper character .prefab.json scenarios is difficult.
