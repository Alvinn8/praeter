package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.resources.pack.JsonResource;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * A font in a {@link ResourcePack}.
 */
public class Font {
    private final ResourcePack pack;
    private final NamespacedKey key;
    private final JsonResource fontJson;
    private SpaceFontProvider spaceProvider;

    /**
     * Load a font by namespaced key.
     * <p>
     * Will create the font if it does not already exist.
     *
     * @param pack The pack of the font.
     * @param key The key of the font.
     * @throws IOException If an I/O error occurs.
     */
    public Font(ResourcePack pack, NamespacedKey key) throws IOException {
        this.pack = pack;
        this.key = key;
        Path path = this.pack.getPath("assets")
            .resolve(key.getNamespace())
            .resolve("font")
            .resolve(key.getKey() + ".json");
        if (Files.exists(path)) {
            this.fontJson = new JsonResource(this.pack, path);
        } else {
            Files.createDirectories(path.getParent());

            JsonObject json = new JsonObject();
            json.add("providers", new JsonArray());

            this.fontJson = new JsonResource(this.pack, path, json);
            this.fontJson.save();
        }
    }

    /**
     * Get the resource pack the font is in.
     *
     * @return The resource pack.
     */
    public ResourcePack getPack() {
        return this.pack;
    }

    /**
     * Get the key of the font.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }

    /**
     * Add the specified font provider to this font.
     *
     * @param provider The provider.
     * @throws IOException If an I/O error occurs.
     */
    public void addProvider(FontProvider provider) throws IOException {
        JsonObject json = this.fontJson.getJson();
        JsonArray providers = json.getAsJsonArray("providers");
        providers.add(provider.asJsonObject());
        this.fontJson.save();
    }

    /**
     * Add the bitmap font character identifier by using the next free character.
     *
     * @param bitmapFontChar The font character to add.
     * @throws IOException If an I/O error occurs.
     */
    public void addFontChar(BitmapFontCharIdentifier bitmapFontChar) throws IOException {
        BitmapFontProvider provider = new BitmapFontProvider(
            bitmapFontChar.textureKey(),
            bitmapFontChar.height(),
            bitmapFontChar.ascent(),
            this.getNextCharAsList()
        );
        JsonArray providers = this.fontJson.getJson().getAsJsonArray("providers");
        for (JsonElement element : providers) {
            JsonObject json = element.getAsJsonObject();
            if (provider.isEqual(json)) {
                // The font character is already present
                return;
            }
        }
        this.addProvider(provider);
    }

    /**
     * Add a new space font character by using the next free character.
     *
     * @param spaceFontChar The font character to add.
     * @throws IOException If an I/O error occurs.
     */
    public void addFontChar(SpaceFontCharIdentifier spaceFontChar) throws IOException {
        int advance = spaceFontChar.advance();
        // Use the shared space provider for this font
        if (this.spaceProvider == null) {
            this.spaceProvider = new SpaceFontProvider();
            this.addProvider(this.spaceProvider);
        }
        // Only add if it does not already exist
        if (!this.spaceProvider.has(advance)) {
            char c = this.getNextChar();
            this.spaceProvider.add(c, advance);
            this.fontJson.save();
        }
    }

    /**
     * Get the next free character to use.
     *
     * @return The character.
     */
    public char getNextChar() {
        int i = 0xe001;
        JsonArray providers = this.fontJson.getJson().getAsJsonArray("providers");
        freeValueLoop:
        while (true) {
            char c = (char) i;
            for (JsonElement element : providers) {
                JsonObject provider = element.getAsJsonObject();
                switch (provider.get("type").getAsString()) {
                    case "bitmap" -> {
                        JsonArray chars = provider.getAsJsonArray("chars");
                        for (JsonElement element2 : chars) {
                            if (element2.getAsString().indexOf(c) >= 0) {
                                // This character is occupied, lets increment and try again
                                i++;
                                continue freeValueLoop;
                            }
                        }
                    }
                    case "space" -> {
                        JsonObject advances = provider.getAsJsonObject("advances");
                        if (advances.has(String.valueOf(c))) {
                            // This character is occupied, lets increment and try again
                            i++;
                            continue freeValueLoop;
                        }
                    }
                }
            }
            // This point was reached without hitting a continue statement.
            // We have an unused character.
            return c;
        }
    }

    /**
     * Get the next free character to use as a list for passing to a bitmap provider.
     * <p>
     * The list will always have one string with a length of one.
     *
     * @return The list.
     * @see #getNextChar()
     */
    @Unmodifiable
    public List<String> getNextCharAsList() {
        return Collections.singletonList(String.valueOf(this.getNextChar()));
    }

}
