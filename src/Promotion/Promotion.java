package Promotion;

import CustomerPackage.*;
import Order.Order;

public abstract class Promotion implements Applicable{
    String promoType;
    private String promoCode;
    private String startDate;
    private String endDate;
    private String percentDiscount;
    private String maxDiscount;
    private String minimumPurchase;

    public Promotion(String promoCode, String startDate, String endDate, String percentDiscount, String maxDiscount,
            String minimumPurchase) {
        this.promoType = "-";
        this.promoCode = promoCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentDiscount = percentDiscount;
        this.maxDiscount = maxDiscount;
        this.minimumPurchase = minimumPurchase;
    }

    public Promotion(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoType() {
        return promoType;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPercentDiscount() {
        return percentDiscount;
    }

    public String getMaxDiscount() {
        return maxDiscount;
    }

    public String getMinimumPurchase() {
        return minimumPurchase;
    }

    @Override
    public boolean isCustomerEligible(Customer x) {
        if (x instanceof Member)
            if (x.longTimeMember() > 30)
                return true;
        return false;
    }

    @Override
    public boolean isMinimumPriceEligible(Order x) {
        return x.getSubTotalFoodCost() >= Integer.parseInt(minimumPurchase);
    }

    @Override
    public boolean isShippingFeeEligible(Order x) {
        return x.getShippingCost() >= 15000;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((promoCode == null) ? 0 : promoCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Promotion))
            return false;
        Promotion other = (Promotion) obj;
        return promoCode != null && promoCode.equals(other.promoCode);
    }
}