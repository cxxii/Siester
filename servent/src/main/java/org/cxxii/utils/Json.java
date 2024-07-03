package org.cxxii.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.bytebuddy.description.method.MethodDescription;
import org.cxxii.messages.PingMessage;
import org.cxxii.server.Server;
import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Json {

    private final static Logger LOGGER = LoggerFactory.getLogger(Json.class);

    private static ObjectMapper myObjectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return om;
    }

    public static JsonNode parse(String jsonSrc) throws IOException {
        return myObjectMapper.readTree(jsonSrc);
    }

    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
        return myObjectMapper.treeToValue(node, clazz);
    }

    public static JsonNode toJson(Object obj) {
        return myObjectMapper.valueToTree(obj);
    }

    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
        return generateJson(node, true);
    }

    private static String generateJson(Object o, boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWriter = myObjectMapper.writer();
        if (pretty) {
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        }

        return objectWriter.writeValueAsString(o);
    }

    public static JsonObject readJsonFromFile(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    public static void appendToHostCacheJson(InetSocketAddress address) throws IOException {
        final String FILE_PATH = String.valueOf(FileManager.getHostCachePath());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<SocketAddr>>() {}.getType();

        List<SocketAddr> sockets;
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement.isJsonArray()) {
                sockets = gson.fromJson(jsonElement, listType);
            } else {
                sockets = new ArrayList<>();
            }
        }

        // TODO - GET PORT FROM SETTINGS
        SocketAddr newSocket = new SocketAddr(address.getAddress(), 6364);
        sockets.add(newSocket);

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(sockets, writer);
        }
    }

    public static void writeDefaultHostDetails() throws IOException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        HostDetailsJson hostDetailsJson = new HostDetailsJson(0, 0);

        try (FileWriter writer = new FileWriter(FileManager.getHostDetailsPath().toFile())) {
            gson.toJson(hostDetailsJson, writer);
            LOGGER.info("Default host details written");
        } catch (IOException e) {
            LOGGER.error("Failed to write default host details: ", e);
            throw new RuntimeException("ERROR writing JSON host details: ", e);
        }
    }


    public static void writeNodePongDetails() throws IOException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        HostDetailsJson hostDetailsJson = new HostDetailsJson(0, 0);

        try (FileWriter writer = new FileWriter(FileManager.getHostDetailsPath().toFile())) {
            gson.toJson(hostDetailsJson, writer);
            LOGGER.info("Set Default host details: FilesShared = 0, KbShared = 0");
        } catch (IOException e) {
            LOGGER.error("Failed to write default host details: ", e);
            throw new RuntimeException("ERROR writing JSON host details: ", e);
        }
    }

    public static JsonObject readJsonFromClasspath(String classpath) throws IOException {
        try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream(classpath);
             Reader reader = new InputStreamReader(inputStream)) {
            if (inputStream == null) {
                throw new FileNotFoundException(classpath + " not found in classpath");
            }
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

}
