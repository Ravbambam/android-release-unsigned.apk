package com.infinityfightclub.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.infinityfightclub.InfinityFightClubGame;

public class MenuScreen implements Screen {
    private final InfinityFightClubGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final com.badlogic.gdx.scenes.scene2d.Stage stage;
    private com.badlogic.gdx.graphics.Texture btnTex, btnTexDown, bgMenuTex;
    private com.badlogic.gdx.scenes.scene2d.ui.TextButton btnPlay, btnSettings, btnExit, btnLang; private com.badlogic.gdx.scenes.scene2d.ui.Label title;

    public MenuScreen(InfinityFightClubGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 480, camera);
        this.batch = new SpriteBatch();
        this.stage = new com.badlogic.gdx.scenes.scene2d.Stage(new com.badlogic.gdx.utils.viewport.FitViewport(800, 480));
        com.badlogic.gdx.scenes.scene2d.ui.Image bg = null;
        if (com.badlogic.gdx.Gdx.files.internal("ui/bg_menu.png").exists()) {
            bgMenuTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/bg_menu.png"));
            bg = new com.badlogic.gdx.scenes.scene2d.ui.Image(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(bgMenuTex)));
            bg.setBounds(0,0,800,480);
            stage.addActor(bg);
        }
        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle ls = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(game.assets.font, com.badlogic.gdx.graphics.Color.WHITE);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle tbs = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        tbs.font = game.assets.font;
        com.badlogic.gdx.graphics.Pixmap p = new com.badlogic.gdx.graphics.Pixmap(220, 48, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        p.setColor(0.95f,0.55f,0.1f,0.8f);
        p.fillRectangle(0,0,220,48);
        btnTex = new com.badlogic.gdx.graphics.Texture(p);
        p.dispose();
        com.badlogic.gdx.graphics.Pixmap p2 = new com.badlogic.gdx.graphics.Pixmap(220, 48, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        p2.setColor(0.8f,0.4f,0.05f,0.9f);
        p2.fillRectangle(0,0,220,48);
        btnTexDown = new com.badlogic.gdx.graphics.Texture(p2);
        p2.dispose();
        tbs.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTex));
        tbs.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTexDown));
        title = new com.badlogic.gdx.scenes.scene2d.ui.Label(game.assets.bundle!=null?game.assets.bundle.get("title"):"InfinityFightClub", ls);
        title.setPosition(280, 420);
        stage.addActor(title);
        btnPlay = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(game.assets.bundle!=null?game.assets.bundle.get("play"):"Play", tbs);
        btnSettings = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(game.assets.bundle!=null?game.assets.bundle.get("settings"):"Settings", tbs);
        String curLang = com.badlogic.gdx.Gdx.app.getPreferences("ifc").getString("lang","EN"); String langLabel = game.assets.bundle!=null?game.assets.bundle.get("language"):"Language"; String langDisp = "EN".equals(curLang)?"English":"RU".equals(curLang)?"Русский":"ES".equals(curLang)?"Español":"DE".equals(curLang)?"Deutsch":curLang; btnLang = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(langLabel+": "+langDisp, tbs);
        btnExit = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(game.assets.bundle!=null?game.assets.bundle.get("exit"):"Exit", tbs);
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnAudioToggle = null;
        if (com.badlogic.gdx.Gdx.files.internal("ui/icon_audio_on.png").exists() && com.badlogic.gdx.Gdx.files.internal("ui/icon_audio_off.png").exists()) {
            boolean aOn = com.badlogic.gdx.Gdx.app.getPreferences("ifc").getBoolean("audio_on", true);
            com.badlogic.gdx.graphics.Texture aTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal(aOn?"ui/icon_audio_on.png":"ui/icon_audio_off.png"));
            btnAudioToggle = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(aTex)));
            btnAudioToggle.setBounds(742, 422, 48, 48);
            com.badlogic.gdx.scenes.scene2d.ui.ImageButton finalBtnAudioToggle = btnAudioToggle;
            btnAudioToggle.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ com.badlogic.gdx.Preferences p = com.badlogic.gdx.Gdx.app.getPreferences("ifc"); boolean on = p.getBoolean("audio_on", true); p.putBoolean("audio_on", !on); p.flush(); com.badlogic.gdx.graphics.Texture nt = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal(!on?"ui/icon_audio_on.png":"ui/icon_audio_off.png")); com.badlogic.gdx.scenes.scene2d.utils.Drawable nd = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(nt)); finalBtnAudioToggle.getStyle().imageUp = nd; finalBtnAudioToggle.getStyle().imageDown = nd; }});
            stage.addActor(btnAudioToggle);
        }
        float cx = 290;
        btnPlay.setBounds(cx, 300, 220, 48);
        btnSettings.setBounds(cx, 240, 220, 48);
        btnLang.setBounds(cx, 180, 220, 48);
        btnExit.setBounds(cx, 120, 220, 48);
        btnPlay.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){ game.setScreen(new GameScreen(game)); }
        });
        btnExit.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){ Gdx.app.exit(); }
        });
        btnLang.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                String[] langs = new String[]{"EN","RU","ES","DE"};
                String cur = com.badlogic.gdx.Gdx.app.getPreferences("ifc").getString("lang","EN");
                int idx = 0; for (int i=0;i<langs.length;i++) if (langs[i].equals(cur)) { idx = i; break; }
                idx = (idx + 1) % langs.length;
                com.badlogic.gdx.Preferences p = com.badlogic.gdx.Gdx.app.getPreferences("ifc");
                p.putString("lang", langs[idx]);
                p.flush();
                game.assets.reloadBundle();
                String ll = game.assets.bundle!=null?game.assets.bundle.get("language"):"Language";
                String ds = "EN".equals(langs[idx])?"English":"RU".equals(langs[idx])?"Русский":"ES".equals(langs[idx])?"Español":"DE".equals(langs[idx])?"Deutsch":langs[idx];
                btnLang.setText(ll+": "+ds);
                btnPlay.setText(game.assets.bundle!=null?game.assets.bundle.get("play"):"Play");
                btnSettings.setText(game.assets.bundle!=null?game.assets.bundle.get("settings"):"Settings");
                btnExit.setText(game.assets.bundle!=null?game.assets.bundle.get("exit"):"Exit");
                title.setText(game.assets.bundle!=null?game.assets.bundle.get("title"):"InfinityFightClub");
            }
        });
        btnSettings.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){ game.setScreen(new SettingsScreen(game)); }
        });
        stage.addActor(btnPlay);
        stage.addActor(btnSettings);
        stage.addActor(btnLang);
        stage.addActor(btnExit);
        if (bg != null) { bg.toBack(); }
        com.badlogic.gdx.InputMultiplexer mux = new com.badlogic.gdx.InputMultiplexer();
        mux.addProcessor(stage);
        com.badlogic.gdx.Gdx.input.setInputProcessor(mux);
    }



    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACK) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) { Gdx.app.exit(); }
        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); stage.getViewport().update(width, height, true); }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() { batch.dispose(); stage.dispose(); if (btnTex!=null) btnTex.dispose(); if (btnTexDown!=null) btnTexDown.dispose(); if (bgMenuTex!=null) bgMenuTex.dispose(); }
}
