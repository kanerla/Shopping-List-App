package objectorientedprogramming;

import java.util.HashMap;
import java.util.Map;

/**
 * A JSONObject class.
 * 
 * @author Laura Kanerva.
 */
public class JsonObject{
    private Map<String, Object> jsonMap = new HashMap<>();

    /**
     * Empty constructor.
     */
    public JsonObject() {

    }

    /**
     * Adds key-value pairs to the jsonMap.
     * 
     * @param key determines what value is
     * @param value any given value for the key
     */
    public void add(String key, Object value) {
        jsonMap.put(key, value);
    }

    /**
     * Returns JSONObject's Map.
     * 
     * @return Map with all added keys and values
     */
    public Map<String, Object> getMap() {
        return jsonMap;
    }

    /**
     * Creates a string in JSON form from jsonMap keys and values.
     * 
     * @return string in JSON form
     */
    public String toString() {
        String json = "{\n";
        int i = 0;
        for(Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Object value = entry.getValue();
            if(value instanceof String) {
                value = "\"" + value + '"';
            }
            json += "\t\"" + entry.getKey() + "\": " + value;
            if(i < jsonMap.size() - 1) {
                json += ",\n";
            }
            i++;
        }
        json += "\n}";
        return json;
    }

}