package Order;
public enum OrderStatus {
    UNPAID("Unpaid"),
    SUCCESSFUL("Successful ordering!"),
    PAID("Already paid");

    private String statusDescription;
    
    OrderStatus(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}