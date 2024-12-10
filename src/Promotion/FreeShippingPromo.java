package Promotion;

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
    public double totalDiscount(Order x) {
        return 0;
    }

    @Override
    public double totalCashback(Order x) {
        return 0;
    }

    @Override
    public double totalPotonganOngkosKirim(Order x) {
        double discount = (Double.parseDouble(getPersenPotongan()) / 100) * x.getSubTotalBiayaMakanan();
        if (discount > Double.parseDouble(getMaksPotongan())) {
            return Double.parseDouble(getMaksPotongan());
        }
        return discount;
    }
}