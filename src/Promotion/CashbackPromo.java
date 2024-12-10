package Promotion;

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
    public double totalDiscount(Order x) {
        return 0;
    }

    @Override
    public double totalCashback(Order x) {
        double discount = (Double.parseDouble(getPersenPotongan()) / 100) * x.getSubTotalBiayaMakanan();
        if (discount > Double.parseDouble(getMaksPotongan())) {
            return Double.parseDouble(getMaksPotongan());
        }
        return discount;
    }

    @Override
    public double totalPotonganOngkosKirim(Order x) {
        return 0;
    }
}