package Order;

public class DataHistori {
    private int nomorPesanan;
    private String idPelanggan;
    private int jumlahPesanan;
    private int subTotal;
    private String kodePromo;
    private String tanggalPesanan;

    public DataHistori(int nomorPesanan, String idPelanggan, int jumlahPesanan, int subTotal, String kodePromo,
            String tanggalPesanan) {
        this.nomorPesanan = nomorPesanan;
        this.idPelanggan = idPelanggan;
        this.jumlahPesanan = jumlahPesanan;
        this.subTotal = subTotal;
        this.kodePromo = kodePromo;
        this.tanggalPesanan = tanggalPesanan;
    }

    public int getNomorPesanan() {
        return nomorPesanan;
    }
    public String getIdPelanggan() {
        return idPelanggan;
    }
    public int getJumlahPesanan() {
        return jumlahPesanan;
    }
    public int getSubTotal() {
        return subTotal;
    }
    public String getKodePromo() {
        return kodePromo;
    }
    public String getTanggalPesanan() {
        return tanggalPesanan;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nomorPesanan;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataHistori other = (DataHistori) obj;
        if (nomorPesanan != other.nomorPesanan)
            return false;
        return true;
    }
}