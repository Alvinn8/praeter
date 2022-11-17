package ca.bkaw.praeter.core.resources.font;

import com.google.gson.JsonObject;

/**
 * A provider for characters in a font.
 *
 * @see Font
 * @see BitmapFontProvider
 * @see SpaceFontProvider
 */
public interface FontProvider {
    /**
     * Get the json object for this provider.
     *
     * @return The json.
     */
    JsonObject asJsonObject();
}
