package Order;
public enum StatusPesanan {
    UNPAID("Belum Dibayar"),
    SUCCESSFUL("Pemesanan berhasil"),
    PAID("Sudah Dibayar");

    private String deskripsiStatus;
    
    StatusPesanan(String deskripsiStatus) {
        this.deskripsiStatus = deskripsiStatus;
    }

    public String getDeskripsiStatus() {
        return deskripsiStatus;
    }
}