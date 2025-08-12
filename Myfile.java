// src/test/java/support/TestData.java
package support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public final class TestData {
  private static final Path FILE = Paths.get("target/test-context.json");
  private static final ObjectMapper M = new ObjectMapper();

  private TestData() {}

  public static synchronized void put(String key, String value) {
    Map<String,String> data = load();
    data.put(key, value);
    try {
      Files.createDirectories(FILE.getParent());
      // atomic write
      Path tmp = Files.createTempFile(FILE.getParent(), "ctx", ".json");
      M.writeValue(tmp.toFile(), data);
      Files.move(tmp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (Exception e) { throw new RuntimeException("Failed to save test data", e); }
  }

  public static synchronized String get(String key) {
    return load().get(key);
  }

  public static synchronized void clear() {
    try { Files.deleteIfExists(FILE); } catch (Exception ignored) {}
  }

  private static Map<String,String> load() {
    try {
      if (!Files.exists(FILE)) return new HashMap<>();
      return M.readValue(FILE.toFile(), new TypeReference<Map<String,String>>() {});
    } catch (Exception e) { throw new RuntimeException("Failed to load test data", e); }
  }
}


public class Hooks {
  @Before("@reset-context")
  public void reset() { support.TestData.clear(); }
}

  support.TestData.put("otp", otp);
  support.TestData.put("userId", currentUserId);
