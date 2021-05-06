package chess.dao;

import chess.dto.RoomDTO;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDAO {

    private final JdbcTemplate jdbcTemplate;

    public RoomDAO(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createRoom(final String title, final int userId) {
        String query = "INSERT INTO room (white_id, title, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, userId, title, "준비중");
    }

    public String createdRoomId() {
        String query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    public List<RoomDTO> allRooms() {
        String query =
            "SELECT room.id, room.title, black.nickname AS black_nickname, white.nickname AS white_nickname, room.status "
                +
                "FROM room LEFT JOIN user as black on black.id = room.black_id " +
                "LEFT JOIN user as white on white.id = room.white_id ORDER BY room.id DESC";
        return jdbcTemplate.query(query, mapper());
    }

    public void enrollBlackUser(final String roomId, final int blackUserId) {
        String query = "UPDATE room SET black_id=? WHERE id=?";
        jdbcTemplate.update(query, blackUserId, roomId);
    }

    public void enrollWhiteUser(final String roomId, final int whiteUserId) {
        String query = "UPDATE room SET white_id=? WHERE id=?";
        jdbcTemplate.update(query, whiteUserId, roomId);
    }

    private RowMapper<RoomDTO> mapper() {
        return (resultSet, rowNum) -> {
            return new RoomDTO(
                resultSet.getInt("id"),
                resultSet.getString("black_nickname"),
                resultSet.getString("white_nickname"),
                resultSet.getString("title"),
                resultSet.getString("status")
            );
        };
    }

    public void changeStatusEndByRoomId(final String roomId) {
        String query = "UPDATE room SET status = '종료됨' WHERE id = ?";
        jdbcTemplate.update(query, roomId);
    }

    public List<String> allRoomIds() {
        RowMapper<String> rowMapper = (resultSet, rowNum) -> resultSet.getString("id");

        String query = "SELECT id FROM room";
        return jdbcTemplate.query(query, rowMapper);
    }

    public String findBlackUserPassword(final String roomId) {
        String query =
            "SELECT black.password FROM room JOIN user as black on room.black_id = black.id "
                + "WHERE room.id = ?";
        return jdbcTemplate.queryForObject(query, String.class, roomId);
    }

    public String findWhiteUserPassword(final String roomId) {
        String query =
            "SELECT white.password FROM room JOIN user as white on room.white_id = white.id "
                + "WHERE room.id = ?";
        return jdbcTemplate.queryForObject(query, String.class, roomId);
    }
}