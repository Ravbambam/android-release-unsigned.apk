package com.infinityfightclub.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.infinityfightclub.InfinityFightClubGame;
import com.infinityfightclub.grid.LevelGrid;
import com.infinityfightclub.utils.GameState;
import com.badlogic.gdx.utils.Array;
import com.infinityfightclub.entities.Player;
import com.infinityfightclub.entities.Enemy;
import com.infinityfightclub.entities.Potion;
import com.infinityfightclub.managers.EnemySpawner;
import com.infinityfightclub.managers.ItemSpawner;

public class GameScreen implements Screen {
    private final InfinityFightClubGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final ShapeRenderer shapes;
    private LevelGrid grid;
    private final GameState state;
    private float time;
    private final Player player;
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Potion> potions = new Array<>();
    private final Vector2 velocity = new Vector2();
    private boolean paused;
    private boolean joystickActive;
    private final Vector2 joystickCenter = new Vector2();
    private final Vector2 joystickVec = new Vector2();
    private float joystickRadius = 60f;
    private int joystickPointer = -1;
    private float dashTimer;
    private float dashCooldown;
    private final com.badlogic.gdx.scenes.scene2d.Stage hudStage;
    private final com.badlogic.gdx.scenes.scene2d.ui.Label hudLabel;
    private final com.badlogic.gdx.scenes.scene2d.ui.Touchpad touchpad;
    private final com.badlogic.gdx.scenes.scene2d.ui.ImageButton pauseButton, dashButton;
    private com.badlogic.gdx.graphics.Texture padBgTex, padKnobTex, btnTex, pauseIconTex, resumeIconTex, menuIconTex, exitIconTex, dashIconTex;
    private com.badlogic.gdx.scenes.scene2d.Group pauseGroup;
    private com.badlogic.gdx.graphics.Texture pausePanelTex;

