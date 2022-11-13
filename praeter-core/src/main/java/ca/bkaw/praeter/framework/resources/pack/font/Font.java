package ca.bkaw.praeter.framework.resources.pack.font;

import ca.bkaw.praeter.framework.resources.pack.JsonResource;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Unmodifiable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
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
     * Add the specified bitmap font provider to this font.
     *
     * @param provider The provider.
     * @throws IOException If an I/O error occurs.
     */
    public void addProvider(BitmapFontProvider provider) throws IOException {
        JsonObject json = this.fontJson.getJson();
        JsonArray providers = json.getAsJsonArray("providers");
        for (JsonElement element : providers) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (provider.isEqual(jsonObject)) {
                // Great, we already had this provider!
                System.out.println("debug: provider already exists");
                return;
            }
        }
        System.out.println("debug: adding provider");
        providers.add(provider.asJsonObject());
        this.fontJson.save();
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
                JsonArray chars = element.getAsJsonObject().getAsJsonArray("chars");
                for (JsonElement element2 : chars) {
                    if (element2.getAsString().indexOf(c) > 0) {
                        // This character is occupied, lets increment and try again
                        i++;
                        continue freeValueLoop;
                    }
                }
            }
            // This point was reached without hitting the continue statement.
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

    /**
     * Get the namespaced key for an almost-transparent, 1x1-pixel image.
     *
     * @return The key.
     * @throws IOException If an I/O error occurs while writing the texture.
     */
    private NamespacedKey getAlmostTransparent1x1Texture() throws IOException {
        Path path = this.pack.getPath("assets/praeter/textures/font/transparent_1.png");
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, 0x11000000);
            try (OutputStream output = Files.newOutputStream(path)) {
                ImageIO.write(image, "png", output);
            }
        }
        return new NamespacedKey("praeter", "font/transparent_1.png");
    }

    private char getChar(BitmapFontProvider provider) {
        return provider.getChars().get(0).charAt(0);
    }

    /**
     * Get or create a font character that is a negative space of the specified amount
     * of pixels.
     * <p>
     * Using this character will move the text cursor backwards.
     *
     * @param pixels The positive amount of pixels to go back.
     * @return The character to use.
     * @throws IOException If an I/O error occurs.
     */
    public char negativeSpace(int pixels) throws IOException {
        BitmapFontProvider provider = new BitmapFontProvider(
            this.getAlmostTransparent1x1Texture(),

            // subtract 2 to remove the pixels before and after the character
            -pixels - 2, // width/height (negative)

            // make it not visible, it's already transparent though
            -32768, // ascent

            this.getNextCharAsList()
        );
        this.addProvider(provider);
        return this.getChar(provider);
    }
}
