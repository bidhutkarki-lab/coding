```
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectNode bootstrap_api(JsonNode input) {
        String customerId = input.get("userResponse")
                .get("body")
                .get("customerId")
                .asText();

        JsonNode defaultCard = input.get("paymentResponse").get("body");
        JsonNode address = input.get("addressResponse")
                .get("body")
                .get("address");

        ObjectNode result = MAPPER.createObjectNode();
        result.put("CustomerId", customerId);
        result.set("DefaultCard", defaultCard);
        result.set("Address", address);

        return result;
    }

    public static void main(String[] args) throws Exception {
        JsonNode input = MAPPER.readTree(System.in);
        System.out.println(MAPPER.writeValueAsString(bootstrap_api(input)));
    }
}

Uses Jackson (jackson-databind). The entire payment body is preserved, including any additional card fields. Status checks are omitted because the input guarantees 200.

For a real HTTP implementation, fetch the user first, then call payment and address services concurrently using the returned customerId.

*HTTP 500:* Treat it as an upstream failure. If UserService fails, stop because both remaining calls require its customer ID. Return an appropriate gateway error, typically 502.
*Timeouts:* Set connection and request timeouts, plus an overall bootstrap deadline. Return 504 when a required dependency times out.
*Exceptions:* Handle network failures, invalid JSON, and missing required fields explicitly. Return sanitized errors without exposing internal details.
*Retries:* Retry transient failures for safe, idempotent requests with bounded exponential backoff and jitter. Keep retries within the overall deadline; avoid retrying validation failures.
*Graceful degradation and partial responses:* If the product allows it, return 200 with available data, null for failed optional sections, and explicit error metadata. Distinguish unavailable data from a customer who simply has no card or address. If all sections are required, fail the request instead.
*Observability:* Use correlation IDs and distributed tracing. Track per-service latency, errors, timeouts, retries, and partial-response rates. Avoid logging card details or addresses.
