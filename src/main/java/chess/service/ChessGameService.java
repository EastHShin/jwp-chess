package chess.service;

import chess.dao.BoardDao;
import chess.dao.PieceDao;
import chess.domain.ChessGame;
import chess.domain.Color;
import chess.domain.Room;
import chess.domain.Winner;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Piece;
import chess.dto.ChessBoardDto;
import chess.dto.RoomDto;
import chess.dto.RoomsDto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ChessGameService {

    private final PieceDao pieceDao;
    private final BoardDao boardDao;

    public ChessGameService(PieceDao pieceDao, BoardDao boardDao) {
        this.pieceDao = pieceDao;
        this.boardDao = boardDao;
    }

    public void create(String title, String password) {
        final Room room = new Room(title, password);
        if (boardDao.existsBoardByName(title)) {
            throw new IllegalArgumentException("이미 존재하는 방입니다.");
        }
        final ChessGame chessGame = ChessGame.create(room);
        final Long boardId = boardDao.save(chessGame);
        pieceDao.save(chessGame.getBoard().getPiecesByPosition(), boardId);
    }

    public void move(Long boardId, String rawSource, String rawTarget) {
        final Position source = Position.from(rawSource);
        final Position target = Position.from(rawTarget);
        final Map<Position, Piece> loadedBoard = pieceDao.load(boardId);
        final Color turn = boardDao.findTurn(boardId);
        final Board board = new Board(loadedBoard, turn);
        final ChessGame chessGame = new ChessGame(boardId, board);
        chessGame.move(source, target);
        savePieces(chessGame, source, target);
        if (chessGame.hasKingCaptured()) {
            boardDao.updateTurn(boardId, Color.NONE);
        }
    }

    private void savePieces(ChessGame chessGame, Position source, Position target) {
        boardDao.updateTurn(chessGame.getId(), chessGame.getBoard().getTurn());
        pieceDao.updatePosition(chessGame.getId(), source.stringName(), target.stringName());
    }

    public ChessBoardDto getBoard(Long boardId) {
        return ChessBoardDto.from(pieceDao.load(boardId));
    }

    public double statusOfWhite(Long boardId) {
        final Map<Position, Piece> loadedBoard = pieceDao.load(boardId);
        final Color turn = boardDao.findTurn(boardId);
        final Board board = new Board(loadedBoard, turn);
        final ChessGame chessGame = new ChessGame(boardId, board);

        return chessGame.scoreOfWhite();
    }

    public double statusOfBlack(Long boardId) {
        final Map<Position, Piece> loadedBoard = pieceDao.load(boardId);
        final Color turn = boardDao.findTurn(boardId);
        final Board board = new Board(loadedBoard, turn);
        final ChessGame chessGame = new ChessGame(boardId, board);

        return chessGame.scoreOfBlack();
    }

    public Winner findWinner(Long boardId) {
        final Map<Position, Piece> loadedBoard = pieceDao.load(boardId);
        final Color turn = boardDao.findTurn(boardId);
        final Board board = new Board(loadedBoard, turn);
        final ChessGame chessGame = new ChessGame(boardId, board);

        return chessGame.findWinner();
    }

    public void end(Long boardId) {
        boardDao.updateTurn(boardId, Color.NONE);
    }

    public Color getTurn(Long boardId) {
        return boardDao.findTurn(boardId);
    }

    public void restart(Long boardId) {
        boardDao.updateTurn(boardId, Color.WHITE);
        final Board board = new Board();
        pieceDao.updateAll(board.getPiecesByPosition(), boardId);
    }

    public void delete(Long boardId, String password) {
        final String loadedPassword = boardDao.findPasswordById(boardId);
        if (!password.equals(loadedPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        final Color turn = boardDao.findTurn(boardId);
        if (!(turn == Color.NONE)) {
            throw new IllegalStateException("종료하지 않은 게임은 삭제할 수 없습니다.");
        }
        pieceDao.delete(boardId);
        boardDao.deleteBoard(boardId);
    }

    public RoomsDto getRooms() {
        List<RoomDto> rooms = boardDao.findAllRooms();
        return new RoomsDto(rooms);
    }

    public boolean isGameEnd(Long boardId) {
        final Color turn = boardDao.findTurn(boardId);
        return turn == Color.NONE;
    }
}
