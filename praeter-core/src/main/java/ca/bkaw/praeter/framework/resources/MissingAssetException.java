package ca.bkaw.praeter.framework.resources;

/**
 * An exception thrown because an asset was requested at runtime that had not
 * been baked into the resource pack during startup.
 */
public class MissingAssetException extends RuntimeException {
    public MissingAssetException(String message) {
        super(message + "- Did you forget to include this asset? Or has the enabled worlds changed?");
    }
}
