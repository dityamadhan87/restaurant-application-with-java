package Order;

public class HistoryData {
    private int orderNumber;
    private String customerId;
    private int orderQuantity;
    private int subTotal;
    private String promoCode;
    private String orderDate;

    public HistoryData(int orderNumber, String customerId, int orderQuantity, int subTotal, String promoCode,
            String orderDate) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.orderQuantity = orderQuantity;
        this.subTotal = subTotal;
        this.promoCode = promoCode;
        this.orderDate = orderDate;
    }

    public int getOrderNumber() {
        return orderNumber;
    }
    public String getCustomerId() {
        return customerId;
    }
    public int getOrderQuantity() {
        return orderQuantity;
    }
    public int getSubTotal() {
        return subTotal;
    }
    public String getPromoCode() {
        return promoCode;
    }
    public String getOrderDate() {
        return orderDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + orderNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HistoryData other = (HistoryData) obj;
        if (orderNumber != other.orderNumber)
            return false;
        return true;
    }
}