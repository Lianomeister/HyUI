package au.ellie.hyui.sounds;

import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.Sound;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static final Map<String, Sound> soundEffects = new HashMap<>();
    private static Sound backgroundMusic;

    // -----------------------
    // Initialisieren TinySound
    // -----------------------
    public static void init() {
        TinySound.init();
    }

    // -----------------------
    // Sound effect laden
    // -----------------------
    public static void loadSoundEffect(String key, String path) {
        Sound sound = TinySound.loadSound(path);
        if (sound != null) {
            soundEffects.put(key, sound);
        }
    }

    // Sound effect aus InputStream laden (ResourceLoader)
    public static void loadSoundEffect(String key, InputStream stream) {
        Sound sound = TinySound.loadSound(stream);
        if (sound != null) {
            soundEffects.put(key, sound);
        }
    }

    // -----------------------
    // Effekt abspielen
    // -----------------------
    public static void playSoundEffect(String key) {
        Sound sound = soundEffects.get(key);
        if (sound != null) sound.play();
    }

    // -----------------------
    // Hintergrundmusik laden
    // -----------------------
    public static void loadBackgroundMusic(String path) {
        if (backgroundMusic != null) backgroundMusic.stop();
        backgroundMusic = TinySound.loadSound(path);
    }

    // Musik aus InputStream laden
    public static void loadBackgroundMusic(InputStream stream) {
        if (backgroundMusic != null) backgroundMusic.stop();
        backgroundMusic = TinySound.loadSound(stream);
    }

    // -----------------------
    // Musik starten / stoppen
    // -----------------------
    public static void playBackgroundMusic(boolean loop) {
        if (backgroundMusic != null) backgroundMusic.play(loop);
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) backgroundMusic.stop();
    }

    // -----------------------
    // TinySound beenden
    // -----------------------
    public static void shutdown() {
        TinySound.shutdown();
    }
}
