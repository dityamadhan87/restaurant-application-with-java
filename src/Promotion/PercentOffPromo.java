package Promotion;

import Order.Order;

public class PercentOffPromo extends Promotion {

    public PercentOffPromo(String promoCode, String startDate, String endDate, String percentDiscount,
            String maxDiscount,
            String minimumPurchase) {
        super(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);
        this.promoType = "DISCOUNT";
    }

    public PercentOffPromo(String promoCode) {
        super(promoCode);
    }

    @Override
    public double totalDiscount(Order x) {
        double discount = (Double.parseDouble(getPercentDiscount()) / 100) * x.getSubTotalFoodCost();
        if (discount > Double.parseDouble(getMaxDiscount())) {
            return Double.parseDouble(getMaxDiscount());
        }
        return discount;
    }
}