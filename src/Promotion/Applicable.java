package Promotion;

import Order.Order;
import Pelanggan.Pelanggan;

public interface Applicable {
    boolean isCustomerEligible(Pelanggan x);
    boolean isMinimumPriceEligible(Order x);
    boolean isShippingFeeEligible(Order x);
    double totalDiscount(Order x);
    double totalCashback(Order x);
    double totalPotonganOngkosKirim(Order x);
}