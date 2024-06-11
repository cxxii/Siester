package org.cxxii.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.cxxii.util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigManager {

    private static ConfigManager myConfigManager;
    private static Config myCurrentconfig;

    private ConfigManager() {
    }

    public static ConfigManager getInstance()
        {
            if (myConfigManager == null)
                myConfigManager = new ConfigManager();
            return myConfigManager;
        }

    /*Used to load a config file via path provided*/
    public void loadConfigFile(String filePath) {

        FileReader fileReader = null;

        try {
            fileReader = new FileReader(filePath);

        } catch (FileNotFoundException e) {

            throw new HttpConfigException(e);
        }

        StringBuffer sb = new StringBuffer();
        int i;

        try {
            while ((i = fileReader.read()) != -1) {
                sb.append((char)i);
            }
        } catch (IOException e) {

            throw new HttpConfigException(e);
        }

        JsonNode conf = null;

        try {
            conf = Json.parse(sb.toString());
        } catch (IOException e) {
            throw new HttpConfigException("Error Paring the config file", e);
        }

        try {
            myCurrentconfig = Json.fromJson(conf, Config.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigException("error paring the config file, internal", e);
        }
    }


    /*Returns the current loaded config*/
    public Config getCurrentconfig() {
        if ( myCurrentconfig == null ) {
            throw new HttpConfigException("No current config Set");

        }

        return myCurrentconfig;
    }
}
