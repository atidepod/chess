package service;

import model.GameData;

import java.util.Collection;

public record listGameResult(
        Collection<GameData> games
) {
}
