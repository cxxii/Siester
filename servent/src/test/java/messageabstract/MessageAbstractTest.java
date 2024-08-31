package messageabstract;

import org.cxxii.messages.MessageAbstract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageAbstractTest {

    private MessageAbstract message;

    @BeforeEach
    public void setUp() {
        // Create a mock MessageAbstract object
        message = mock(MessageAbstract.class);
    }

    @Test
    public void testTtlAndHopsIncrementor() {
        // Set up initial conditions
        when(message.getTimeToLive()).thenReturn((byte) 5);  // Initial TTL
        when(message.getHops()).thenReturn((byte) 3);        // Initial Hops

        // Call the method to test
        ttlAndHopsIncrementor(message);

        // Verify that the TimeToLive was decremented by 1
        verify(message).setTimeToLive((byte) 4);  // TTL was 5, so it should be set to 4

        // Verify that the Hops was incremented by 1
        verify(message).setHops((byte) 4);  // Hops was 3, so it should be set to 4
    }

    // The method you want to test
    public static void ttlAndHopsIncrementor(MessageAbstract messageAbstract) {
        messageAbstract.setTimeToLive((byte) (messageAbstract.getTimeToLive() - 1));
        messageAbstract.setHops((byte) (messageAbstract.getHops() + 1));
    }
}
