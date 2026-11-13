package client;

import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var repl = new Repl("http://localhost:8080");
        repl.run();

    }
}
