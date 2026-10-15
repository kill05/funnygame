package com.github.kill05.funnygame.server.game;

import java.util.function.Function;

public record GameType(String typeId, Function<GameController, Game> factory) {

}
