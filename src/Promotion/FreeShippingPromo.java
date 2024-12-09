package Promotion;

import java.util.Set;

import Order.Order;

public class FreeShippingPromo extends Promotion {

    public FreeShippingPromo(String kodePromo, String startDate, String endDate, String persenPotongan,
            String maksPotongan,
            String minPembelian) {
        super(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);
        this.tipePromo = "DELIVERY";
    }

    public FreeShippingPromo(String kodePromo) {
        super(kodePromo);
    }

    @Override
    public double totalDiscount(Set<Order> x) {
        return 0;
    }

    @Override
    public double totalCashback(Set<Order> x) {
        return 0;
    }

    @Override
    public double totalPotonganOngkosKirim(Set<Order> x) {
        int total = 0;
        for (Order order : x) {
            total += order.getSubTotalBiayaMakanan();
        }
        double discount = (Double.parseDouble(getPersenPotongan()) / 100) * total;
        if (discount > Double.parseDouble(getMaksPotongan())) {
            return Double.parseDouble(getMaksPotongan());
        }
        return discount;
    }
}