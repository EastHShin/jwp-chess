package chess.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import chess.domain.Winner;
import chess.dto.ResponseDto;
import chess.dto.ResultDto;
import chess.dto.StatusDto;
import chess.service.ChessGameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChessSpringController.class)
public class ChessSpringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChessGameService chessGameService;

    @Test
    @DisplayName("POST /create 테스트")
    void post_create() throws Exception {
        final ResponseDto responseDto = new ResponseDto(200, "");
        final String title = "abc";
        final String password = "123";
        final Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("password", password);
        given(chessGameService.create(title, password)).willReturn(new ResponseDto(200, ""));

        mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(content().json(responseDto.toJson()));
    }

//    @DisplayName("start Test- GET")
//    @Test
//    void get_start() throws Exception {
//        ResponseDto responseDto = new ResponseDto(200, "");
//        given(chessGameService.start()).willReturn(new ResponseDto(200, ""));
//
//        mockMvc.perform(get("/start"))
//                .andExpect(content().json(responseDto.toJson()));
//    }

//    @DisplayName("chess Test- GET")
//    @Test
//    void get_chess() throws Exception {
//        final ChessBoardDto chessBoardDto = ChessBoardDto.from(new Board().getPiecesByPosition());
//        given(chessGameService.getBoard()).willReturn(ChessBoardDto.from(new Board().getPiecesByPosition()));
//
//        mockMvc.perform(get("/chess"))
//                .andExpect(view().name("index"))
//                .andExpect(model().attribute("boardDto", chessBoardDto));
//    }

//    @DisplayName("move POST 요청 테스트")
//    @Test
//    void post_move() throws Exception {
//        final String requestString = "a2 a4";
//        final ResponseDto responseDto = new ResponseDto(302, "");
//        given(chessGameService.move("a2", "a4")).willReturn(responseDto);
//        mockMvc.perform(post("/move")
//                .content(requestString))
//                .andExpect(content().json(responseDto.toJson()));
//
//    }

    @DisplayName("status GET 요청 테스트")
    @Test
    void get_status() throws Exception {
        final StatusDto statusDto = StatusDto.of(37, 37);
        given(chessGameService.statusOfBlack()).willReturn(37.0);
        given(chessGameService.statusOfWhite()).willReturn(37.0);

        mockMvc.perform(get("/chess-status"))
                .andExpect(view().name("status"))
                .andExpect(model().attribute("status", statusDto));
    }

//    @DisplayName("end GET 요청 테스트")
//    @Test
//    void get_end() throws Exception {
//        ResponseDto responseDto = new ResponseDto(200, "");
//        given(chessGameService.end()).willReturn(new ResponseDto(200, ""));
//
//        mockMvc.perform(get("/end"))
//                .andExpect(content().json(responseDto.toJson()));
//    }

    @DisplayName("result GET 요청 테스트")
    @Test
    void get_result() throws Exception {
        final ResultDto resultDto = ResultDto.of(36, 37, Winner.BLACK);

        given(chessGameService.statusOfWhite()).willReturn(36.0);
        given(chessGameService.statusOfBlack()).willReturn(37.0);
        given(chessGameService.findWinner()).willReturn(Winner.BLACK);

        mockMvc.perform(get("/chess-result"))
                .andExpect(view().name("result"))
                .andExpect(model().attribute("result", resultDto));
    }
}
