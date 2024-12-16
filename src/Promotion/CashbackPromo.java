package Promotion;

import Order.Order;

public class CashbackPromo extends Promotion {

    public CashbackPromo(String promoCode, String startDate, String endDate, String percentDiscount, String maxDiscount,
            String minimumPurchase) {
        super(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);
        this.promoType = "CASHBACK";
    }

    public CashbackPromo(String promoCode) {
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