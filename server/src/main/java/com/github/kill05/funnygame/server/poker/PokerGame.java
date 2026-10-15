package com.github.kill05.funnygame.server.poker;

import com.github.kill05.funnygame.server.game.Game;
import com.github.kill05.funnygame.server.game.GameController;

public class PokerGame extends Game {

    public PokerGame(GameController controller) {
        super(controller);
    }

    @Override
    public String getDisplayName() {
        return "Poker";
    }
}
