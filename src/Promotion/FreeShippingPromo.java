package Promotion;

import Order.Order;

public class FreeShippingPromo extends Promotion {

    public FreeShippingPromo(String promoCode, String startDate, String endDate, String percentDiscount,
            String maxDiscount,
            String minimumPurchase) {
        super(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);
        this.promoType = "DELIVERY";
    }

    public FreeShippingPromo(String promoCode) {
        super(promoCode);
    }

    @Override
    public double totalDiscount(Order x) {
        double discount = (Double.parseDouble(getPercentDiscount()) / 100) * x.getShippingCost();
        if (discount > Double.parseDouble(getMaxDiscount())) {
            return Double.parseDouble(getMaxDiscount());
        }
        return discount;
    }
}