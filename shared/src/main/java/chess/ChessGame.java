package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {

        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;


    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BlACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece occupant = board.getPiece(startPosition);
        if (occupant == null){
            return null;

        }

        return occupant.pieceMoves(board, startPosition);

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece occupant = board.getPiece(start);
        Collection<ChessMove> isLegal = validMoves(start);

        if (occupant.getTeamColor() != teamTurn || !isLegal.contains(move)){
            throw InvalidMoveException;
        }
        board.addPiece(start, null);
        board.addPiece(end, occupant);

        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(occupant.getTeamColor(), move.getPromotionPiece()));
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BlACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> kingPosition = new ArrayList<>();
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece occupant = board.getPiece(pos);
                if (occupant.getPieceType() == ChessPiece.PieceType.KING){
                    kingPosition.add(pos);
                }
            }
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
