package bowling.domain.state;

import bowling.domain.score.Score;

public interface State {
    State bowl(BowlingPin bowlingPin);
    boolean isDone();
    String toSymbol();
    Score score();
    int currentBowlingPin();
}