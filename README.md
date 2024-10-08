# KF3summary
A Java program to summarize the character and main stories of Kemono Friends 3.

## Building
Clone or download the repo.

Run `gradlew shadowJar` to compile the program into an executable JAR.

Get the JAR from `build/libs/KF3summary-0.x-all.jar`.

## Usage
Obtain the story .prefab.json files from KF3. Obtain the CHARA_DATA.json file from KF3 as well (if the version on this repo is out of date).

Obtain a Google Gemini API key. You can get a free API key from https://aistudio.google.com/app/apikey .

Place the .prefab.json scenario files in a folder named in the same directory as the JAR. You should have folders named "another", "char", "event", "login", and "main".

Use `java -jar KF3summary-0.x-all.jar` to run the program. The program will generate the needed output folders and config file. Once the config.json file has been generated, put your Gemini API key where it says `GeminiAPIKey`. Do not edit `PromptOverride` unless you know what you want to do with it.

The correct file and folder structure should look like this.
```
config.json
CHARA_DATA.json
KF3summary-0.x-all.jar
summary/
└── untranslated/
another/
char/
event/
login/
main/
```

The summarization options are "main" for main story 1, "main2" for main story 2, "main3" for main story 3, "main4" for main story 4, "another" for Arai-san's story, "event" for events, and a specific character's story.

If you chose to summarize a character story, input the character ID for the character story you want to summarize. For example, `322` is Serval, `69` is Cheetah, `1` is Dhole. Enter the ID at the first prompt. The output file will be prefixed with the character's name, like ``Spectacled Caiman_char_0096.txt``.

If you choose to summarize an event, enter "event" and then the ID of the event when prompted.

## How it works
This works by putting the scenarios together in order, getting the relevant character names, parsing out only the dialog in a "nameJP|nameEN: text" format, saving the untranslated story, summarizing the untranslated story by sending it to an AI, and saving the summarized story to a file.

The summarizer AI used is Google Gemini 1.5 for its large context size. In the future, other AI services may be supported.

The summary prompt provided to the AI is as follows -
```
Create a complete summary for the following story: \n
- Respond only in English.\n
- The summary should be 5-8 paragraphs in length, and include all major events.\n
- Be thorough describing the events.\n
- Specify which characters are involved in each event.\n
- Use the characters' Japanese names when they are referred to.\n
- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n
\n\n
[dialog]
\n\nSummarize the story, and respond in English.
```
To override the default prompt, enter your custom prompt in config.json. use `\n` for newlines, and use ``{char}`` for the main character's name of a character story. Remember to remove the overridden prompt if you are summarizing a main story.

An example of an override prompt is below -

```
Create a complete overview of {char}'s personality in this story. This story highlights them and their personality while they are interacting with other Friends:\n- Respond only in English.\n- The summary should be 1-2 paragraphs in length, and include all descriptions and relative details about the character's personality.\n - Be specific about the tone, mannerisms, vocal ticks (if any), quirks, and minute details the character has.\n- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n
```

## Known issues
Only Gemini is supported.

~Rarely, the AI will ignore the input dialog. This might be a fault on the AI's side.~ This hasn't happened in a while.

Obtaining the proper character .prefab.json scenarios is difficult.

The AI cannot get some names correct. It will often incorrectly name characters - i.e. calling Dhole "doll". To remediate the issue, I have added a section at the end of the summary that contains all untranslated names of characters involved in the summarized story. Additionally, I have made the name formatting sent to the AI like "nameJP|nameEN: dialog." The CHARA_DATA.json file is what contains data for all (controllable) characters in the game, and what is used to look up the characters' english names.