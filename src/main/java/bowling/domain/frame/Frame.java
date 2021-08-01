package bowling.domain.frame;

import bowling.domain.pin.DownedPins;
import bowling.domain.state.State;

public abstract class Frame {
    protected State state;

    protected Frame(State state) {
        this.state = state;
    }

    public void downPins(DownedPins downedPins) {
        this.state = state.downPins(downedPins);
    }

}
