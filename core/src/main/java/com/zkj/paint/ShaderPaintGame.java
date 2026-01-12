package com.zkj.paint;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.zkj.paint.screen.ShadeRenderScreen;

public class ShaderPaintGame extends Game {
    Screen screen;
    public ShaderPaintGame() {
        screen = new ShadeRenderScreen();
    }

    @Override
    public void create() {
        this.setScreen(screen);
    }


}
