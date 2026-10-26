package dataaccess;

import model.GameData;

import java.util.Collection;

public interface gamedao {

    void createGame(GameData game) throws DataAccessException;

    int getNextGameID();

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;
}
