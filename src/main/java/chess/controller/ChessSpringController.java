package chess.controller;

import chess.dto.ResponseDto;
import chess.dto.ResultDto;
import chess.dto.RoomInfoDto;
import chess.dto.StatusDto;
import chess.service.ChessGameService;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChessSpringController {
    private final ChessGameService chessGameService;

    public ChessSpringController(ChessGameService chessGameService) {
        this.chessGameService = chessGameService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/start")
//    public @ResponseBody
//    ResponseDto start() {
//        return chessGameService.start();
//    }

    @PostMapping("/create")
    @ResponseBody
    ResponseDto create(@RequestBody RoomInfoDto roomInfoDto) {
        return chessGameService.create(roomInfoDto.getTitle(), roomInfoDto.getPassword());
    }

//    @GetMapping("/chess")
//    public ModelAndView chess() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("boardDto", chessGameService.getBoard());
//        modelAndView.setViewName("index");
//        return modelAndView;
//    }
//
//    @PostMapping("/move")
//    public @ResponseBody
//    ResponseDto move(@RequestBody String request) {
//        List<String> command = Arrays.asList(request.split(" "));
//        return chessGameService.move(command.get(0), command.get(1));
//    }

    @PostMapping("/board/move")
    @ResponseBody
    public ResponseDto move(@RequestParam(name = "id") Long id, @RequestBody String request) {
        List<String> command = Arrays.asList(request.split(" "));
        return chessGameService.move(id, command.get(0), command.get(1));
    }

//    @GetMapping("/chess-status")
//    public ModelAndView status() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView
//                .addObject("status", StatusDto.of(chessGameService.statusOfWhite(), chessGameService.statusOfBlack()));
//        modelAndView.setViewName("status");
//        return modelAndView;
//    }

    @GetMapping("/board/chess-status")
    public ModelAndView status(@RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView
                .addObject("status",
                        StatusDto.of(chessGameService.statusOfWhite(id), chessGameService.statusOfBlack(id)));
        modelAndView.setViewName("status");
        return modelAndView;
    }

    @GetMapping("/board/chess-result")
    public ModelAndView result(@RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView
                .addObject("result", ResultDto
                        .of(chessGameService.statusOfWhite(id), chessGameService.statusOfBlack(id),
                                chessGameService.findWinner(id)));
        modelAndView.setViewName("result");
        return modelAndView;
    }

    @PostMapping("/board/end")
    @ResponseBody
    public ResponseDto end(@RequestParam(name = "id") Long id) {
        return chessGameService.end(id);
    }

    @PostMapping("/board/restart")
    @ResponseBody
    public ResponseDto restart(@RequestParam(name = "id") Long id) {
        return chessGameService.restart(id);
    }
}
