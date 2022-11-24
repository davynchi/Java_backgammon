package edu.phystech.davydovmv;

public abstract class Figure {

    private StringBuilder pos;
    private Colour colour;
    private boolean taggedAsKing = false;
    private boolean taggedAsBeaten = false;

    public abstract String getBoardPosition();
    public abstract boolean isKing();
    protected abstract void makeKing();
    public abstract void unlock();
    public abstract boolean isInvalid();

    /**
     *
     * @return Позицию в записи хода
     */
    public StringBuilder getFullPosition() {
        return pos;
    }

    /**
     *
     * @return Позицию в записи хода типа String
     */
    public String getStringFullPosition() {
        return pos.toString();
    }

    /**
     * Делает позицию pos равной newPos
     * @param newPos Новая позиция
     */
    protected void setPosition(String newPos) {
        pos = new StringBuilder(newPos);
    }

    /**
     * Если фигура в данный момент достигла последней для нее вертикали, помечает фигуру дамкой
     * (но не делает ее таковой). В дальнейшем обращение к этой фигуре как к дамке, за исключением того,
     * что позиция фигуры обозначается, как будто фиугура не дамка
     */
    public void tagKingIfCorrectPosition() {
        if (!isKing()
                && (pos.charAt(1) == '8' && getColour() == Colour.WHITE
                || pos.charAt(1) == '1' && getColour() == Colour.BLACK)) {
            tagKing();
        }
    }

    /**
     * Если фигура в данный момент достигла последней для нее вертикали, делает фигуру дамкой
     */
    public void makeKingIfCorrectPosition() {
        tagKingIfCorrectPosition();
        makeKingIfTaggedEarlier();
    }

    private void tagKing() {
        taggedAsKing = true;
    }

    /**
     * Проверяет, была ли когда-либо помечена фигура как дамка
     * @return Была ли когда-либо помечена фигура как дамка
     */
    public boolean isTaggedAsKing() {
        return taggedAsKing;
    }

    /**
     * Если ранее фигура была помечена дамкой, делает ее полноправной дамкой
     */
    public void makeKingIfTaggedEarlier()  {
        if (isTaggedAsKing()) {
            makeKing();
        }
    }

    /**
     * Помечает, что данную фигуру побили на этом ходу.
     */
    public void tagBeaten() {
        taggedAsBeaten = true;
    }

    /**
     * Делает фигуру снова непобитой.
     */
    protected void tagUnbeaten() {
        taggedAsBeaten = false;
    }

    /**
     * Проверяет, били ли данную фигуру на этом ходу.
     * @return taggedAsBeaten -- били ли данную фигуру на этом ходу.
     */
    public boolean wasBeaten() {
        return taggedAsBeaten;
    }

    /**
     * Возвращает цвет фигуры.
     * @return Цвет фигуры.
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Делает цвет фигуры равным newColour.
     * @param newColour новый цвет фигуры.
     */
    protected void setColour(Colour newColour) {
        this.colour = newColour;
    }

    /**
     * Смещает позицию по горизонтали и вертикали.
     * @param pos Позиция, которую надо сместить.
     * @param biasX Смещение по горизонтали.
     * @param biasY Смещение по вертикали.
     */
    public static void displacePosition(StringBuilder pos, int biasX, int biasY) {
        char letter = (char) (pos.charAt(0) + biasX);
        char digit = (char) (pos.charAt(1) + biasY);
        pos.setLength(0);
        pos.append(new String(new char[]{letter, digit}));
    }
}
