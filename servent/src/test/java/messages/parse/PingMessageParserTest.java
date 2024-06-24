//package messages.parse;
//
//import org.cxxii.messages.PingMessage;
//import org.cxxii.messages.PingMessageParser;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//
//public class PingMessageParserTest {
//
//    private Socket mockSocket;
//    private InputStream mockInputStream;
//    private ByteArrayOutputStream mockOutputStream;
//    InetSocketAddress mockSocketAddress;
//
//    @BeforeEach
//    public void setup() throws IOException {
//        // Mock the Socket and its InputStream and OutputStream
//        mockSocket = mock(Socket.class);
//        mockInputStream = new ByteArrayInputStream(createMockHeader());
//        mockOutputStream = new ByteArrayOutputStream();
//
//
//        InetSocketAddress addr = mock(InetSocketAddress.class);
//
//        // Setup the socket to return the mocked streams
//        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
//        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
//        when(mockSocket.getRemoteSocketAddress()).thenReturn(mockSocketAddress);
//    }
//
//    @Test
//    public void testParseValidHeader() throws IOException {
//        // Prepare test data
//        byte[] header = new byte[23];
//        byte[] id = new byte[15];
//        Arrays.fill(id, (byte) 1); // Fill id with some data
//        System.arraycopy(id, 0, header, 0, 15);
//
//        header[16] = 0x00; // TypeId for Ping_to_delete
//        header[17] = 0x07; // TTL
//        header[18] = 0x00; // Hops
//        int payloadLength = 10;
//        byte[] payloadLengthBytes = ByteBuffer.allocate(4).putInt(payloadLength).array();
//        System.arraycopy(payloadLengthBytes, 0, header, 19, 4);
//
//        byte[] payload = new byte[payloadLength];
//        Arrays.fill(payload, (byte) 2); // Fill payload with some data
//
//        // Create parser and parse message
//        PingMessageParser parser = new PingMessageParser();
//        PingMessage message = (PingMessage) parser.parse(header, payload, mockSocketAddress);
//
//        // Assert the parsed message
//        assertArrayEquals(id, message.getBytesMessageID());
//        assertEquals(0x00, message.getTypeId());
//        assertEquals(0x07, message.getTimeToLive());
//        assertEquals(0x00, message.getHops());
//        assertEquals(payloadLength, message.getPayloadLength());
////        assertArrayEquals(payload, message.getPayload());
//    }
//
//    @Test
//    public void testParseInvalidHeader() {
//        // Prepare test data with an incomplete header
//        byte[] header = new byte[22];
//        byte[] payload = new byte[0];
//        InetSocketAddress addr = mockSocketAddress; // Mock or real implementation
//
//        PingMessageParser parser = new PingMessageParser();
//
//        assertThrows(IOException.class, () -> {
//            parser.parse(header, payload, addr);
//        });
//    }
//
//    @Test
//    public void testParseWithNullHeader() {
//        byte[] payload = new byte[0];
//        InetSocketAddress addr = mockSocketAddress; // Mock or real implementation
//
//        PingMessageParser parser = new PingMessageParser();
//
//        assertThrows(NullPointerException.class, () -> {
//            parser.parse(null, payload, addr);
//        });
//    }
//
//    @Test
//    public void testParseWithNullPayload() throws IOException {
//        // Prepare test data
//        byte[] header = new byte[23];
//        byte[] id = new byte[15];
//        Arrays.fill(id, (byte)1); // Fill id with some data
//        System.arraycopy(id, 0, header, 0, 15);
//
//        header[16] = 0x00; // TypeId for Ping_to_delete
//        header[17] = 0x07; // TTL
//        header[18] = 0x00; // Hops
//        int payloadLength = 10;
//        byte[] payloadLengthBytes = ByteBuffer.allocate(4).putInt(payloadLength).array();
//        System.arraycopy(payloadLengthBytes, 0, header, 19, 4);
//
//        InetSocketAddress addr = mockSocketAddress; // Mock or real implementation
//
//        // Create parser and parse message
//        PingMessageParser parser = new PingMessageParser();
//        PingMessage message = (PingMessage) parser.parse(header, null, addr);
//
//        // Assert the parsed message
//        assertArrayEquals(id, message.getBytesMessageID());
//        assertEquals(0x00, message.getTypeId());
//        assertEquals(0x07, message.getTimeToLive());
//        assertEquals(0x00, message.getHops());
//        assertEquals(payloadLength, message.getPayloadLength());
////        assertNull(message.getPayload());
//    }
//
//    @Test
//    public void testParseWithEmptyPayload() throws IOException {
//        // Prepare test data
//        byte[] header = new byte[23];
//        byte[] id = new byte[15];
//        Arrays.fill(id, (byte)1); // Fill id with some data
//        System.arraycopy(id, 0, header, 0, 15);
//
//        header[16] = 0x00; // TypeId for Ping_to_delete
//        header[17] = 0x07; // TTL
//        header[18] = 0x00; // Hops
//        int payloadLength = 10;
//        byte[] payloadLengthBytes = ByteBuffer.allocate(4).putInt(payloadLength).array();
//        System.arraycopy(payloadLengthBytes, 0, header, 19, 4);
//
//        byte[] payload = new byte[0];
//
//        InetSocketAddress addr = mockSocketAddress; // Mock or real implementation
//
//        // Create parser and parse message
//        PingMessageParser parser = new PingMessageParser();
//        PingMessage message = (PingMessage) parser.parse(header, payload, addr);
//
//        // Assert the parsed message
//        assertArrayEquals(id, message.getBytesMessageID());
//        assertEquals(0x00, message.getTypeId());
//        assertEquals(0x07, message.getTimeToLive());
//        assertEquals(0x00, message.getHops());
//        assertEquals(payloadLength, message.getPayloadLength());
////        assertArrayEquals(payload, message.getPayload());
//    }
//
//    private byte[] createMockHeader() {
//        byte[] header = new byte[23];
//        byte[] id = new byte[15];
//        Arrays.fill(id, (byte) 1); // Fill id with some data
//        System.arraycopy(id, 0, header, 0, 15);
//
//        header[16] = 0x00; // TypeId for Ping_to_delete
//        header[17] = 0x07; // TTL
//        header[18] = 0x00; // Hops
//        int payloadLength = 10;
//        byte[] payloadLengthBytes = ByteBuffer.allocate(4).putInt(payloadLength).array();
//        System.arraycopy(payloadLengthBytes, 0, header, 19, 4);
//
//        return header;
//    }
//}
