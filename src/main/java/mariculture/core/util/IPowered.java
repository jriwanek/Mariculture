package mariculture.core.util;

public interface IPowered {
    String getPowerText();

    int getPowerScaled(int i);

    int getPowerPerTick();
    
    boolean isConsumer();
}
