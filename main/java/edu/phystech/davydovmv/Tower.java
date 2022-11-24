package edu.phystech.davydovmv;

import java.util.ArrayDeque;
import java.util.Objects;

public class Tower extends Figure {
    private ArrayDeque<Checker> checkers;

    public Tower(Colour colour, String position) {
        checkers = new ArrayDeque<>();
        setColour(colour);
        if (position.length() == 2) {
            checkers.add(new Checker(colour, position));
        } else {
            String place = position.substring(0, 2);
            String allCheckersInPosition = position.substring(3);
            for (int i = 0; i < allCheckersInPosition.length(); ++i) {
                switch (allCheckersInPosition.charAt(i)) {
                    case 'w':
                        checkers.offerLast(new Checker(Colour.WHITE, place.toLowerCase()));
                        break;
                    case 'W':
                        checkers.offerLast(new Checker(Colour.WHITE, place.toUpperCase()));
                        break;
                    case 'b':
                        checkers.offerLast(new Checker(Colour.BLACK, place.toLowerCase()));
                        break;
                    case 'B':
                        checkers.offerLast(new Checker(Colour.BLACK, place.toUpperCase()));
                        break;
                }
            }
            setPosition(position);
        }
    }

    /**
     *
     * @return Позицию на доске
     */
    @Override
    public String getBoardPosition() {
        return checkers.getFirst().getBoardPosition();
    }

    /**
     *
     * @return Является ли верхняя шашка дамкой
     */
    @Override
    public boolean isKing() {
        return checkers.getFirst().isKing() || isTaggedAsKing();
    }

    /**
     * Делает позицию в записи хода равной newPos и меняет позицию каждой шашки на позицию на доске,
     * соответствующую newPos
     * @param newPos Новая позиция в запси хода
     */
    @Override
    public void setPosition(String newPos) {
        for (var checker: checkers) {
            checker.setPosition(checker.isKing() ? newPos.substring(0, 2).toUpperCase() : newPos.substring(0, 2));
        }
        super.setPosition(newPos);
    }

    /**
     * Делает верхнюю шашку в башне дамкой
     */
    @Override
    protected void makeKing() {
        checkers.getFirst().makeKing();
        char kingChar = getColour() == Colour.WHITE ? 'W' : 'B';
        setPosition(getFullPosition().substring(0, 3) + kingChar + getFullPosition().substring(4));
    }

    /**
     * Если башню били на этом ходу, меняет цвет и позицию башни на значения, которые должны быть у башни после побития.
     * Если башня пуста, обнуляет позицию и цвет башни (то есть делает башню невалидной).
     */
    @Override
    public void unlock() {
        if (isInvalid()) {
            setPosition(null);
            setColour(null);
        } else {
            if (wasBeaten()) {
                setPosition(getFullPosition().substring(0, 3) + getFullPosition().substring(4));
            }
            setColour(checkers.getFirst().getColour());
        }
        tagUnbeaten();
    }

    /**
     * Сравнивает 2 башни на равенство
     * @param o Другая башня
     * @return Равны ли башни
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tower tower = (Tower) o;
        if (isInvalid() || tower.isInvalid()) {
            return false;
        }
        return checkers.getFirst().equals(tower.checkers.getFirst());
    }

    /**
     *
     * @return Хеш башни
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFullPosition());
    }

    private Checker removeChecker() {
        return checkers.removeFirst();
    }

    private void offerChecker(Checker checker) {
        checkers.offerLast(checker);
    }

    /**
     * Берет верхнюю шашку у @tower и перемещает в низ башни. При этом @pos у обворованной башни меняется,
     * а у ворующей башни - нет
     * @param tower Башня, у которой крадется верхняя шашка
     */
    public void stealChecker(Tower tower) {
        Checker stolenChecker = tower.removeChecker();
        offerChecker(stolenChecker);
    }

    /**
     * Проверяет, можно ли с этой башней взаимодействовать (то есть валидна ли башня). Влидна башня только в том случае,
     * когда она содержит хотя бы 1 шашку.
     * @return Можно ли с этой башней взаимодействовать.
     */
    public boolean isInvalid() {
        return checkers.isEmpty();
    }
}
