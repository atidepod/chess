package passoff.chess.piecemoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessGameTest1 {

    public static void main(String[] args) throws Exception {

        System.out.println("=== TEST 1: Fool's Mate (Checkmate) ===");
        testCheckmate();

        System.out.println("\n=== TEST 2: Classic Stalemate ===");
        testStalemate();

        System.out.println("\n=== TEST 3: Normal Position (No Check, No Mate) ===");
        testNormal();
    }

    private static void testCheckmate() throws Exception {
        ChessGame game = new ChessGame();

        // Clear board
        ChessBoard empty = new ChessBoard();
        game.setBoard(empty);

        // Fool's mate position
        // Black king trapped on e8, white queen delivering mate on h5
        empty.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        empty.addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        empty.addPiece(new ChessPosition(5, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

        game.setTeamTurn(ChessGame.TeamColor.BLACK);

        System.out.println("In check? " + game.isInCheck(ChessGame.TeamColor.BLACK));
        System.out.println("In checkmate? " + game.isInCheckmate(ChessGame.TeamColor.BLACK));
    }

    private static void testStalemate() throws Exception {
        ChessGame game = new ChessGame();

        // Clear board
        ChessBoard empty = new ChessBoard();
        game.setBoard(empty);

        // Classic stalemate:
        // Black king on h8, white queen on g7, white king on g6
        empty.addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        empty.addPiece(new ChessPosition(7, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        empty.addPiece(new ChessPosition(6, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        game.setTeamTurn(ChessGame.TeamColor.BLACK);

        System.out.println("In check? " + game.isInCheck(ChessGame.TeamColor.BLACK));
        System.out.println("In stalemate? " + game.isInStalemate(ChessGame.TeamColor.BLACK));
    }

    private static void testNormal() throws Exception {
        ChessGame game = new ChessGame();

        // Standard starting position
        game.setBoard(new ChessBoard());
        game.getBoard().resetBoard();

        game.setTeamTurn(ChessGame.TeamColor.WHITE);

        System.out.println("In check? " + game.isInCheck(ChessGame.TeamColor.WHITE));
        System.out.println("In checkmate? " + game.isInCheckmate(ChessGame.TeamColor.WHITE));
        System.out.println("In stalemate? " + game.isInStalemate(ChessGame.TeamColor.WHITE));
    }
}

