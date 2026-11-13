
package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;

public class ChessPainter {

    public static void printBoard(ChessGame game, boolean whitePerspective) {

        chess.ChessBoard board = game.getBoard();

        String[] columns = {
                "a", "b", "c", "d", "e", "f", "g", "h"
        };

        System.out.print("   ");

        if (whitePerspective) {
            for (String column : columns) {
                System.out.print(" " + column + " ");
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                System.out.print(" " + columns[i] + " ");
            }
        }

        System.out.println();

        for (int row = 0; row < 8; row++) {
            int rank = whitePerspective ? 8 - row : row + 1;
            System.out.print(" " + rank + " ");
            for (int col = 0; col < 8; col++) {
                int file = whitePerspective ? col + 1 : 8 - col;
                ChessPosition position = new ChessPosition(rank, file);

                ChessPiece piece = board.getPiece(position);

                boolean lightSquare =(rank + file) % 2 == 0;

                String background = lightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String pieceText = getPieceSymbol(piece);

                System.out.print(background +
                        EscapeSequences.SET_TEXT_COLOR_WHITE +
                        pieceText +
                        EscapeSequences.RESET_BG_COLOR +
                        EscapeSequences.RESET_TEXT_COLOR);

            }

            System.out.println(" " + rank);
        }

        System.out.print("   ");

        if (whitePerspective) {
            for (String column : columns) {
                System.out.print(" " + column + " ");
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                System.out.print(" " + columns[i] + " ");
            }
        }

        System.out.println();
    }

    private static String getPieceSymbol(ChessPiece piece) {

        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.WHITE_KING;
                case QUEEN:
                    return EscapeSequences.WHITE_QUEEN;
                case BISHOP:
                    return EscapeSequences.WHITE_BISHOP;
                case KNIGHT:
                    return EscapeSequences.WHITE_KNIGHT;
                case ROOK:
                    return EscapeSequences.WHITE_ROOK;
                case PAWN:
                    return EscapeSequences.WHITE_PAWN;
            }

        } else {

            switch (piece.getPieceType()) {
                case KING:
                    return EscapeSequences.BLACK_KING;
                case QUEEN:
                    return EscapeSequences.BLACK_QUEEN;
                case BISHOP:
                    return EscapeSequences.BLACK_BISHOP;
                case KNIGHT:
                    return EscapeSequences.BLACK_KNIGHT;
                case ROOK:
                    return EscapeSequences.BLACK_ROOK;
                case PAWN:
                    return EscapeSequences.BLACK_PAWN;
            }
        }

        return EscapeSequences.EMPTY;
    }
}
