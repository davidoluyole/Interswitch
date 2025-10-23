import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InterswitchTransfer {
    private static final String TRANSFER_URL = "https://sandbox.interswitchng.com/api/v2/quickteller/payments/transfers";
    private static final String ACCESS_TOKEN = "your_access_token"; // Replace with token from getAccessToken()

    public static void initiateTransfer() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // Sample transfer request payload
        String requestBody = mapper.writeValueAsString(new Object() {
            public final String amount = "10000"; // Amount in kobo (e.g., 100.00 NGN = 10000 kobo)
            public final String currency = "NGN";
            public final String accountNumber = "1234567890";
            public final String bankCode = "011"; // Bank code (e.g., First Bank)
            public final String reference = "TXN" + System.currentTimeMillis(); // Unique transaction reference
            public final String narration = "Test transfer";
        });

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSFER_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Transfer Response: " + response.body());
            // Parse response for status (e.g., "00" for success)
            String status = mapper.readTree(response.body()).get("responseCode").asText();
            if ("00".equals(status)) {
                System.out.println("Transfer successful!");
            } else {
                System.out.println("Transfer failed: " + response.body());
            }
        } else {
            throw new RuntimeException("Transfer request failed: " + response.body());
        }
    }

    public static void main(String[] args) throws Exception {
        initiateTransfer();
    }
}