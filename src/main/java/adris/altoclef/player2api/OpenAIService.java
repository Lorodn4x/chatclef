package adris.altoclef.player2api;

import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OpenAIService {
    private static final String BASE_URL = "http://193.233.114.29:4000/v1";
    private static final String API_KEY = "sk-OkYhcpYUhwINidb2I_9yaA";
    private static final String MODEL = "gpt-oss-120b";
    private static final String TTS_MODEL = "tts-1";

    /**
     * Handles boilerplate logic for interacting with the OpenAI-compatible API endpoint
     *
     * @param endpoint The API endpoint (e.g., "/chat/completions").
     * @param postRequest True -> POST request, False -> GET request
     * @param requestBody JSON payload to send.
     * @return A map containing JSON keys and values from the response.
     * @throws Exception If there is an error.
     */
    private static Map<String, JsonElement> sendRequest(String endpoint, boolean postRequest, JsonObject requestBody) throws Exception {
        URL url = new URI(BASE_URL + endpoint).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(postRequest ? "POST" : "GET");

        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        if (postRequest && requestBody != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode + ": " + connection.getResponseMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();
        Map<String, JsonElement> responseMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonResponse.entrySet()) {
            responseMap.put(entry.getKey(), entry.getValue());
        }

        return responseMap;
    }

    /**
     * Handles a chat completion request using the OpenAI-compatible API.
     *
     * @param conversationHistory The conversation history object.
     * @return The AI's response as a JSON object.
     * @throws Exception If there is an error.
     */
    public static JsonObject completeConversation(ConversationHistory conversationHistory) throws Exception {
        JsonObject requestBody = new JsonObject();
        JsonArray messagesArray = new JsonArray();
        for (JsonObject msg : conversationHistory.getListJSON()) {
            messagesArray.add(msg);
        }

        requestBody.add("messages", messagesArray);
        requestBody.addProperty("model", MODEL);
        requestBody.addProperty("max_tokens", 1000);
        requestBody.addProperty("temperature", 0.7);

        Map<String, JsonElement> responseMap = sendRequest("/chat/completions", true, requestBody);
        if (responseMap.containsKey("choices")) {
            JsonArray choices = responseMap.get("choices").getAsJsonArray();

            if (choices.size() != 0) {
                JsonObject messageObject = choices.get(0).getAsJsonObject().getAsJsonObject("message");

                if (messageObject != null && messageObject.has("content")) {
                    String content = messageObject.get("content").getAsString();
                    return Utils.parseCleanedJson(content);
                }
            }
        }

        throw new Exception("Invalid response format: " + responseMap.toString());
    }

    /**
     * Handles a chat completion request and returns the response as a plain string.
     *
     * @param conversationHistory The conversation history object.
     * @return The AI's response as a string.
     * @throws Exception If there is an error.
     */
    public static String completeConversationToString(ConversationHistory conversationHistory) throws Exception {
        JsonObject requestBody = new JsonObject();
        JsonArray messagesArray = new JsonArray();
        for (JsonObject msg : conversationHistory.getListJSON()) {
            messagesArray.add(msg);
        }

        requestBody.add("messages", messagesArray);
        requestBody.addProperty("model", MODEL);
        requestBody.addProperty("max_tokens", 1000);
        requestBody.addProperty("temperature", 0.7);

        Map<String, JsonElement> responseMap = sendRequest("/chat/completions", true, requestBody);
        if (responseMap.containsKey("choices")) {
            JsonArray choices = responseMap.get("choices").getAsJsonArray();

            if (choices.size() != 0) {
                JsonObject messageObject = choices.get(0).getAsJsonObject().getAsJsonObject("message");

                if (messageObject != null && messageObject.has("content")) {
                    String content = messageObject.get("content").getAsString();
                    return content;
                }
            }
        }

        throw new Exception("Invalid response format: " + responseMap.toString());
    }

    /**
     * Creates a default character since we no longer fetch from API.
     * This can be made configurable later.
     *
     * @return A default Character object.
     */
    public static Character getDefaultCharacter() {
        return new Character(
            "AI Assistant", 
            "AI", 
            "Hello! I'm your AI assistant ready to help you in Minecraft.", 
            "You are a helpful AI assistant that can play Minecraft and help users with various tasks.",
            new String[]{"alloy"} // Default OpenAI voice
        );
    }

    /**
     * Text-to-speech using OpenAI-compatible /audio/speech endpoint.
     * Note: This implementation assumes the LiteLLM proxy supports TTS.
     *
     * @param message The text to convert to speech.
     * @param character The character (used for voice selection).
     */
    public static void textToSpeech(String message, Character character) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", TTS_MODEL);
            requestBody.addProperty("input", message);
            
            // Use character's primary voice
            String voice = character.getPrimaryVoice();
            requestBody.addProperty("voice", voice);
            requestBody.addProperty("response_format", "mp3");

            System.out.println("Sending TTS request: " + message);
            
            // Note: This endpoint returns binary audio data, not JSON
            // For now, we'll just send the request. Real implementation would need
            // to handle binary response and play the audio.
            sendRequest("/audio/speech", true, requestBody);

        } catch (Exception e) {
            System.err.println("TTS Error: " + e.getMessage());
            // Fail silently for now
        }
    }

    /**
     * Speech-to-Text start using the enhanced OpenAI STT service.
     */
    public static void startSTT() {
        OpenAISTTService.startSTT();
    }

    /**
     * Speech-to-Text stop using the enhanced OpenAI STT service.
     */
    public static String stopSTT() {
        return OpenAISTTService.stopSTT();
    }

    /**
     * Health check is not needed for OpenAI API, but keeping for compatibility.
     */
    public static void sendHeartbeat() {
        // OpenAI doesn't need heartbeats, but we can keep this as a no-op
        // or implement a simple connection test
        System.out.println("Heartbeat not required for OpenAI API");
    }
}
