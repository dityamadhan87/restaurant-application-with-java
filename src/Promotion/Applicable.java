package Promotion;

import java.util.Set;

import Order.Order;
import Pelanggan.Pelanggan;

public interface Applicable {
    boolean isCustomerEligible(Pelanggan x);
    boolean isMinimumPriceEligible(Set<Order> x);
    boolean isShippingFeeEligible(Set<Order> x);
    double totalDiscount(Set<Order> x);
    double totalCashback(Set<Order> x);
    double totalPotonganOngkosKirim(Set<Order> x);
}