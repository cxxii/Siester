//package messages.parse;
//
//import org.cxxii.messages.QueryHitMessage;
//import org.cxxii.messages.QueryHitMessageParser;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class QueryHitMessageParserTest {
//
//    private QueryHitMessageParser parser;
//
//    @BeforeEach
//    void setUp() {
//        parser = new QueryHitMessageParser();
//    }
//
//    @Test
//    void testParseSingleFile() throws Exception {
//        byte[] header = {
//                -54, -14, -124, -117, -68, 73, 73, 38, -83, -23, 13, 77, 127, -110, -101, 66, // messageId
//                -127, // typeID
//                6, // timeToLive
//                1, // hops
//                0, 0, 0, 51 // payloadLength (51)
//        };
//
//        byte[] payload = {
//                1, // numberOfHits
//                24, -36, // portNum (6364)
//                1, 1, 1, 1, // ipAddress (1.1.1.1)
//                0, 0, 86, -50, // speed (22222)
//                0, 0, 0, 1, // fileIndex (1)
//                0, 0, 0, 0, 0, 0, 0, 0, // fileSize (0)
//                110, 101, 108, 108, 121, 46, 99, 111, 109, 0 // fileName ("nelly.com\0")
//        };
//
//        InetSocketAddress addr = Mockito.mock(InetSocketAddress.class);
//
//        QueryHitMessage result = parser.parse(header, payload, addr);
//
//        assertNotNull(result);
//        assertEquals(1, result.numberOfHits);
//
//        List<QueryHitMessage.Result> results = result.resultList;
//        assertEquals(1, results.size());
//
//        QueryHitMessage.Result fileResult = results.get(0);
//        assertEquals(1, fileResult.getFileIndex());
//        assertEquals(0, fileResult.getFileSize());
//        assertEquals("nelly.com", fileResult.getFileName());
//    }
//
//    @Test
//    void testParseMultipleFiles() throws Exception {
//        byte[] header = {
//                -54, -14, -124, -117, -68, 73, 73, 38, -83, -23, 13, 77, 127, -110, -101, 66, // messageId
//                -127, // typeID
//                6, // timeToLive
//                1, // hops
//                0, 0, 0, 51 // payloadLength (51)
//        };
//
//        byte[] payload = {
//                2, // numberOfHits
//                24, -36, // portNum (6364)
//                1, 1, 1, 1, // ipAddress (1.1.1.1)
//                0, 0, 86, -50, // speed (22222)
//                0, 0, 0, 1, // fileIndex (1)
//                0, 0, 0, 0, 0, 0, 0, 0, // fileSize (0)
//                110, 101, 108, 108, 121, 46, 99, 111, 109, 0, // fileName ("nelly.com\0")
//                0, 0, 0, 2, // fileIndex (2)
//                0, 0, 0, 0, 0, 0, 0, 0, // fileSize (0)
//                110, 101, 108, 108, 121, 46, 116, 120, 116, 0 // fileName ("nelly.txt\0")
//        };
//
//        InetSocketAddress addr = Mockito.mock(InetSocketAddress.class);
//
//        QueryHitMessage result = parser.parse(header, payload, addr);
//
//        assertNotNull(result);
//        assertEquals(2, result.numberOfHits);
//
//        List<QueryHitMessage.Result> results = result.resultList;
//        assertEquals(2, results.size());
//
//        QueryHitMessage.Result fileResult1 = results.get(0);
//        assertEquals(1, fileResult1.getFileIndex());
//        assertEquals(0, fileResult1.getFileSize());
//        assertEquals("nelly.com", fileResult1.getFileName());
//
//        QueryHitMessage.Result fileResult2 = results.get(1);
//        assertEquals(2, fileResult2.getFileIndex());
//        assertEquals(0, fileResult2.getFileSize());
//        assertEquals("nelly.txt", fileResult2.getFileName());
//    }
//
//    @Test
//    void testParseWithInvalidPayload() {
//        byte[] header = {
//                -54, -14, -124, -117, -68, 73, 73, 38, -83, -23, 13, 77, 127, -110, -101, 66, // messageId
//                -127, // typeID
//                6, // timeToLive
//                1, // hops
//                0, 0, 0, 51 // payloadLength (51)
//        };
//
//        byte[] payload = {1}; // Invalid payload (too short)
//
//        InetSocketAddress addr = Mockito.mock(InetSocketAddress.class);
//
//        assertThrows(IOException.class, () -> parser.parse(header, payload, addr));
//    }
//}
