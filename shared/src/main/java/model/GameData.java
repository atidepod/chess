package model;

import chess.ChessGame;

public class GameData {
    private final int gameID;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;

    GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;


    }
    private int getGameID(){
        return gameID;
    }
    private String getWhiteUsername(){
        return whiteUsername;
    }
    private String getBlackUsername(){
        return blackUsername;
    }
    private String getGameName(){
        return gameName
    }
    private ChessGame getGame(){
        return game;
    }
    public String toString(){
        return "Game Data Class[" + 
    }

}
