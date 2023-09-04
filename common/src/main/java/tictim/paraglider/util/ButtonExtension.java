package tictim.paraglider.util;

public interface ButtonExtension {
    default void setTooltip(Tooltip tooltip) {
        throw new IllegalStateException("what");
    }
    default Tooltip getTooltip() {
        throw new IllegalStateException("what");
    }
}
