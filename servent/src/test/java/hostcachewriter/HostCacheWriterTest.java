//package hostcachewriter;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import org.cxxii.json.HostsJson;
//import org.cxxii.network.Network;
//import org.cxxii.share.FileServer;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class HostCacheWriterTest {
//
//    private ObjectMapper objectMapper;
//    private HostsJson testHost;
//
//    @TempDir
//    Path tempDir;  // JUnit 5 will inject a temporary directory for file operations
//
//    @BeforeEach
//    public void setUp() throws UnknownHostException {
//        objectMapper = new ObjectMapper();
//
//        // Create a test HostsJson object
//        testHost = new HostsJson();
//        testHost.setIp("192.168.1.100");  // Example IP
//    }
//
//    @Test
//    public void testAppendPongToHostCache_AddNewHost() throws IOException, SocketException {
//        Path hostCachePath = tempDir.resolve("host_cache.json");
//
//        // Create a mock static method call for Network.getLocalIpString()
//        try (MockedStatic<Network> mockedNetwork = Mockito.mockStatic(Network.class)) {
//            mockedNetwork.when(Network::getLocalIpString).thenReturn("192.168.1.1");  // Simulate local IP
//
//            // Create a mocked HostsJson set
//            Set<HostsJson> mockHostsSet = new HashSet<>();
//
//            // Write an empty set to simulate an existing but empty host cache file
//            objectMapper.writeValue(hostCachePath.toFile(), mockHostsSet);
//
//            // Create a mocked static method for getHostCachePath()
//            try (MockedStatic<FileServer> mockedFileServer = Mockito.mockStatic(FileServer.class)) {
//                mockedFileServer.when(FileServer::getHostCachePath).thenReturn(hostCachePath);
//
//                // Call the method to test
//                FileServer.appendPongToHostCache(testHost);
//
//                // Read the updated file to verify that the new host was added
//                Set<HostsJson> updatedHostsSet = objectMapper.readValue(hostCachePath.toFile(), new TypeReference<Set<HostsJson>>() {});
//                assertTrue(updatedHostsSet.contains(testHost));
//
//                // Ensure that the set size is 1 since it added the testHost
//                assertTrue(updatedHostsSet.size() == 1);
//            }
//        }
//    }
//
//    @Test
//    public void testAppendPongToHostCache_HostAlreadyExists() throws IOException, SocketException {
//        Path hostCachePath = tempDir.resolve("host_cache.json");
//
//        // Create a mock static method call for Network.getLocalIpString()
//        try (MockedStatic<Network> mockedNetwork = Mockito.mockStatic(Network.class)) {
//            mockedNetwork.when(Network::getLocalIpString).thenReturn("192.168.1.1");  // Simulate local IP
//
//            // Create a mocked HostsJson set containing the testHost already
//            Set<HostsJson> mockHostsSet = new HashSet<>();
//            mockHostsSet.add(testHost);
//
//            // Write the set containing the host to simulate an existing host cache file
//            objectMapper.writeValue(hostCachePath.toFile(), mockHostsSet);
//
//            // Create a mocked static method for getHostCachePath()
//            try (MockedStatic<FileServer> mockedFileServer = Mockito.mockStatic(FileServer.class)) {
//                mockedFileServer.when(FileServer::getHostCachePath).thenReturn(hostCachePath);
//
//                // Call the method to test
//                FileServer.appendPongToHostCache(testHost);
//
//                // Read the updated file to verify that no new host was added
//                Set<HostsJson> updatedHostsSet = objectMapper.readValue(hostCachePath.toFile(), new TypeReference<Set<HostsJson>>() {});
//
//                // Ensure that the host set still contains only 1 entry
//                assertTrue(updatedHostsSet.size() == 1);
//                assertTrue(updatedHostsSet.contains(testHost));
//            }
//        }
//    }
//
//    @Test
//    public void testAppendPongToHostCache_LocalIpMatch() throws IOException, SocketException, UnknownHostException {
//        // Simulate when the host IP matches the local IP, in which case the method shouldn't modify the cache
//        try (MockedStatic<Network> mockedNetwork = Mockito.mockStatic(Network.class)) {
//            mockedNetwork.when(Network::getLocalIpString).thenReturn("192.168.1.100");  // Simulate local IP equals host IP
//
//            // Call the method to test
//            FileServer.appendPongToHostCache(testHost);
//
//            // Ensure that no actions were taken on the file because of IP match
//            // We could assert logger warnings or other side effects here if needed
//            // For now, we assume no file operations take place
//        }
//    }
//}
