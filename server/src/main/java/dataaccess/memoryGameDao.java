package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class memoryGameDao implements gamedao{
    private HashMap<Integer, GameData> games;
    private int nextGameID;
    public memoryGameDao(){
        games = new HashMap<>();
        nextGameID = 1;
    }
    @Override
    public int getNextGameID(){
        return nextGameID++;
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
        if (games.values() == null){
            throw new DataAccessException("No Such Game");
        }
        return games.values();
    }

    @Override
    public void clear() throws DataAccessException {
        if (games == null){
            throw new DataAccessException("no games to clear");
        }
        games.clear();

    }
}
