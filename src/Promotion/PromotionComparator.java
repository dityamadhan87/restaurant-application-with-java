package Promotion;

import java.util.Comparator;

import Order.Order;

public class PromotionComparator implements Comparator<Promotion>{

    private Order order;

    public PromotionComparator(Order order){
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public int compare(Promotion o1, Promotion o2) {
        double discount1 = o1.totalDiscount(order);
        double discount2 = o2.totalDiscount(order);

        return Double.compare(discount2, discount1);
    }
}
