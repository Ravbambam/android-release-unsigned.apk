package com.infinityfightclub.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.infinityfightclub.InfinityFightClubGame;

public class SettingsScreen implements Screen {
    private final InfinityFightClubGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final com.badlogic.gdx.scenes.scene2d.Stage stage;
    private com.badlogic.gdx.graphics.Texture btnTex, btnTexDown, backIconTex, audioOnTex, audioOffTex, iconExitTex;

    private com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnBack, btnAudioIcon;

    public SettingsScreen(InfinityFightClubGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 480, camera);
        this.batch = new SpriteBatch();
        this.stage = new com.badlogic.gdx.scenes.scene2d.Stage(new com.badlogic.gdx.utils.viewport.FitViewport(800, 480));
        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle ls = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(game.assets.font, com.badlogic.gdx.graphics.Color.WHITE);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle tbs = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        tbs.font = game.assets.font;
        com.badlogic.gdx.graphics.Pixmap p = new com.badlogic.gdx.graphics.Pixmap(220, 48, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        p.setColor(0.95f,0.55f,0.1f,0.95f);
        p.fillRectangle(0,0,220,48);
        btnTex = new com.badlogic.gdx.graphics.Texture(p);
        p.dispose();
        com.badlogic.gdx.graphics.Pixmap p2 = new com.badlogic.gdx.graphics.Pixmap(220, 48, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        p2.setColor(0.8f,0.4f,0.05f,1f);
        p2.fillRectangle(0,0,220,48);
        btnTexDown = new com.badlogic.gdx.graphics.Texture(p2);
        p2.dispose();
        tbs.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTex));
        tbs.down = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTexDown));
        String backPath = com.badlogic.gdx.Gdx.files.internal("ui/icon_back.png").exists()?"ui/icon_back.png":"ui/back_icon.png";
        backIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal(backPath));
        btnBack = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(backIconTex)));
        btnBack.setBounds(10, 422, 48, 48);
        btnBack.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){ game.setScreen(new MenuScreen(game)); } });
        audioOnTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_audio_on.png"));
        audioOffTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_audio_off.png"));
        iconExitTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_exit.png"));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnExitIcon = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(iconExitTex)));
        btnExitIcon.setBounds(694, 422, 48, 48);
        btnExitIcon.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ com.badlogic.gdx.Gdx.app.exit(); } });
        stage.addActor(btnExitIcon);
        boolean aOn = Gdx.app.getPreferences("ifc").getBoolean("audio_on", true);
        com.badlogic.gdx.scenes.scene2d.utils.Drawable audioDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(aOn?audioOnTex:audioOffTex));
        btnAudioIcon = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(audioDr);
        btnAudioIcon.setBounds(742, 422, 48, 48);
        btnAudioIcon.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ com.badlogic.gdx.Preferences p = Gdx.app.getPreferences("ifc"); boolean on = p.getBoolean("audio_on", true); p.putBoolean("audio_on", !on); p.flush(); com.badlogic.gdx.scenes.scene2d.utils.Drawable nd = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(!on?audioOnTex:audioOffTex)); btnAudioIcon.getStyle().imageUp = nd; btnAudioIcon.getStyle().imageDown = nd; }});
        com.badlogic.gdx.scenes.scene2d.ui.Table table = new com.badlogic.gdx.scenes.scene2d.ui.Table();
        table.setFillParent(true);
        String onStr = game.assets.bundle!=null?game.assets.bundle.get("on"):"On"; String offStr = game.assets.bundle!=null?game.assets.bundle.get("off"):"Off"; com.badlogic.gdx.scenes.scene2d.ui.TextButton btnAudio = new com.badlogic.gdx.scenes.scene2d.ui.TextButton((game.assets.bundle!=null?game.assets.bundle.get("audio"):"Audio")+": "+(Gdx.app.getPreferences("ifc").getBoolean("audio_on", true)?onStr:offStr), tbs);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnPrivacy = new com.badlogic.gdx.scenes.scene2d.ui.TextButton((game.assets.bundle!=null?game.assets.bundle.get("privacy"):"Privacy")+": "+(Gdx.app.getPreferences("ifc").getBoolean("privacy_mode", false)?onStr:offStr), tbs);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnResetSeed = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(game.assets.bundle!=null?game.assets.bundle.get("reset_seed"):"Reset Seed", tbs);
        table.center();
        table.add(new com.badlogic.gdx.scenes.scene2d.ui.Label(game.assets.bundle!=null?game.assets.bundle.get("settings"):"Settings", ls)).padBottom(20).row();
        table.add(btnAudio).width(220).height(48).pad(6).row();
        table.add(btnPrivacy).width(220).height(48).pad(6).row();
        table.add(btnResetSeed).width(220).height(48).pad(6).row();
        btnAudio.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                com.badlogic.gdx.Preferences p = Gdx.app.getPreferences("ifc");
                boolean on = p.getBoolean("audio_on", true);
                p.putBoolean("audio_on", !on);
                p.flush();
                btnAudio.setText((game.assets.bundle!=null?game.assets.bundle.get("audio"):"Audio")+": "+(!on?onStr:offStr));
            }
        });
        btnPrivacy.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                com.badlogic.gdx.Preferences p = Gdx.app.getPreferences("ifc");
                boolean pm = p.getBoolean("privacy_mode", false);
                p.putBoolean("privacy_mode", !pm);
                p.flush();
                btnPrivacy.setText((game.assets.bundle!=null?game.assets.bundle.get("privacy"):"Privacy")+": "+(!pm?onStr:offStr));
            }
        });
        btnResetSeed.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                com.badlogic.gdx.Preferences p = Gdx.app.getPreferences("ifc");
                p.putLong("seed", 0L);
                p.flush();
            }
        });
        stage.addActor(table);
        stage.addActor(btnBack);
        stage.addActor(btnAudioIcon);
        com.badlogic.gdx.InputMultiplexer mux = new com.badlogic.gdx.InputMultiplexer();
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(mux);
    }

    @Override public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACK) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) { game.setScreen(new MenuScreen(game)); }
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        if (btnTex!=null) btnTex.dispose();
        if (btnTexDown!=null) btnTexDown.dispose();
        if (backIconTex!=null) backIconTex.dispose();
        if (audioOnTex!=null) audioOnTex.dispose();
        if (audioOffTex!=null) audioOffTex.dispose();
        if (iconExitTex!=null) iconExitTex.dispose();
    }
}
