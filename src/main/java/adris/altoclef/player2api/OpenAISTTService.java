package adris.altoclef.player2api;

import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Enhanced Speech-to-Text service for OpenAI API.
 * Since OpenAI requires file upload instead of streaming, we need to record audio to a file first.
 */
public class OpenAISTTService {
    private static final String BASE_URL = "http://193.233.114.29:4000/v1";
    private static final String API_KEY = "sk-OkYhcpYUhwINidb2I_9yaA";
    private static final String STT_MODEL = "whisper-1";
    
    private static final AtomicBoolean isRecording = new AtomicBoolean(false);
    private static CompletableFuture<String> recordingFuture = null;
    private static Path tempAudioFile = null;

    /**
     * Starts audio recording to a temporary file.
     * This is a placeholder implementation - in reality, you'd need to integrate
     * with Java's audio recording capabilities or use a native library.
     */
    public static void startSTT() {
        if (isRecording.get()) {
            System.out.println("STT recording already in progress");
            return;
        }

        try {
            // Create temporary audio file
            tempAudioFile = Files.createTempFile("chatclef_audio", ".wav");
            isRecording.set(true);
            
            System.out.println("STT recording started - file: " + tempAudioFile.toString());
            
            // Start recording in a separate thread
            recordingFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    // TODO: Implement actual audio recording
                    // This is a placeholder that simulates recording
                    simulateAudioRecording();
                    return "Recording completed";
                } catch (Exception e) {
                    System.err.println("Recording error: " + e.getMessage());
                    return "Recording failed";
                }
            });
            
        } catch (IOException e) {
            System.err.println("Failed to create temporary audio file: " + e.getMessage());
            isRecording.set(false);
        }
    }

    /**
     * Stops audio recording and sends the file to OpenAI for transcription.
     */
    public static String stopSTT() {
        if (!isRecording.get()) {
            return "No recording in progress";
        }

        try {
            isRecording.set(false);
            
            // Wait for recording to finish
            if (recordingFuture != null) {
                recordingFuture.get(); // Wait for completion
            }
            
            if (tempAudioFile == null || !Files.exists(tempAudioFile)) {
                return "No audio file found";
            }

            // Send file to OpenAI for transcription
            String transcription = transcribeAudioFile(tempAudioFile);
            
            // Clean up temporary file
            try {
                Files.deleteIfExists(tempAudioFile);
            } catch (IOException e) {
                System.err.println("Failed to delete temporary audio file: " + e.getMessage());
            }
            
            return transcription;
            
        } catch (Exception e) {
            System.err.println("STT error: " + e.getMessage());
            return "STT failed: " + e.getMessage();
        } finally {
            isRecording.set(false);
            tempAudioFile = null;
            recordingFuture = null;
        }
    }

    /**
     * Sends audio file to OpenAI /audio/transcriptions endpoint.
     */
    private static String transcribeAudioFile(Path audioFile) throws Exception {
        URL url = new URI(BASE_URL + "/audio/transcriptions").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        
        // Boundary for multipart/form-data
        String boundary = "----ChatClefBoundary" + System.currentTimeMillis();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
            
            // Add model field
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"model\"").append("\r\n");
            writer.append("\r\n");
            writer.append(STT_MODEL).append("\r\n");
            writer.flush();

            // Add file field
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                  .append(audioFile.getFileName().toString()).append("\"").append("\r\n");
            writer.append("Content-Type: audio/wav").append("\r\n");
            writer.append("\r\n");
            writer.flush();

            // Write file content
            Files.copy(audioFile, os);
            os.flush();
            
            writer.append("\r\n");
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode + ": " + connection.getResponseMessage());
        }

        // Read response
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse JSON response
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();
        
        if (jsonResponse.has("text")) {
            return jsonResponse.get("text").getAsString();
        } else {
            throw new Exception("No 'text' field in transcription response: " + response.toString());
        }
    }

    /**
     * Placeholder for actual audio recording implementation.
     * In a real implementation, this would use JavaSound API or a native library
     * to record audio from the microphone.
     */
    private static void simulateAudioRecording() throws InterruptedException {
        // Simulate recording for a few seconds
        int recordingTime = 0;
        while (isRecording.get() && recordingTime < 30000) { // Max 30 seconds
            Thread.sleep(100);
            recordingTime += 100;
        }
        
        // TODO: Replace with actual audio recording code
        // This would use javax.sound.sampled.AudioSystem to capture microphone input
        System.out.println("Audio recording simulation completed");
    }

    /**
     * Check if currently recording.
     */
    public static boolean isRecording() {
        return isRecording.get();
    }
}
