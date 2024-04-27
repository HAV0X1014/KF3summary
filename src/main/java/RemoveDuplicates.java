import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

public class RemoveDuplicates {
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
