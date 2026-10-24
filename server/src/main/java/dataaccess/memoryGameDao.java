package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class memoryGameDao implements gamedao{
    private HashMap<Integer, GameData> games;
    public memoryGameDao(){
        games = new HashMap<>();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())){
            throw new DataAccessException("Game Already Made");
        }
        games.put(game.gameID(), game);

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)){
            throw new DataAccessException("No Game ID");
        }
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("No Such Game");
        }
        games.put(game.gameID(), game);



    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();

    }
}
