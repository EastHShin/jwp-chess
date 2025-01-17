package chess.dao;

public class FakeBoard {

    private final String turn;
    private final String title;
    private final String password;

    public FakeBoard(String turn, String title, String password) {
        this.turn = turn;
        this.title = title;
        this.password = password;
    }

    public String getTurn() {
        return turn;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }
}
