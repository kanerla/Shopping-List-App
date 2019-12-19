package objectorientedprogramming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for reading and writing JSON files.
 * 
 * @author Laura Kanerva.
 */
public class JsonUtil {
    private int index;
    private JsonObject thisJson;
    private String line;

    /**
     * Empty constructor.
     */
    public JsonUtil() {

    }

    /**
     * Saves the JSONObjects to a .json file.
     * 
     * @param file the file user chose for saving the json
     * @param jsons list of jsons to be saved in the file
     */
    public void writeToJson(File file, JsonObject[] jsons) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(Arrays.toString(jsons));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads the .json file and creates a JSONObject from its content.
     * 
     * @param file the file user wants to read
     * @return the object created from file content
     */
    public ArrayList<JsonObject> readJson(File file) {
        FileReader fr = null; 
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = "";
        try {
            String thisLine = br.readLine();
            while(thisLine != null) {
                str += thisLine.strip();
                thisLine = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        str = removeWhiteSpace(str);

        return stringToJson(str);
    }

    /**
     * Removes all white space from the given string.
     * 
     * @param str string with possible, useless white space
     * @return string without any useless white space
     */
    public String removeWhiteSpace(String str) {
        String noWhiteSpace = "";
        boolean reading = false;
        for(int i = 0; i < str.length(); i++) {
            char current = str.charAt(i);
            if ((current == ' ' || current == '\t') && !reading) {
                continue;
            } if (current == '"') {
                reading = !reading;
            }
            noWhiteSpace += current;
        }
        return noWhiteSpace;
    }

    /**
     * Creates a JSONObject from given string.
     * 
     * @param str the whole json file in a string form
     * @return the JSONObject created from str content
     */
    public ArrayList<JsonObject> stringToJson(String str) {
        thisJson = null;
        index = 1;
        line = str;

        ArrayList<JsonObject> jsons = new ArrayList<>();

        while (index < line.length()) { //käy koko linen läpi
            if(line.charAt(index++) == '{') { //aloittaa uuden objectin
                thisJson = createObject();
                jsons.add(thisJson);
            }
        }
        return jsons;
    }

    /**
     * Adds keys and values to the JSONObject.
     *
     * @return the created JSONobject
     */
    public JsonObject createObject() {
        JsonObject jo = new JsonObject();
        while(true) {
            String key = readString();
            index ++; // :
            Object value = readValue();
            jo.add(key, value);
            if(line.charAt(index) == '}') {
                break;
            } else {
                index ++;
            }
        }
        return jo; 
    }

    /**
     * Reads a single string from line.
     *
     * @return String read from line.
     */
    public String readString() {
        String str = "";
        if(line.charAt(index) == '"') {
            index++;
        }
        while(true) {
            char c = line.charAt(index++);
            if(c == '"') {
                break;
            }
            str += c;
        }
        return str;
    }

    /**
     * Reads value that can be either a string or an integer.
     *
     * @return either a string or an integer, detected from line.
     */
    public Object readValue() {
        if(line.charAt(index) == '"') {
            return readString();
        } else {
            String number = "";
            while(true) {
                char c = line.charAt(index);
                if(c == '}' || c == ',') {
                    break;
                }
                index++;
                number += c;
            }
            return Integer.parseInt(number);
        }
    }
}