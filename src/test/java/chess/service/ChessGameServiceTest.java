package chess.service;

import static org.assertj.core.api.Assertions.assertThat;

import chess.dao.MockBoardDao;
import chess.dao.MockPieceDao;
import chess.domain.Color;
import chess.domain.Winner;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import chess.dto.ChessBoardDto;
import chess.dto.ResponseDto;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessGameServiceTest {

    private ChessGameService chessGameService;
    private final String title = "abc";
    private final String password = "123";

    @BeforeEach
    void init(){
        chessGameService = new ChessGameService(new MockPieceDao(), new MockBoardDao());
    }

    @Test
    @DisplayName("입력된 이름으로 저장된 체스판이 없으면 생성한다.")
    void create_board() {
        //when
        chessGameService.create(title, password);
        //then
        final ChessBoardDto chessBoardDto = chessGameService.getBoard(1L);
        final Map<String, Piece> board = chessBoardDto.getBoard();
        assertThat(board.size()).isEqualTo(64);
    }

    @Test
    @DisplayName("중복된 이름의 방 제목을 생성하면 statusCode 401을 반환한다.")
    void create_duplicate_title_board() {
        //given
        chessGameService.create(title, password);
        //when
        final ResponseDto responseDto = chessGameService.create(title, password);
        //then
        assertThat(responseDto.getStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("move 명령을 했을 때 원하는 위치로 기물이 움직였는지 확인")
    void move_piece() {
        //given
        chessGameService.create(title, password);
        //when
        chessGameService.move(1L, "a2", "a4");
        //then
        final ChessBoardDto board = chessGameService.getBoard(1L);
        assertThat(board.getBoard().get("a4")).isInstanceOf(Pawn.class);
    }
    
    @Test
    @DisplayName("백색의 점수 반환")
    void status_white() {
        //given
        chessGameService.create(title, password);
        //when
        final double statusOfWhite = chessGameService.statusOfWhite(1L);
        //then
        assertThat(statusOfWhite).isEqualTo(38.0);
    }

    @Test
    @DisplayName("흑색의 점수 반환")
    void status_black() {
        //given
        chessGameService.create(title, password);
        //when
        final double statusOfBlack = chessGameService.statusOfBlack(1L);
        //then
        assertThat(statusOfBlack).isEqualTo(38.0);
    }

    @Test
    @DisplayName("백색이 이긴 경우에 Winner.WHITE 반환")
    void find_winner() {
        //given
        chessGameService.create(title, password);
        chessGameService.move(1L, "a2", "a4");
        chessGameService.move(1L, "b7", "b5");
        chessGameService.move(1L, "a4", "a5");
        chessGameService.move(1L, "c7", "c6");
        chessGameService.move(1L, "a1", "a7");
        //when
        final Winner winner = chessGameService.findWinner(1L);
        //then
        assertThat(winner).isEqualTo(Winner.WHITE);
    }

    @Test
    @DisplayName("게임을 끝내면 turn 이 Color.NONE 으로 변경")
    void end_game() {
        //given
        chessGameService.create(title, password);
        //when
        chessGameService.end(1L);
        //then
        final Color turn = chessGameService.getTurn(1L);
        assertThat(turn).isEqualTo(Color.NONE);

    }
}
