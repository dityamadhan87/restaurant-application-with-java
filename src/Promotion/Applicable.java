package Promotion;

import CustomerPackage.Customer;
import Order.Order;

public interface Applicable {
    boolean isCustomerEligible(Customer x);
    boolean isMinimumPriceEligible(Order x);
    boolean isShippingFeeEligible(Order x);
    double totalDiscount(Order x);
}