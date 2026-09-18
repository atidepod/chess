package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    private kingMoves(ChessBoard board, ChessPosition position){
        int[][] directions = {
                
        }

    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position){
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int r = row+1;
        while(r<=8) {
            ChessPosition newPos = new ChessPosition(r, col);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            }
            else if (occupant.getTeamColor() != this.pieceColor){
                moves.add(new ChessMove(position, newPos, null));
                break;
            }
            else {
                break;
            }
            r++;
        }
        r = row-1;
        while(r>=1) {
            ChessPosition newPos = new ChessPosition(r, col);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            r--;
        }
        int c = col +1;
        while (c <=8) {
            ChessPosition newPos = new ChessPosition(row, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            c++;
        }
        c = col-1;
        while (c>=1) {
            ChessPosition newPos = new ChessPosition(row, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            c--;
        }
        r = row-1;
        c = col-1;
        while (r>=1 && c>=1){
            ChessPosition newPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            r--;
            c--;

        }
        r = row+1;
        c = col-1;
        while (r<=8 && c>=1){
            ChessPosition newPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            r++;
            c--;

        }
        r = row-1;
        c = col+1;
        while (r>=1 && c<=8){
            ChessPosition newPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            r--;
            c++;

        }
        r = row+1;
        c = col+1;
        while (r<=8 && c<=8){
            ChessPosition newPos = new ChessPosition(r, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
            r++;
            c++;

        }

        return moves;

    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position){
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        for (int r = row+1; r <=8; r++){
            ChessPosition newPos = new ChessPosition(r, col);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            }
            else if (occupant.getTeamColor() != this.pieceColor){
                moves.add(new ChessMove(position, newPos, null));
                break;
            }
            else {
                break;
            }
        }
        for (int r = row-1; r >=1; r--) {
            ChessPosition newPos = new ChessPosition(r, col);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
        }
        for (int c = col+1; c <=8; c++) {
            ChessPosition newPos = new ChessPosition(row, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
        }
        for (int c = col-1; c >=1; c--) {
            ChessPosition newPos = new ChessPosition(row, c);
            ChessPiece occupant = board.getPiece(newPos);

            if (occupant == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else if (occupant.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(position, newPos, null));
                break;
            } else {
                break;
            }
        }
        return moves;

    }
    private bishopMoves(ChessBoard board, ChessPosition position){

    }
    private knightMoves(ChessBoard board, ChessPosition position){

    }
    private pawnMoves(ChessBoard board, ChessPosition position){

    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() { return type; }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        return new ArrayList<>();
    }
}
