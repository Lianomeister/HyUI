package au.ellie.hyui.utils;

import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.server.core.Message;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

/**
 * Helper class for building BsonDocument for UI styles.
 */
public final class BsonDocumentHelper {
    private final BsonDocument document = new BsonDocument();

    public BsonDocumentHelper set(String key, Message message) {
        document.put(key, Message.CODEC.encode(message, EmptyExtraInfo.EMPTY));
        return this;
    }
    
    public BsonDocumentHelper set(String key, String value) {
        document.put(key, new BsonString(value));
        return this;
    }

    public BsonDocumentHelper set(String key, int value) {
        document.put(key, new BsonInt32(value));
        return this;
    }

    public BsonDocumentHelper set(String key, double value) {
        document.put(key, new BsonDouble(value));
        return this;
    }

    public BsonDocumentHelper set(String key, boolean value) {
        document.put(key, new BsonBoolean(value));
        return this;
    }

    public BsonDocumentHelper set(String key, BsonValue value) {
        document.put(key, value);
        return this;
    }

    public BsonDocument getDocument() {
        return document;
    }
}
