import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Transfer {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Bank code is required")
    private String bankCode;

    @Positive(message = "Amount must be positive")
    private Long amount; // In kobo (e.g., 10000 for ₦100)

    @NotBlank(message = "Currency is required")
    private String currency = "NGN";

    @NotBlank(message = "Narration is required")
    private String narration;

    @NotBlank(message = "Reference is required")
    @JsonProperty("transactionRef") // Maps to Interswitch field name
    private String transactionRef;

    // Constructors, getters, setters
    public Transfer() {}

    public Transfer(String accountNumber, String bankCode, Long amount, String narration, String transactionRef) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.amount = amount;
        this.narration = narration;
        this.transactionRef = transactionRef;
    }

    // Getters and setters...
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}