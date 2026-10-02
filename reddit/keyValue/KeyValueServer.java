import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueServer {

    private final Map<String, byte[]> store = new ConcurrentHashMap<>();

    public void start(int port) throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);

            while (true) {
                Socket client = server.accept();

                // Simple concurrency model for this interview solution.
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    private void handleClient(Socket client) {
        try (Socket socket = client;
             InputStream input =
                     new BufferedInputStream(socket.getInputStream());
             OutputStream output =
                     new BufferedOutputStream(socket.getOutputStream())) {

            String command;

            System.out.println("I am ready for the command");
            while ((command = readCommandLine(input)) != null) {
                String[] parts = command.split(" ", -1);

                switch (parts[0]) {
                    case "set":
                        handleSet(parts, input, output);
                        break;

                    case "get":
                        handleGet(parts, output);
                        break;

                    default:
                        throw new IOException("Unknown command");
                }

                output.flush();
            }
        } catch (IOException e) {
            // Close malformed or disconnected connections.
            System.err.println("Connection closed: " + e.getMessage());
        }
    }

    private void handleSet(
            String[] parts,
            InputStream input,
            OutputStream output
    ) throws IOException {

        if (parts.length != 3) {
            throw new IOException("Expected: set <key> <byte_count>");
        }

        String key = parts[1];
        validateKey(key);

        int byteCount = parseByteCount(parts[2]);

        byte[] value = input.readNBytes(byteCount);

        if (value.length != byteCount) {
            throw new EOFException("Connection closed during value");
        }

        if (input.read() != '\n') {
            throw new IOException("Missing newline after value");
        }

        // Only store after the entire request has been validated.
        store.put(key, value);

        writeAscii(output, "STORED\n");
    }

    private void handleGet(
            String[] parts,
            OutputStream output
    ) throws IOException {

        if (parts.length < 2) {
            throw new IOException("Expected: get <key> [more keys]");
        }

        // Validate all keys before writing any response.
        for (int i = 1; i < parts.length; i++) {
            validateKey(parts[i]);
        }

        // This also handles the multi-key get bonus.
        for (int i = 1; i < parts.length; i++) {
            String key = parts[i];
            byte[] value = store.get(key);

            if (value == null) {
                continue;
            }

            writeAscii(
                    output,
                    "VALUE " + key + " " + value.length + "\n"
            );

            output.write(value);
            output.write('\n');
        }

        writeAscii(output, "END\n");
    }

    private String readCommandLine(InputStream input) throws IOException {
        StringBuilder line = new StringBuilder();

        while (true) {
            int nextByte = input.read();

            if (nextByte == -1) {
                if (line.length() == 0) {
                    return null; // Clean disconnect between commands.
                }

                throw new EOFException("Incomplete command");
            }

            if (nextByte == '\n') {
                return line.toString();
            }

            // ascii only use 0-127
            if (nextByte > 127) {
                throw new IOException("Command must be ASCII");
            }

            line.append((char) nextByte);
        }
    }

    private void validateKey(String key) throws IOException {
        if (!key.matches("[A-Za-z0-9_-]+")) {
            throw new IOException("Invalid key");
        }
    }

    private int parseByteCount(String text) throws IOException {
        if (!text.matches("[0-9]+")) {
            throw new IOException("Invalid byte count");
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IOException("Byte count is too large", e);
        }
    }

    private void writeAscii(
            OutputStream output,
            String text
    ) throws IOException {
        output.write(text.getBytes(StandardCharsets.US_ASCII));
    }

    public static void main(String[] args) throws IOException {
        new KeyValueServer().start(11211);
    }
}
