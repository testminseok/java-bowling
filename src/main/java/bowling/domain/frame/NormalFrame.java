package bowling.domain.frame;

import bowling.domain.pin.Pin;
import bowling.domain.pin.PinCountValidator;
import bowling.domain.pin.Pins;
import bowling.domain.score.Score;
import bowling.exception.IllegalNormalFrameException;

public final class NormalFrame extends Frame {

    public static final RoundNumber MAX_NORMAL_FRAME_ROUND_NUMBER = new RoundNumber(RoundNumber.MAX - 1);
    public static final int MAX_NORMAL_PIN_COUNT = 10;
    private static final int SECOND_PIN_EXIST_SIZE = 2;

    private NormalFrame(RoundNumber roundNumber, Pins pins) {
        super(roundNumber, pins);
    }

    @Override
    public Frame nextFrame() {
        if (nextFrame == null) {
            createNextFrame();
        }
        return nextFrame;
    }

    public static Frame of(RoundNumber roundNumber, Pins pins) {
        validateNormalRoundNumber(roundNumber);
        return new NormalFrame(roundNumber, pins);
    }

    private static void validateNormalRoundNumber(RoundNumber roundNumber) {
        if (RoundNumber.MAX_ROUND_NUMBER.equals(roundNumber)) {
            throw new IllegalNormalFrameException();
        }
    }

    public static Frame createFirstFrame() {
        return NormalFrame.of(RoundNumber.firstRoundNumber(), Pins.create());
    }

    @Override
    public void createNextFrame() {
        this.nextFrame = generateNextFrame();
    }

    private Frame generateNextFrame() {
        if (MAX_NORMAL_FRAME_ROUND_NUMBER.equals(roundNumber)) {
            return FinalFrame.from(Pins.create());
        }
        return NormalFrame.of(roundNumber.nextRoundNumber(), Pins.create());
    }

    @Override
    public void knockDownPin(Pin pin) {
        pins.validatePinCount(pin, PinCountValidator.NORMAL);
        pins.knockDownPin(pin);
    }

    @Override
    public boolean isEnded() {
        return pins.isEnded();
    }

    @Override
    public boolean isFinalFrame() {
        return false;
    }

    @Override
    public Score score() {
        if (!isEnded()) {
            return Score.notCalculable();
        }
        if (pins.frameStatus() == FrameStatus.STRIKE) {
            return nextFrame().addScore(Score.strike());
        }
        if (pins.frameStatus() == FrameStatus.SPARE) {
            return nextFrame().addScore(Score.spare());
        }
        return Score.normal(pins.totalPinCount());
    }

    @Override
    protected Score addScore(Score previousScore) {
        if (pins.isEmpty()) {
            return Score.notCalculable();
        }

        final Score addedScore = previousScore.add(Score.normal(pins.firstPinCount()));
        if (!addedScore.canCalculate()) {
            return previousStrikeScore(addedScore);
        }

        return addedScore;
    }

    private Score previousStrikeScore(Score addedScore) {
        if (pins.isStrike()) {
            return nextFrame().addScore(addedScore);
        }
        if (pins.size() < SECOND_PIN_EXIST_SIZE) {
            return Score.notCalculable();
        }
        return addedScore.add(Score.normal(pins.secondPinCount()));
    }
}