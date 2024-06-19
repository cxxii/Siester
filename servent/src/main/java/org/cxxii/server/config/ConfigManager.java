package org.cxxii.server.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.cxxii.server.config.Config;
import org.cxxii.server.config.ConfigException;
import org.cxxii.utils.Json;

import java.io.*;

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
//    public void loadConfigFile(String filePath) {
//
//        FileReader fileReader = null;
//
//        try {
//            fileReader = new FileReader(filePath);
//
//        } catch (FileNotFoundException e) {
//
//            throw new ConfigException(e);
//        }
//
//        StringBuffer sb = new StringBuffer();
//        int i;
//
//        try {
//            while ((i = fileReader.read()) != -1) {
//                sb.append((char)i);
//            }
//        } catch (IOException e) {
//
//            throw new ConfigException(e);
//        }
//
//        JsonNode conf = null;
//
//        try {
//            conf = Json.parse(sb.toString());
//        } catch (IOException e) {
//            throw new ConfigException("Error Paring the config file", e);
//        }
//
//        try {
//            myCurrentconfig = Json.fromJson(conf, Config.class);
//        } catch (JsonProcessingException e) {
//            throw new ConfigException("error paring the config file, internal", e);
//        }
//    }


    // OK - works above is no obsolete
    public void loadConfigFile(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            myCurrentconfig = new Gson().fromJson(reader, Config.class);
        }
    }

    /*Returns the current loaded config*/
    public Config getCurrentconfig() {
        if ( myCurrentconfig == null ) {
            throw new ConfigException("No current config Set");

        }

        return myCurrentconfig;
    }
}
