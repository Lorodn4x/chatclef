package adris.altoclef.player2api;

import java.util.Arrays;

public class Character {
    public final String name;
    public final String shortName;
    public final String greetingInfo;
    public final String description;
    public final String[] voiceIds;

    /**
     * Constructs a Character instance with provided values.
     *
     * @param characterName The name of the character.
     * @param shortName The short identifier for the character.
     * @param greetingInfo A description of what the greeting should look like. Note this is not what the actual greeting should be, but rather a prompt that can add to it.
     * @param description The description/personality of the character.
     * @param voiceIds An array of OpenAI-compatible voice IDs (e.g., "alloy", "echo", "fable", "onyx", "nova", "shimmer").
     */
    public Character(String characterName, String shortName, String greetingInfo, String description, String[] voiceIds) {
        this.name = characterName;
        this.shortName = shortName;
        this.greetingInfo = greetingInfo;
        this.description = description;
        // Ensure we have at least one voice, default to "alloy" if none provided
        this.voiceIds = (voiceIds != null && voiceIds.length > 0) ? voiceIds : new String[]{"alloy"};
    }

    /**
     * Convenience constructor with a single voice ID.
     */
    public Character(String characterName, String shortName, String greetingInfo, String description, String voiceId) {
        this(characterName, shortName, greetingInfo, description, new String[]{voiceId != null ? voiceId : "alloy"});
    }

    /**
     * Gets the primary voice ID for TTS.
     */
    public String getPrimaryVoice() {
        return voiceIds.length > 0 ? voiceIds[0] : "alloy";
    }


    /**
     * Returns a formatted string representation of the Character object.
     *
     * @return A string containing character details.
     */
    @Override
    public String toString() {
        return String.format(
                "Character{name='%s', shortName='%s', greeting='%s', voiceIds=%s}",
                name,
                shortName,
                greetingInfo,
                Arrays.toString(voiceIds)
        );
    }

}