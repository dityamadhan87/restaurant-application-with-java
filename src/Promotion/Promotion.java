package Promotion;

import Order.Order;
import Pelanggan.*;

public abstract class Promotion implements Applicable{
    String tipePromo;
    private String kodePromo;
    private String startDate;
    private String endDate;
    private String persenPotongan;
    private String maksPotongan;
    private String minPembelian;

    public Promotion(String kodePromo, String startDate, String endDate, String persenPotongan, String maksPotongan,
            String minPembelian) {
        this.tipePromo = "-";
        this.kodePromo = kodePromo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.persenPotongan = persenPotongan;
        this.maksPotongan = maksPotongan;
        this.minPembelian = minPembelian;
    }

    public Promotion(String kodePromo) {
        this.kodePromo = kodePromo;
    }

    public String getTipePromo() {
        return tipePromo;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPersenPotongan() {
        return persenPotongan;
    }

    public String getMaksPotongan() {
        return maksPotongan;
    }

    public String getMinPembelian() {
        return minPembelian;
    }

    @Override
    public boolean isCustomerEligible(Pelanggan x) {
        if (x instanceof Member)
            if (x.lamaMenjadiMember() > 30)
                return true;
        return false;
    }

    @Override
    public boolean isMinimumPriceEligible(Order x) {
        return x.getSubTotalBiayaMakanan() >= Integer.parseInt(minPembelian);
    }

    @Override
    public boolean isShippingFeeEligible(Order x) {
        return x.getOngkosKirim() >= 15000;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kodePromo == null) ? 0 : kodePromo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Promotion))
            return false;
        Promotion other = (Promotion) obj;
        return kodePromo != null && kodePromo.equals(other.kodePromo);
    }
}