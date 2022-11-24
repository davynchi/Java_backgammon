package edu.phystech.davydovmv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.*;

public class TowersBoardTest {
    @Test
    void CreateBoardTest() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/only_create_board.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "a1_w a3_w b2_w c1_w c3_w d2_w e1_w e3_w f2_w g1_w g3_w h2_w \na7_b b6_b b8_b c7_b d6_b d8_b e7_b f6_b f8_b g7_b h6_b h8_b \n");
    }

    @Test
    void OneMoveTest() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/one_move.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "a1_w a3_w b2_w c1_w d2_w d4_w e1_w e3_w f2_w g1_w g3_w h2_w \na7_b b6_b b8_b c7_b d6_b d8_b e7_b f6_b f8_b g5_b g7_b h8_b \n");
    }

    @Test
    void OneKingMove() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/one_king_move.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "b6_w c1_ww h6_Wbw \na3_bbw b4_B h4_bBW \n");
    }

    @Test
    void OneBeat() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/one_beat.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "c5_Ww f4_w g5_wbwbb \nb4_bBwWw f2_bBb \n");
    }

    @Test
    void BeatInARow() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/beat_in_a_row.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "c3_w d4_ww \nc1_Bwww d2_bbbb e5_B \n");
    }

    @Test
    void KingBeatInARow() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/king_beat_in_a_row.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "d6_wB g7_wb \nb6_BW c1_BwwW c3_bb d2_Bbbb \n");
    }

    void GeneralError(int i) throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/general_errors/general_error_"
                        + i
                        + ".txt");
        Board.GeneralErrorException thrown =
                Assertions.assertThrows(Board.GeneralErrorException.class, board::run);
        Assertions.assertEquals("general error", thrown.getMessage());
    }

    @Test
    void GeneralErrors() throws IOException {
        for (int i = 1; i <= 17; ++i) {
            GeneralError(i);
        }
    }

    @Test
    void BusyCell() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/busy_cell.txt");
        Board.BusyCellException thrown =
                Assertions.assertThrows(Board.BusyCellException.class, board::run);
        Assertions.assertEquals("busy cell", thrown.getMessage());
    }

    @Test
    void WhiteCell() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/white_cell.txt");
        Board.WhiteCellException thrown =
                Assertions.assertThrows(Board.WhiteCellException.class, board::run);
        Assertions.assertEquals("white cell", thrown.getMessage());
    }

    void InvalidMove(int i) throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/invalid_moves/invalid_move_"
                        + i
                        + ".txt");
        Board.InvalidMoveException thrown =
                Assertions.assertThrows(Board.InvalidMoveException.class, board::run);
        Assertions.assertEquals("invalid move", thrown.getMessage());
    }

    @Test
    void InvalidMoves() throws IOException {
        for (int i = 1; i <= 4; ++i) {
            InvalidMove(i);
        }
    }

    @Test
    void GameEnded() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/game_ended.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "c3_wb \n\n");
    }

    @Test
    void LongGame1() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/long_games/long_game_1.txt");
        Board.InvalidMoveException thrown =
                Assertions.assertThrows(Board.InvalidMoveException.class, board::run);
        Assertions.assertEquals("invalid move", thrown.getMessage());
    }

    @Test
    void LongGame2() throws IOException {
        Board board = new TowersBoard(
                "/home/mikhail/Java/checkers/src/test/java/edu/phystech/davydovmv/TowersBoard/long_games/long_game_2.txt");
        assertThat(Assertions.assertDoesNotThrow(board::run)).isEqualTo(
                "a7_wbb b6_Wwbbbb c1_w e1_w e5_ww f2_w g1_w \na3_bwww b8_b f8_b g5_b g7_b h8_b \n");
    }
}
