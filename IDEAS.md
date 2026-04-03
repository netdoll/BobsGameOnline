# IDEAS: bobsgameonlinejava Improvements

## 1. Networking Modernization
- **WebSocket Native Proxy:** While Netty currently powers the raw TCP server, building a robust, native WebSocket handler into the Java server would allow `bobsgameweb` to connect directly without needing the Node.js Socket.io middleman.
- **KryoNet / Protobuf:** Migrate away from `GZip/Base64 GSON/JSON` to a true binary protocol (like Protobuf or FlatBuffers) to reduce GC pressure and bandwidth during 8-player frantic VS matches.

## 2. Server Architecture
- **Virtual Threads (Loom):** Upgrade all concurrent Room and Session managers to use Java 21's Virtual Threads instead of heavy platform thread pools, massive scalability boost for the `GameServerTCP`.
- **Database Abstraction:** Introduce Hibernate/JPA or JOOQ instead of raw JDBC SQL strings to make schema migrations (like tracking Elo rating history) safer.

## 3. Map Editor
- **Tiled Import/Export:** The custom `EditorMain.java` is powerful but isolated. Adding a `.tmx` parser would let the project utilize industry-standard editors like Tiled natively.
- **Aseprite Integration:** Build a deeper watcher that hot-reloads `.aseprite` files straight into `SpriteSheet` objects during development using `aseprite-file` library.

## 4. Portability / Language
- **GraalVM Native Image:** Compile the headless `GameServerTCP` and `IndexClientTCP` to native binaries using GraalVM. This would eliminate JVM warmup and drastically lower memory consumption on cloud hosts.
- **Kotlin Migration:** Slowly introduce Kotlin for new server endpoints. Data classes and coroutines would clean up the massive boilerplate in packet parsing.
