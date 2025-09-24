package com.infinityfightclub;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.infinityfightclub.managers.Assets;
import com.infinityfightclub.screens.MenuScreen;

public class InfinityFightClubGame extends Game {
    public Assets assets;

    @Override
    public void create() {
        assets = new Assets();
        assets.load();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        if (assets != null) assets.dispose();
    }
}
