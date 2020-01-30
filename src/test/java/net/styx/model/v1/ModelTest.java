package net.styx.model.v1;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    void testPropertyTypes() {
        NewOrderSingle msg = new NewOrderSingle();
        assertIterableEquals(Collections.emptyList(), msg.getPreAllocGrp());
        PreAllocGrp p = new PreAllocGrp();
        msg.getPreAllocGrp().add(p);
        assertSame(p, msg.getPreAllocGrp().iterator().next());
    }
}
