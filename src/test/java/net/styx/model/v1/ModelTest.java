package net.styx.model.v1;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    void testPropertyTypes() {
        Order msg = new Order();
        assertIterableEquals(Collections.emptyList(), msg.getStrategyParametersGrp());
        StrategyParametersGrp group = new StrategyParametersGrp();
        group.setStrategyParameterName("Frank");
        group.setStrategyParameterType(StrategyParameterType.PRICE);
        group.setStrategyParameterValue("87.9");
        msg.getStrategyParametersGrp().add(group);
        assertSame(group, msg.getStrategyParametersGrp().iterator().next());
    }
}
