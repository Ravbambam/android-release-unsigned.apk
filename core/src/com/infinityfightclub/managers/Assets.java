package com.infinityfightclub.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Assets {
    public final AssetManager manager = new AssetManager();
    public BitmapFont font;
    public I18NBundle bundle;
    private final Map<String, Texture> obstacleTextures = new HashMap<>();
    private Texture playerTex;
    private Texture enemyTex;
    private Texture potionTex;
    private Texture exitTex;
    private Texture floorTex;
    private final Map<String, Sound> sounds = new HashMap<>();
    private Music music;

    public void load() {
        font = new BitmapFont();
        reloadBundle();
    }

    public Texture getObstacleTexture(String id) {
        if (id == null) id = "rock";
        Texture t = obstacleTextures.get(id);
        if (t != null) return t;
        FileHandle fh = Gdx.files.internal(id+".png");
        if (fh.exists()) {
            t = new Texture(fh);
        } else {
            Pixmap pm = new Pixmap(32,32, Pixmap.Format.RGBA8888);
            if ("bush".equals(id)) pm.setColor(0f,0.6f,0.1f,1f); else pm.setColor(0.5f,0.5f,0.5f,1f);
            pm.fillRectangle(2,2,28,28);
            t = new Texture(pm);
            pm.dispose();
        }
        obstacleTextures.put(id, t);
        return t;
    }

    public Texture getPlayerTexture() {
        if (playerTex != null) return playerTex;
        Texture t = getTextureOrNull("player.png");
        if (t != null) { playerTex = t; return playerTex; }
        Pixmap pm = new Pixmap(16,16, Pixmap.Format.RGBA8888);
        pm.setColor(0f,1f,0f,1f);
        pm.fillRectangle(0,0,16,16);
        playerTex = new Texture(pm);
        pm.dispose();
        return playerTex;
    }

    public Texture getEnemyTexture() {
        if (enemyTex != null) return enemyTex;
        Texture t = getTextureOrNull("enemy.png");
        if (t != null) { enemyTex = t; return enemyTex; }
        Pixmap pm = new Pixmap(16,16, Pixmap.Format.RGBA8888);
        pm.setColor(1f,0f,0f,1f);
        pm.fillRectangle(0,0,16,16);
        enemyTex = new Texture(pm);
        pm.dispose();
        return enemyTex;
    }

    public Texture getPotionTexture() {
        if (potionTex != null) return potionTex;
        Texture t = getTextureOrNull("potion.png");
        if (t != null) { potionTex = t; return potionTex; }
        Pixmap pm = new Pixmap(12,12, Pixmap.Format.RGBA8888);
        pm.setColor(0f,0.5f,1f,1f);
        pm.fillRectangle(0,0,12,12);
        potionTex = new Texture(pm);
        pm.dispose();
        return potionTex;
    }

    public Texture getExitTexture() {
        if (exitTex != null) return exitTex;
        Texture t = getTextureOrNull("portal.png");
        if (t == null) t = getTextureOrNull("exit.png");
        if (t != null) { exitTex = t; return exitTex; }
        Pixmap pm = new Pixmap(16,16, Pixmap.Format.RGBA8888);
        pm.setColor(0f,0f,1f,1f);
        pm.fillRectangle(0,0,16,16);
        exitTex = new Texture(pm);
        pm.dispose();
        return exitTex;
    }

    public Texture getFloorTexture() {
        if (floorTex != null) return floorTex;
        Texture t = getTextureOrNull("floor.png");
        if (t != null) { floorTex = t; return floorTex; }
        Pixmap pm = new Pixmap(32,32, Pixmap.Format.RGBA8888);
        pm.setColor(0.2f,0.2f,0.2f,1f);
        pm.fillRectangle(0,0,32,32);
        floorTex = new Texture(pm);
        pm.dispose();
        return floorTex;
    }

    public Texture getTextureOrNull(String path) {
        FileHandle fh = Gdx.files.internal(path);
        if (fh.exists()) return new Texture(fh);
        return null;
    }

    public void playSound(String file) {
        if (!Gdx.app.getPreferences("ifc").getBoolean("audio_on", true)) return;
        FileHandle fh = Gdx.files.internal(file);
        if (!fh.exists()) return;
        Sound s = sounds.get(file);
        if (s == null) {
            s = Gdx.audio.newSound(fh);
            sounds.put(file, s);
        }
        s.play();
    }

    public void playMusic(String file, boolean loop) {
        if (!Gdx.app.getPreferences("ifc").getBoolean("audio_on", true)) return;
        FileHandle fh = Gdx.files.internal(file);
        if (!fh.exists()) return;
        if (music != null) { music.stop(); music.dispose(); music = null; }
        music = Gdx.audio.newMusic(fh);
        music.setLooping(loop);
        music.play();
    }

    public void stopMusic() {
        if (music != null) { music.stop(); music.dispose(); music = null; }
    }

    public void reloadBundle() {
        String lang = Gdx.app.getPreferences("ifc").getString("lang", "EN");
        java.util.Locale locale;
        if ("RU".equals(lang)) locale = new java.util.Locale("ru");
        else if ("ES".equals(lang)) locale = new java.util.Locale("es");
        else if ("DE".equals(lang)) locale = new java.util.Locale("de");
        else locale = java.util.Locale.ENGLISH;
        FileHandle base = Gdx.files.internal("i18n/messages");
        bundle = I18NBundle.createBundle(base, locale);
    }

    public void dispose() {
        manager.dispose();
        if (font != null) font.dispose();
        if (playerTex != null) playerTex.dispose();
        if (enemyTex != null) enemyTex.dispose();
        if (potionTex != null) potionTex.dispose();
        if (exitTex != null) exitTex.dispose();
        if (floorTex != null) floorTex.dispose();
        for (Texture t: obstacleTextures.values()) t.dispose();
        obstacleTextures.clear();
        for (Sound s: sounds.values()) s.dispose();
        sounds.clear();
        if (music != null) { music.stop(); music.dispose(); }
    }
}
