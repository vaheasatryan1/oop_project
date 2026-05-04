package core;

public class Player {
    private Position position;

    public Player(Position startPosition) {
        this.position = startPosition;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}