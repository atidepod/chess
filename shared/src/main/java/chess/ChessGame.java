package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    private void makeMoveSim(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) throw new InvalidMoveException("No piece");

        Collection<ChessMove> rawMoves = piece.pieceMoves(board, start);
        if (!rawMoves.contains(move)) throw new InvalidMoveException("Illegal move");

        board.addPiece(start, null);
        board.addPiece(end, piece);

        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        TeamColor originalColor = teamTurn;
        List<ChessMove> okMoves = new ArrayList<>();
        ChessPiece occupant = board.getPiece(startPosition);
        if (occupant == null){
            return null;

        }
        Collection<ChessMove> allMoves = occupant.pieceMoves(board, startPosition);

        ChessBoard snapshot = copyBoard(board);
        for (ChessMove x : allMoves){
            try {
                makeMoveSim(x);
                if (!isInCheck(occupant.getTeamColor())){
                    okMoves.add(x);
                }
            } catch (InvalidMoveException ignored) {
                continue;
            }
            board = snapshot;
            teamTurn = originalColor;
            snapshot = copyBoard(board);

        }
        return okMoves;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece occupant = board.getPiece(start);
        if (occupant == null) {
            throw new InvalidMoveException("Illegal move");
        }
        if (occupant.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Illegal move");
        }

        Collection<ChessMove> rawMoves = occupant.pieceMoves(board, start);
        if (!rawMoves.contains(move)) {
            throw new InvalidMoveException("Illegal move");
        }
        ChessBoard snapshot = copyBoard(board);


        board.addPiece(start, null);
        board.addPiece(end, occupant);

        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(occupant.getTeamColor(), move.getPromotionPiece()));
        }
        if (isInCheck(teamTurn)) {
            setBoard(snapshot);
            throw new InvalidMoveException("Move leaves king in check");
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row=1;row<=8 && kingPosition == null;row++){
            for (int col=1;col<=8;col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece occupant = board.getPiece(pos);
                if (occupant != null && occupant.getPieceType() == ChessPiece.PieceType.KING && occupant.getTeamColor() == teamColor){
                    kingPosition = pos;
                    break;
                }
            }
        }
        if (kingPosition == null){
            return false;
        }
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPosition circle = new ChessPosition(row,col);
                ChessPiece enemy = board.getPiece(circle);
                if (enemy == null) continue;
                if (enemy.getTeamColor() == teamColor) continue;
                Collection<ChessMove> moves = enemy.pieceMoves(board, circle);
                if (moves == null) continue;

                for (ChessMove m : moves){
                    if(m.getEndPosition().equals(kingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     *
     */
    public ChessBoard copyBoard(ChessBoard source){
        ChessBoard copy = new ChessBoard();
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece occupant = source.getPiece(pos);
                if (occupant != null) {
                    copy.addPiece(pos, new ChessPiece(occupant.getTeamColor(), occupant.getPieceType()));
                }

            }
        }
        return copy;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor originalTurn = teamTurn;

        if (!isInCheck(teamColor)){
            return false;
        }

        ChessBoard snapshot = copyBoard(board);
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPosition circle = new ChessPosition(row,col);
                ChessPiece friend = board.getPiece(circle);
                if (friend == null) continue;
                if (friend.getTeamColor() != teamColor) continue;
                Collection<ChessMove> moves = validMoves(circle);
                if (moves == null) continue;
                for (ChessMove m : moves){
                    try {
                        makeMoveSim(m);
                    } catch (InvalidMoveException e) {
                        continue;
                    }
                    if (!isInCheck(teamColor)){
                        teamTurn = originalTurn;
                        setBoard(snapshot);
                        return false;
                    }
                    setBoard(copyBoard(snapshot));
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        TeamColor originalTurn = teamTurn;

        if (isInCheck(teamColor)){
            return false;
        }

        ChessBoard snapshot = copyBoard(board);
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPosition circle = new ChessPosition(row,col);
                ChessPiece friend = board.getPiece(circle);
                if (friend == null) continue;
                if (friend.getTeamColor() != teamColor) continue;
                Collection<ChessMove> moves = validMoves(circle);
                if (moves == null) continue;
                for (ChessMove m : moves){
                    try {
                        makeMoveSim(m);
                    } catch (InvalidMoveException e) {
                        continue;
                    }
                    if (!isInCheck(teamColor)){
                        setBoard(snapshot);
                        teamTurn = originalTurn;
                        return false;

                    }
                    setBoard(copyBoard(snapshot));
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
