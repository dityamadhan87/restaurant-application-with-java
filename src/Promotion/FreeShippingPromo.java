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
        double discount = (Double.parseDouble(getPersenPotongan()) / 100) * x.getOngkosKirim();
        if (discount > Double.parseDouble(getMaksPotongan())) {
            return Double.parseDouble(getMaksPotongan());
        }
        return discount;
    }
}