package edu.phystech.davydovmv;

import lombok.Getter;
import java.io.IOException;

@Getter
public class TowersBoard extends Board {

    public TowersBoard(String whitePoses, String blackPoses) {
        super(whitePoses, blackPoses, "[a-hA-H][1-8]_([bwBW])*");
    }

    public TowersBoard(String pathStr) throws
            IOException {
        super(pathStr, "[a-hA-H][1-8]_([bwBW])*");
    }

    public TowersBoard() {
        super("[a-hA-H][1-8]_([bwBW])*");
    }

    /**
     * Создает объект класса Tower, у которой верхняя шашка цвета colour с позицией position
     * @param colour Цвет верхней шашки объекта
     * @param position Позиция объекта
     * @return Созданную башню
     */
    @Override
    protected Figure createFigure(Colour colour, String position) {
        return new Tower(colour, position);
    }

    /**
     * Проверяет, правда ли, что, башня figure, перейдя на новую позицию, может иметь позицию в записи хода endPos.
     * Если нет, бросает GeneralErrorException
     * @param figure Двигающаяся башня
     * @param endPos Конечная позиция в ходу
     * @param itIsBeating Бьет ли башня другую башня во время этого хода
     * @throws GeneralErrorException
     */
    @Override
    protected void checkIfNotationIsRightElseThrow(Figure figure, String endPos, boolean itIsBeating)
            throws GeneralErrorException {
        String subcheckers = figure.getFullPosition().substring(3);
        if (itIsBeating) {
            if (!subcheckers.equals(endPos.substring(3, endPos.length() - 1))) {
                throw new GeneralErrorException("general error");
            }
            char lastSymb = endPos.charAt(endPos.length() - 1);
            if (figure.getColour() == Colour.WHITE && lastSymb != 'b' && lastSymb != 'B'
                    || figure.getColour() == Colour.BLACK && lastSymb != 'w' && lastSymb != 'W') {
                throw new GeneralErrorException("general error");
            }
        } else if (!subcheckers.equals(endPos.substring(3))) {
            throw new GeneralErrorException("general error");
        }
    }

    /**
     * Проверяет финальное совпадение нотаций
     * @param figureToBeat Башня, которую нужно побить
     * @param endPos Конечная позиция
     * @throws GeneralErrorException
     */
    @Override
    protected void checkFinalCorectnessOfNotation(Figure figureToBeat, String endPos)
            throws GeneralErrorException {
        char lastSymb = endPos.charAt(endPos.length() - 1);
        if ((lastSymb == 'B' || lastSymb == 'W') && !figureToBeat.isKing()
                || (lastSymb == 'b' || lastSymb == 'w') && figureToBeat.isKing()) {
            throw new GeneralErrorException("general error");
        }
    }

    /**
     * Бьет башней movingFigure башню figureToBeat.
     * @param movingFigure Двигающаяся башня.
     * @param figureToBeat Башня на доске, которую бьют.
     */
    @Override
    protected void beatFigure(Figure movingFigure, Figure figureToBeat) {
        Tower mainTower = (Tower) movingFigure;
        Tower towerToSteal = (Tower) figureToBeat;

        mainTower.stealChecker(towerToSteal);
        figureToBeat.tagBeaten();
    }
}
