package edu.phystech.davydovmv;

import java.io.IOException;

public final class Main {

    private Main() { }

    public static void main(String[] args)
            throws IOException {
        try {
            Board board = new TowersBoard("input.txt");
            board.run();
        } catch (Board.BusyCellException
                 | Board.WhiteCellException
                 | Board.InvalidMoveException
                 | Board.GeneralErrorException e) {
            System.out.println(e.getMessage());
        }
    }
}
