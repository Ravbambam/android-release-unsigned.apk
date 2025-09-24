package com.infinityfightclub.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Preferences;
import com.infinityfightclub.InfinityFightClubGame;

public class GameOverScreen implements Screen {
    private final InfinityFightClubGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final int score;

    public GameOverScreen(InfinityFightClubGame game, int score) {
        this.game = game;
        this.score = score;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 480, camera);
        this.batch = new SpriteBatch();
        Preferences p = Gdx.app.getPreferences("ifc");
        boolean privacy = p.getBoolean("privacy_mode", false);
        if (!privacy) {
            int best = p.getInteger("best", 0);
            if (score > best) { p.putInteger("best", score); p.flush(); }
        }
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        boolean privacy = Gdx.app.getPreferences("ifc").getBoolean("privacy_mode", false);
        int best = privacy ? 0 : Gdx.app.getPreferences("ifc").getInteger("best", 0);
        com.badlogic.gdx.utils.I18NBundle b = game.assets.bundle;
        String go = b!=null?b.get("game_over"):"Game Over";
        String sc = b!=null?b.get("score"):"Score";
        String be = b!=null?b.get("best"):"Best";
        String tap = b!=null?b.get("tap_to_return"):"Tap to return";
        game.assets.font.draw(batch, go+"\n"+sc+": "+score+"\n"+be+": "+best+"\n"+tap, 300, 260);
        batch.end();
    }
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); }
}
