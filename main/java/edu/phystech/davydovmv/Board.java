package edu.phystech.davydovmv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class Board {

    /**
     * Ошибка: целевая клетка занята
     */
    public static class BusyCellException extends Exception {
        BusyCellException(String message) {
            super(message);
        }
    }

    /**
     * Ошибка: целевая клетка белая
     */
    public static class WhiteCellException extends Exception {
        WhiteCellException(String message) {
            super(message);
        }
    }

    /**
     * Ошибка: у игрока есть вариант побить фигуру, но он его не использует, а идёт на другую клетку.
     */
    public static class InvalidMoveException extends Exception {
        InvalidMoveException(String message) {
            super(message);
        }
    }

    /**
     * Ошибка: любая ошибка, не совпадающая с BusyCellException, WhiteCellException или InvalidMoveException
     */
    public static class GeneralErrorException extends Exception {
        GeneralErrorException(String message) {
            super(message);
        }
    }


    private ArrayList<Figure> board;
    private List<String> movesAndTurns;
    private boolean gameEnded = false;

    private String correctPositionPattern;
    private String correctMovePattern;
    private String correctTwoSideMovePattern;

    protected abstract Figure createFigure(Colour colour, String position);

    private Figure createFigure(Colour colour, StringBuilder position) {
        return createFigure(colour, position.toString());
    }

    protected abstract void beatFigure(Figure movingFigure, Figure figureToBeat);

    protected abstract void checkIfNotationIsRightElseThrow(Figure figure, String endPos, boolean itIsBeating)
            throws GeneralErrorException;

    protected abstract void checkFinalCorectnessOfNotation(Figure figureToBeat, String endPos)
            throws GeneralErrorException;

    /**
     * Возвращает фигуру на доске с данным индексом. Предполагается, что по индексу возможно достать фигуру
     * @param index Индекс фигуры на доске
     * @return Фигуру с индексом @index
     */
    protected Figure getFromBoard(int index) {
        return board.get(index);
    }

    /**
     * Возвращает позицию фигуры цвета colour с позицией на доске boardPos. Предполагается, что такая фигура существует
     * @param colour Цвет фигуры
     * @param boardPos Позиция фигуры на доске
     * @return Индекс фигуры с позицией на доске boardPos цвета colour
     */
    protected int getIndexFromBoard(Colour colour, String boardPos) {
        return board.indexOf(createFigure(colour, boardPos));
    }

    private void fillPatterns(String newCorrectPositionPattern) {
        this.correctPositionPattern = newCorrectPositionPattern;
        correctMovePattern = "((".concat(correctPositionPattern).concat(":)+").concat(correctPositionPattern)
                .concat(")|(").concat(correctPositionPattern).concat("-").concat(correctPositionPattern)
                .concat(")");
        correctTwoSideMovePattern = "(".concat(correctMovePattern).concat(") (").concat(correctMovePattern)
                .concat(")");
    }

    public Board(String whitePoses, String blackPoses, String correctPositionPattern) {
        fillPatterns(correctPositionPattern);
        board = new ArrayList<>();
        addAllOneColouredElementsOnBoard(Colour.WHITE, whitePoses);
        addAllOneColouredElementsOnBoard(Colour.BLACK, blackPoses);
    }

    public Board(String pathStr, String correctPositionPattern) throws
            IOException {
        fillPatterns(correctPositionPattern);
        Path path = Paths.get(pathStr);
        movesAndTurns =  Files.readAllLines(path, StandardCharsets.UTF_8);
        board = new ArrayList<>();
    }

    public Board(String correctPositionPattern) {
        fillPatterns(correctPositionPattern);
        board = new ArrayList<>();

        Scanner scan = new Scanner(System.in);
        String whitePoses = scan.nextLine();
        movesAndTurns = new ArrayList<>();
        movesAndTurns.add(whitePoses);
        String blackPoses = scan.nextLine();
        movesAndTurns.add(blackPoses);
        String move;
        while (scan.hasNext()) {
            move = scan.nextLine();
            movesAndTurns.add(move);
        }
    }

    private void addAllOneColouredElementsOnBoard(Colour colour, String positions) {
        Pattern pattern = Pattern.compile(correctPositionPattern);
        Matcher matcher = pattern.matcher(positions);
        while (matcher.find()) {
            String position = matcher.group();
            board.add(createFigure(colour, position));
        }
    }

    /**
     * Симулирует ходы, которые Board до этого получил в конструкторе
     * @return Итоговые позиции всех оставшихся фигур после симуляции ходов
     * @throws BusyCellException
     * @throws WhiteCellException
     * @throws InvalidMoveException
     * @throws GeneralErrorException
     */
    public String run() throws
            BusyCellException,
            WhiteCellException,
            InvalidMoveException,
            GeneralErrorException,
            IOException {
        addAllOneColouredElementsOnBoard(Colour.WHITE, movesAndTurns.get(0));
        addAllOneColouredElementsOnBoard(Colour.BLACK, movesAndTurns.get(1));
        for (int i = 2; i < movesAndTurns.size(); ++i) {
            makeMoves(movesAndTurns.get(i));
        }
        return printAllFigures();
    }

    //Long naming but understandable
    private boolean checkIfFigureOfOppositeColourExistsOnPosAndUnbeatenAndAfterThisFigureNoFiguresInThisCaseThrow(
            StringBuilder pos, Colour colour, int biasX, int biasY)
            throws InvalidMoveException {
        int indexOfFigureToBeat = board.indexOf(createFigure(colour, pos));
        if (indexOfFigureToBeat == -1) {
            return false;
        }
        Figure figureToBeat = board.get(indexOfFigureToBeat);
        if (figureToBeat.wasBeaten()) {
            return true;
        }
        Figure.displacePosition(pos, biasX, biasY);
        if (!board.contains(createFigure(Colour.WHITE, pos))
                && !board.contains(createFigure(Colour.BLACK, pos))) {
            throw new InvalidMoveException("invalid move");
        } else {
            return true;
        }
    }

    private void checkForKingIfFigureCanBeatIllegallyInThisCaseThrow(Figure figure, int biasX, int biasY)
            throws InvalidMoveException {
        StringBuilder pos = new StringBuilder(figure.getBoardPosition());
        Colour oppositeColour = getOppositeColour(figure);
        Figure.displacePosition(pos, biasX, biasY);
        while (pos.charAt(0) > 'a'
                && pos.charAt(0) < 'h'
                && pos.charAt(1) > '1'
                && pos.charAt(1) < '8') {
            if (board.contains(createFigure(figure.getColour(), pos))) {
                return;
            }
            if (checkIfFigureOfOppositeColourExistsOnPosAndUnbeatenAndAfterThisFigureNoFiguresInThisCaseThrow(
                    pos, oppositeColour, biasX, biasY)) {
                return;
            }
            Figure.displacePosition(pos, biasX, biasY);
        }
    }

    private void checkForNotKingIfFigureCanBeatIllegallyInThisCaseThrow(Figure figure, int biasX, int biasY)
            throws InvalidMoveException {
        StringBuilder pos = new StringBuilder(figure.getBoardPosition());
        Colour oppositeColour = getOppositeColour(figure);
        Figure.displacePosition(pos, biasX, biasY);
        checkIfFigureOfOppositeColourExistsOnPosAndUnbeatenAndAfterThisFigureNoFiguresInThisCaseThrow(
                pos, oppositeColour, biasX, biasY);
    }

    private void checkIfFigureCanBeatIllegallyInThisCaseThrow(Figure figure, int biasX, int biasY) throws
            InvalidMoveException {
        if (figure.isKing()) {
            checkForKingIfFigureCanBeatIllegallyInThisCaseThrow(figure, biasX, biasY);
        } else {
            checkForNotKingIfFigureCanBeatIllegallyInThisCaseThrow(figure, biasX, biasY);
        }
    }

    private void checkIfFigureCanBeatIllegallyInThisCaseThrow(Figure figure) throws InvalidMoveException {
        String beginPos = figure.getBoardPosition();
        if (beginPos.charAt(0) > 'b' && beginPos.charAt(1) > '2') {
            checkIfFigureCanBeatIllegallyInThisCaseThrow(figure, -1, -1);
        }
        if (beginPos.charAt(0) > 'b' && beginPos.charAt(1) < '7') {
            checkIfFigureCanBeatIllegallyInThisCaseThrow(figure, -1, 1);
        }
        if (beginPos.charAt(0) < 'g' && beginPos.charAt(1) > '2') {
            checkIfFigureCanBeatIllegallyInThisCaseThrow(figure, 1, -1);
        }
        if (beginPos.charAt(0) < 'g' && beginPos.charAt(1) < '7') {
            checkIfFigureCanBeatIllegallyInThisCaseThrow(figure, 1, 1);
        }
    }

    private void checkIfFigureCanBeatIllegallyInThisCaseThrow(Colour colour) throws InvalidMoveException {
        for (var figure: board) {
            if (figure.getColour() == colour) {
                checkIfFigureCanBeatIllegallyInThisCaseThrow(figure);
            }
        }
    }


    private void checkIfPosToMoveIsBlackElseThrow(String endPos) throws
            WhiteCellException {
        if ((endPos.charAt(0) + endPos.charAt(1)) % 2 != 0) {
            throw new WhiteCellException("white cell");
        }
    }

    private void checkIfPosToMoveIsNearElseThrow(Figure figure, String endPos, boolean itIsBeating) throws
            GeneralErrorException {
        StringBuilder beginPos = figure.getFullPosition();
        int diffX = Math.abs(beginPos.charAt(0) - endPos.charAt(0));
        int diffY = Math.abs(beginPos.charAt(1) - endPos.charAt(1));
        if (!figure.isKing()
                && (itIsBeating && (diffX != 2 || diffY != 2)
                || !itIsBeating && (diffX != 1 || diffY != 1))
                || diffX != diffY) {
            throw new GeneralErrorException("general error");
        }
    }

    private void checkIfPosToMoveIsFreeElseThrow(String endPos) throws BusyCellException {
        if (board.contains(createFigure(Colour.WHITE, endPos)) || board.contains(createFigure(Colour.BLACK, endPos))) {
            throw new BusyCellException("busy cell");
        }
    }

    private void moveFigure(Figure figure, String endPos) {
        figure.setPosition(endPos);
        figure.tagKingIfCorrectPosition();
    }

    private void moveFigureToPos(Figure figure, String endPos) throws
            BusyCellException,
            WhiteCellException,
            InvalidMoveException,
            GeneralErrorException {
        checkIfNotationIsRightElseThrow(figure, endPos, false);
        checkIfFigureCanBeatIllegallyInThisCaseThrow(figure.getColour());
        checkIfPosToMoveIsBlackElseThrow(endPos);
        checkIfPosToMoveIsNearElseThrow(figure, endPos, false);
        checkIfPosToMoveIsFreeElseThrow(endPos);
        figure.setPosition(endPos);
        figure.makeKingIfCorrectPosition();
    }

    private int checkIfRequiredFigureExistsElseThrow(String beginPos, Colour colour) throws
            GeneralErrorException  {
        int figureIndex = board.indexOf(createFigure(colour, beginPos));
        if (figureIndex == -1 || !board.get(figureIndex).getStringFullPosition().equals(beginPos)) {
            throw new GeneralErrorException("general error");
        }
        return figureIndex;
    }

    private void moveFigure(String move, Colour colour) throws
            BusyCellException,
            WhiteCellException,
            InvalidMoveException,
            GeneralErrorException {
        Pattern pattern = Pattern.compile(correctPositionPattern);
        Matcher matcher = pattern.matcher(move);
        Figure figure = null;
        if (matcher.find()) {
            String beginPos = matcher.group();
            int figureIndex = checkIfRequiredFigureExistsElseThrow(beginPos, colour);
            figure = board.get(figureIndex);
        }
        if (matcher.find()) {
            String endPos = matcher.group();
            moveFigureToPos(figure, endPos);
        }
    }

    private void beatFigureOnPathIfNotKing(Figure figure, String endPos) throws
            GeneralErrorException  {
        StringBuilder pos = new StringBuilder(figure.getFullPosition());
        int biasX = pos.charAt(0) < endPos.charAt(0) ? 1 : -1;
        int biasY = pos.charAt(1) < endPos.charAt(1) ? 1 : -1;
        Figure.displacePosition(pos, biasX, biasY);

        Colour oppositeColour = getOppositeColour(figure);
        int beatingFigureIndex = board.indexOf(createFigure(oppositeColour, pos));
        if (beatingFigureIndex != -1) {
            beatFigureWithIndex(figure, beatingFigureIndex, endPos);
        } else {
            throw new GeneralErrorException("general error");
        }
    }

    private void beatFigureWithIndex(Figure figure, int beatingFigureIndex, String endPos)
            throws GeneralErrorException {
        Figure beatingFigure = getFromBoard(beatingFigureIndex);
        if (beatingFigure.wasBeaten()) {
            throw new GeneralErrorException("general error");
        }
        checkFinalCorectnessOfNotation(beatingFigure, endPos);
        beatFigure(figure, beatingFigure);
    }

    private void beatFigureOnPathIfKing(Figure figure, String endPos) throws
            GeneralErrorException {
        StringBuilder pos = new StringBuilder(figure.getFullPosition());
        int biasX = pos.charAt(0) < endPos.charAt(0) ? 1 : -1;
        int biasY = pos.charAt(1) < endPos.charAt(1) ? 1 : -1;
        Figure.displacePosition(pos, biasX, biasY);
        Colour oppositeColour = getOppositeColour(figure);
        int beatingFigureIndex = -1;
        int countOfBeatingFigures = 0;
        while (pos.charAt(0) != endPos.charAt(0)) {
            int oppositeFigureIndex = board.indexOf(createFigure(oppositeColour, pos));
            if (board.contains(createFigure(figure.getColour(), pos))) {
                throw new GeneralErrorException("general error");
            } else if (oppositeFigureIndex != -1 && countOfBeatingFigures == 0) {
                beatingFigureIndex = oppositeFigureIndex;
                ++countOfBeatingFigures;
            } else if (oppositeFigureIndex != -1 && countOfBeatingFigures > 0) {
                throw new GeneralErrorException("general error");
            }
            Figure.displacePosition(pos, biasX, biasY);
        }
        if (countOfBeatingFigures == 0) {
            throw new GeneralErrorException("general error");
        } else {
            beatFigureWithIndex(figure, beatingFigureIndex, endPos);
        }
    }

    private void beatFigureOnPath(Figure figure, String endPos) throws
            GeneralErrorException {
        if (figure.isKing()) {
            beatFigureOnPathIfKing(figure, endPos);
        } else {
            beatFigureOnPathIfNotKing(figure, endPos);
        }
    }

    private void moveFigureWithBeating(Figure figure, String endPos) throws
            BusyCellException,
            WhiteCellException,
            GeneralErrorException {
        checkIfNotationIsRightElseThrow(figure, endPos, true);
        checkIfPosToMoveIsBlackElseThrow(endPos);
        checkIfPosToMoveIsNearElseThrow(figure, endPos, true);
        checkIfPosToMoveIsFreeElseThrow(endPos);
        beatFigureOnPath(figure, endPos);
        moveFigure(figure, endPos);
    }

    private void removeFigures() {
        for (int i = board.size() - 1; i >= 0; --i) {
            if (board.get(i).isInvalid()) {
                board.remove(i);
            }
        }
    }

    private void unlockFigures() {
        for (var figure: board) {
            figure.unlock();
        }
    }

    /**
     * Бьет в ряд фигурой цвета @colour шашки на позициях в @move
     * @param move Ход, в котором записано, на каких позициях надо бить
     * @param colour Цвет бьющей шашки
     * @throws BusyCellException
     * @throws WhiteCellException
     * @throws InvalidMoveException
     * @throws GeneralErrorException
     */
    private void beatInARow(String move, Colour colour) throws
            BusyCellException,
            WhiteCellException,
            InvalidMoveException,
            GeneralErrorException {
        Pattern pattern = Pattern.compile(correctPositionPattern);
        Matcher matcher = pattern.matcher(move);
        ArrayList<String> subposes = new ArrayList<>();
        while (matcher.find()) {
            subposes.add(matcher.group());
        }
        int figureIndex = checkIfRequiredFigureExistsElseThrow(subposes.get(0), colour);
        Figure figure = board.get(figureIndex);
        for (int i = 0; i < subposes.size() - 1; ++i) {
            moveFigureWithBeating(figure, subposes.get(i + 1));
        }
        figure.makeKingIfTaggedEarlier();
        checkIfFigureCanBeatIllegallyInThisCaseThrow(figure);

        removeFigures();
        unlockFigures();
    }

    private void makeSpecificMove(String move, Colour colour) throws
            BusyCellException,
            WhiteCellException,
            InvalidMoveException,
            GeneralErrorException {
        Pattern pattern = Pattern.compile("[:-]");
        Matcher matcher = pattern.matcher(move);
        if (matcher.find()) {
            if (matcher.group().equals("-")) {
                moveFigure(move, colour);
            } else {
                beatInARow(move, colour);
            }
        }
    }

    private void checkIfMovesAreCorrectElseThrow(String moves) throws GeneralErrorException {
        if (gameEnded) {
            throw new GeneralErrorException("general error");
        }
        boolean inputMovesWasWrittenCorrectly = moves.matches(correctTwoSideMovePattern);
        if (!inputMovesWasWrittenCorrectly) {
            boolean itsFinalTurn = moves.matches(correctMovePattern);
            if (!itsFinalTurn) {
                throw new GeneralErrorException("general error");
            }
        }
    }

    /**
     * Для данной фигуры выдает цвет, противоположный цвету фигуры
     * @param figure Фиугра, у которой хотим узнать противоположный цвет
     * @return Цвет, против-ый цвету фигуры
     */
    public Colour getOppositeColour(Figure figure) {
        return figure.getColour() == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
    }

    /**
     * Делает 1 ход белыми и черными
     * @param moves Ход, который нужно произвести
     * @throws GeneralErrorException
     * @throws BusyCellException
     * @throws WhiteCellException
     * @throws InvalidMoveException
     */
    public void makeMoves(String moves) throws
            GeneralErrorException,
            BusyCellException,
            WhiteCellException,
            InvalidMoveException {
        checkIfMovesAreCorrectElseThrow(moves);
        Pattern pattern = Pattern.compile(correctMovePattern);
        Matcher matcher = pattern.matcher(moves);
        if (matcher.find()) {
            String whiteMove = matcher.group();
            makeSpecificMove(whiteMove, Colour.WHITE);
        } else {
            throw new GeneralErrorException("general error");
        }
        if (matcher.find()) {
            String blackMove = matcher.group();
            makeSpecificMove(blackMove, Colour.BLACK);
        } else {
            for (var figure : board) {
                if (figure.getColour() == Colour.BLACK) {
                    throw new GeneralErrorException("general error");
                }
            }
            gameEnded = true;
        }
    }

    private void sortFiguresInBoard() {
        board.sort((c1, c2) -> {
            if (c1.getColour() == Colour.WHITE && c2.getColour() == Colour.BLACK) {
                return -1;
            }
            if (c1.getColour() == Colour.BLACK && c2.getColour() == Colour.WHITE) {
                return 1;
            }
            return c1.getFullPosition().compareTo(c2.getFullPosition());
        });
    }

    private String getAllFiguresInString() throws IOException {
        StringBuilder finalString = new StringBuilder();
        Iterator<Figure> itr = board.iterator();
        Figure figure = null;
        boolean meetBlackFigure = false;
        while (itr.hasNext()) {
            figure = itr.next();
            if (figure.getColour() == Colour.BLACK) {
                meetBlackFigure = true;
                break;
            }
            finalString.append(figure.getStringFullPosition()).append(' ');
        }
        finalString.append('\n');
        if (meetBlackFigure) {
            finalString.append(figure.getStringFullPosition()).append(' ');
            while (itr.hasNext()) {
                figure = itr.next();
                finalString.append(figure.getStringFullPosition()).append(' ');
            }
        }
        finalString.append('\n');
        return finalString.toString();
    }

    /**
     * Печатает позиции шашек
     * @return Строка, в которой зписаны итоговые позиции шашек: на 1 строке все белые, на 2 строке все чёрные
     * @throws GeneralErrorException
     */
    public String printAllFigures() throws GeneralErrorException, IOException {
        if (board.isEmpty()) {
            throw new GeneralErrorException("general error");
        }

        sortFiguresInBoard();

        String finalString = getAllFiguresInString();
        System.out.print(finalString);
        return finalString;
    }
}
