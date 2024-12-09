package Promotion;

import java.util.Set;

import Order.Order;

public class CashbackPromo extends Promotion {

    public CashbackPromo(String kodePromo, String startDate, String endDate, String persenPotongan, String maksPotongan,
            String minPembelian) {
        super(kodePromo, startDate, endDate, persenPotongan, maksPotongan, minPembelian);
        this.tipePromo = "CASHBACK";
    }

    public CashbackPromo(String kodePromo) {
        super(kodePromo);
    }

    @Override
    public double totalDiscount(Set<Order> x) {
        return 0;
    }

    @Override
    public double totalCashback(Set<Order> x) {
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

    @Override
    public double totalPotonganOngkosKirim(Set<Order> x) {
        return 0;
    }
}