    public GameScreen(InfinityFightClubGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 480, camera);
        this.batch = new SpriteBatch();
        this.shapes = new ShapeRenderer();
        this.grid = new com.infinityfightclub.managers.LevelManager().loadFromJson("level_rules.json");
        this.camera.position.set(grid.spawnRect(0,0).x, grid.spawnRect(0,0).y, 0);
        this.camera.zoom = 0.4f;
        this.camera.update();
        this.state = new GameState();
        com.badlogic.gdx.math.Rectangle sr = grid.spawnRect(16,16);
        this.player = new Player(sr.x, sr.y, sr.width, sr.height);
        if (grid.rectBlocked(player.bounds)) {
            for (int r=0;r<=3;r++) {
                boolean placed=false;
                for (int dx=-r; dx<=r && !placed; dx++) for (int dy=-r; dy<=r && !placed; dy++) {
                    int cx = grid.spawnX + dx, cy = grid.spawnY + dy;
                    if (cx<0||cy<0||cx>=grid.width||cy>=grid.height) continue;
                    com.badlogic.gdx.math.Rectangle rr = new com.badlogic.gdx.math.Rectangle(cx*grid.tileSize + (grid.tileSize-player.bounds.width)/2f, cy*grid.tileSize + (grid.tileSize-player.bounds.height)/2f, player.bounds.width, player.bounds.height);
                    if (!grid.rectBlocked(rr)) { player.bounds.set(rr); placed=true; }
                }
                if (placed) break;
            }
        }
        enemies.addAll(new EnemySpawner().spawn(grid, state.level));
        potions.addAll(new ItemSpawner().spawnPotions(grid));
        hudStage = new com.badlogic.gdx.scenes.scene2d.Stage(new com.badlogic.gdx.utils.viewport.FitViewport(800, 480));
        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle ls = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(game.assets.font, com.badlogic.gdx.graphics.Color.WHITE);
        hudLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("", ls);
        hudLabel.setPosition(10, 450);
        hudStage.addActor(hudLabel);
        com.badlogic.gdx.graphics.Pixmap pbg = new com.badlogic.gdx.graphics.Pixmap(140,140, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pbg.setColor(0.2f,0.2f,0.2f,0.4f);
        pbg.fillCircle(70,70,70);
        padBgTex = new com.badlogic.gdx.graphics.Texture(pbg);
        pbg.dispose();
        com.badlogic.gdx.graphics.Pixmap pknob = new com.badlogic.gdx.graphics.Pixmap(40,40, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pknob.setColor(0.6f,0.6f,0.6f,0.9f);
        pknob.fillCircle(20,20,20);
        padKnobTex = new com.badlogic.gdx.graphics.Texture(pknob);
        pknob.dispose();
        com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle ts = new com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle();
        ts.background = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(padBgTex));
        ts.knob = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(padKnobTex));
        touchpad = new com.badlogic.gdx.scenes.scene2d.ui.Touchpad(10f, ts);
        touchpad.setBounds(20, 20, 140, 140);
        hudStage.addActor(touchpad);
        com.badlogic.gdx.graphics.Pixmap pbtn = new com.badlogic.gdx.graphics.Pixmap(60,40, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pbtn.setColor(0.95f,0.55f,0.1f,0.8f);
        pbtn.fillRectangle(0,0,60,40);
        btnTex = new com.badlogic.gdx.graphics.Texture(pbtn);
        pbtn.dispose();
        com.badlogic.gdx.scenes.scene2d.utils.Drawable btnDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTex));
        pauseIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_pause.png"));
        resumeIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_resume.png"));
        menuIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_menu.png"));
        exitIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_exit.png"));
        com.badlogic.gdx.scenes.scene2d.utils.Drawable pauseDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(pauseIconTex));
        pauseButton = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(pauseDr);
        float ww = hudStage.getViewport().getWorldWidth();
        float wh = hudStage.getViewport().getWorldHeight();
        pauseButton.setBounds(ww-76, wh-76, 64, 64);
        pauseButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){ paused = true; if (pauseGroup!=null) pauseGroup.setVisible(true); }
        });
        hudStage.addActor(pauseButton);
        com.badlogic.gdx.scenes.scene2d.utils.Drawable dashDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(dashIconTex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal("ui/icon_dash.png"))));
        dashButton = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(dashDr);
        dashButton.setBounds(ww-76, 12, 64, 64);
        dashButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                if (dashCooldown <= 0f) { dashTimer = 0.35f; dashCooldown = 1.2f; }
            }
        });
        hudStage.addActor(dashButton);
        buildPauseOverlay();
        com.badlogic.gdx.InputMultiplexer mux = new com.badlogic.gdx.InputMultiplexer();
        mux.addProcessor(hudStage);
        mux.addProcessor(new com.badlogic.gdx.InputAdapter(){
            @Override public boolean keyDown(int keycode){
                if (keycode==com.badlogic.gdx.Input.Keys.BACK || keycode==com.badlogic.gdx.Input.Keys.ESCAPE){
                    paused = !paused;
                    if (pauseGroup!=null) pauseGroup.setVisible(paused);
                    return true;
                }
                return false;
            }
        });
        com.badlogic.gdx.Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, true);
        com.badlogic.gdx.Gdx.input.setInputProcessor(mux);
    }

    void buildPauseOverlay() {
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle tbs = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        tbs.font = game.assets.font;
        tbs.up = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(btnTex));
        tbs.down = tbs.up;
        com.badlogic.gdx.graphics.Pixmap pm = new com.badlogic.gdx.graphics.Pixmap(400,240, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pm.setColor(0f,0f,0f,0.6f);
        pm.fillRectangle(0,0,400,240);
        pausePanelTex = new com.badlogic.gdx.graphics.Texture(pm);
        pm.dispose();
        com.badlogic.gdx.scenes.scene2d.ui.Image panel = new com.badlogic.gdx.scenes.scene2d.ui.Image(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(pausePanelTex)));
        panel.setBounds(200,120,400,240);
        com.badlogic.gdx.scenes.scene2d.utils.Drawable resumeDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(resumeIconTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnResume = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(resumeDr);
        btnResume.setBounds(368, 300, 64, 64);
        btnResume.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ paused = false; pauseGroup.setVisible(false);} });
        com.badlogic.gdx.scenes.scene2d.utils.Drawable menuDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(menuIconTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnMenu = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(menuDr);
        btnMenu.setBounds(368, 250, 64, 64);
        btnMenu.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ game.setScreen(new MenuScreen(game)); } });
        com.badlogic.gdx.scenes.scene2d.utils.Drawable exitDr = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(exitIconTex));
        com.badlogic.gdx.scenes.scene2d.ui.ImageButton btnExit = new com.badlogic.gdx.scenes.scene2d.ui.ImageButton(exitDr);
        btnExit.setBounds(368, 200, 64, 64);
        btnExit.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){ public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ com.badlogic.gdx.Gdx.app.exit(); } });
        pauseGroup = new com.badlogic.gdx.scenes.scene2d.Group();
        pauseGroup.addActor(panel);
        pauseGroup.addActor(btnResume);
        pauseGroup.addActor(btnMenu);
        pauseGroup.addActor(btnExit);
        pauseGroup.setVisible(false);
        hudStage.addActor(pauseGroup);
    }

    @Override
    public void show() { game.assets.playMusic("bgm_main.ogg", true); }

    private void handleInput(float dt) {
        if (paused) return;
        velocity.set(0,0);
        float kx = touchpad.getKnobPercentX();
        float ky = touchpad.getKnobPercentY();
        if (kx*kx + ky*ky > 0.04f) {
            joystickActive = true;
            joystickVec.set(kx, ky);
        } else {
            joystickActive = false;
            joystickVec.setZero();
        }
        if (joystickActive) {
            velocity.set(joystickVec).nor();
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.y += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.y -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.x -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.x += 1;
        }
        float speed = 120f;
        if (dashTimer > 0) speed = 260f;
        if (velocity.len2() > 0) velocity.nor().scl(speed * dt);
        if (!grid.rectBlocked(new com.badlogic.gdx.math.Rectangle(player.bounds.x+velocity.x, player.bounds.y+velocity.y, player.bounds.width, player.bounds.height))) {
            player.bounds.x += velocity.x;
            player.bounds.y += velocity.y;
        }
        if (player.bounds.x < 0) player.bounds.x = 0;
        if (player.bounds.y < 0) player.bounds.y = 0;
        if (player.bounds.x + player.bounds.width > grid.getWorldWidth()) player.bounds.x = grid.getWorldWidth() - player.bounds.width;
        if (player.bounds.y + player.bounds.height > grid.getWorldHeight()) player.bounds.y = grid.getWorldHeight() - player.bounds.height;
        camera.position.set(player.bounds.x + player.bounds.width/2f, player.bounds.y + player.bounds.height/2f, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        time += delta;
        if (dashTimer > 0) dashTimer -= delta; else dashTimer = 0;
        if (dashCooldown > 0) dashCooldown -= delta; else dashCooldown = 0;
        if (!paused) {
            updateEnemies(delta);
            checkPickups();
            if (com.badlogic.gdx.math.Intersector.overlaps(player.bounds, grid.exitRect())) nextLevel();
            if (player.hp <= 0) game.setScreen(new GameOverScreen(game, state.score));
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        boolean debugOn = com.badlogic.gdx.Gdx.app.getPreferences("ifc").getBoolean("debug_on", false);
        if (debugOn) {
            shapes.begin(ShapeRenderer.ShapeType.Line);
            grid.renderDebug(shapes);
            shapes.end();
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float vx = camera.position.x - viewport.getWorldWidth()/2f;
        float vy = camera.position.y - viewport.getWorldHeight()/2f;
        int x0 = Math.max(0, (int)Math.floor(vx / grid.tileSize) - 1);
        int y0 = Math.max(0, (int)Math.floor(vy / grid.tileSize) - 1);
        int x1 = Math.min(grid.width-1, (int)Math.ceil((vx + viewport.getWorldWidth()) / grid.tileSize) + 1);
        int y1 = Math.min(grid.height-1, (int)Math.ceil((vy + viewport.getWorldHeight()) / grid.tileSize) + 1);
        for (int x=x0;x<=x1;x++) for (int y=y0;y<=y1;y++) {
            com.badlogic.gdx.graphics.Texture floorTex = game.assets.getFloorTexture();
            batch.draw(floorTex, x*grid.tileSize, y*grid.tileSize, grid.tileSize, grid.tileSize);
            if (grid.tiles[x][y] == com.infinityfightclub.grid.TileType.OBSTACLE) {
                com.badlogic.gdx.graphics.Texture tex = game.assets.getObstacleTexture(grid.obstacleId[x][y]);
                batch.draw(tex, x*grid.tileSize, y*grid.tileSize, grid.tileSize, grid.tileSize);
            }
        }
        com.badlogic.gdx.graphics.Texture exitTex = game.assets.getExitTexture();
        com.badlogic.gdx.math.Rectangle er = grid.exitRect();
        batch.draw(exitTex, er.x, er.y, er.width, er.height);
        for (int i=0;i<enemies.size;i++) {
            Enemy e = enemies.get(i);
            batch.draw(game.assets.getEnemyTexture(), e.bounds.x, e.bounds.y, e.bounds.width, e.bounds.height);
        }
        for (int i=0;i<potions.size;i++) {
            Potion p = potions.get(i);
            batch.draw(game.assets.getPotionTexture(), p.bounds.x, p.bounds.y, p.bounds.width, p.bounds.height);
        }
        batch.draw(game.assets.getPlayerTexture(), player.bounds.x, player.bounds.y, player.bounds.width, player.bounds.height);
        batch.end();
        String hpK = game.assets.bundle!=null?game.assets.bundle.get("hp"):"HP"; String lvlK = game.assets.bundle!=null?game.assets.bundle.get("level"):"Lvl"; String scoreK = game.assets.bundle!=null?game.assets.bundle.get("score"):"Score"; hudLabel.setText(hpK+":"+player.hp+"/"+player.maxHp+"  "+lvlK+":"+state.level+"  "+scoreK+":"+state.score);
        hudStage.act(delta);
        hudStage.draw();
    }

    void updateEnemies(float dt) {
        for (Enemy e: enemies) {
            float dx = player.bounds.x - e.bounds.x;
            float dy = player.bounds.y - e.bounds.y;
            float len2 = dx*dx+dy*dy;
            if (len2 > 1f) {
                float len = (float)Math.sqrt(len2);
                e.bounds.x += (dx/len) * e.speed * 40f * dt;
                e.bounds.y += (dy/len) * e.speed * 40f * dt;
            }
            e.attackTimer += dt;
            if (com.badlogic.gdx.math.Intersector.overlaps(player.bounds, e.bounds) && e.attackTimer >= e.attackCooldown) {
                player.hp -= e.damage;
                e.attackTimer = 0f;
            }
        }
    }

    void checkPickups() {
        for (int i=0;i<potions.size;i++) {
            Potion p = potions.get(i);
            if (com.badlogic.gdx.math.Intersector.overlaps(player.bounds, p.bounds)) {
                player.hp = Math.min(player.maxHp, player.hp + p.healAmount);
                game.assets.playSound("sfx_portal.wav");
                potions.removeIndex(i);
                i--;
            }
        }
    }

    void nextLevel() {
        state.level++;
        state.score += 100;
        game.assets.playSound("sfx_portal.wav");
        com.infinityfightclub.managers.LevelManager lm = new com.infinityfightclub.managers.LevelManager();
        LevelGrid newGrid = lm.loadFromJson("level_rules.json");
        newGrid.tiles[newGrid.spawnX][newGrid.spawnY] = com.infinityfightclub.grid.TileType.SPAWN;
        this.enemies.clear();
        this.potions.clear();
        this.enemies.addAll(new EnemySpawner().spawn(newGrid, state.level));
        this.potions.addAll(new ItemSpawner().spawnPotions(newGrid));
        this.player.bounds.set(newGrid.spawnRect(16,16));
        if (newGrid.rectBlocked(player.bounds)) {
            for (int r=0;r<=3;r++) {
                boolean placed=false;
                for (int dx=-r; dx<=r && !placed; dx++) for (int dy=-r; dy<=r && !placed; dy++) {
                    int cx = newGrid.spawnX + dx, cy = newGrid.spawnY + dy;
                    if (cx<0||cy<0||cx>=newGrid.width||cy>=newGrid.height) continue;
                    com.badlogic.gdx.math.Rectangle rr = new com.badlogic.gdx.math.Rectangle(cx*newGrid.tileSize + (newGrid.tileSize-player.bounds.width)/2f, cy*newGrid.tileSize + (newGrid.tileSize-player.bounds.height)/2f, player.bounds.width, player.bounds.height);
                    if (!newGrid.rectBlocked(rr)) { player.bounds.set(rr); placed=true; }
                }
                if (placed) break;
            }
        }
        this.camera.position.set(player.bounds.x + player.bounds.width/2f, player.bounds.y + player.bounds.height/2f, 0);
        this.camera.update();
        this.grid = newGrid;
        this.time = 0f;
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); hudStage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() { game.assets.stopMusic(); }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        hudStage.dispose();
        if (padBgTex != null) padBgTex.dispose();
        if (padKnobTex != null) padKnobTex.dispose();
        if (btnTex != null) btnTex.dispose();
        if (pauseIconTex != null) pauseIconTex.dispose();
        if (resumeIconTex != null) resumeIconTex.dispose();
        if (menuIconTex != null) menuIconTex.dispose();
        if (exitIconTex != null) exitIconTex.dispose();
        if (dashIconTex != null) dashIconTex.dispose();
    }
}
