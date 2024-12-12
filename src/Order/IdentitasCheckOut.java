package Order;

import java.time.LocalDate;

public class IdentitasCheckOut {
    private int nomorPesanan;
    private LocalDate tanggalPesanan;

    public IdentitasCheckOut(int nomorPesanan, LocalDate tanggalPesanan) {
        this.nomorPesanan = nomorPesanan;
        this.tanggalPesanan = tanggalPesanan;
    }

    public int getNomorPesanan() {
        return nomorPesanan;
    }

    public LocalDate getTanggalPesanan() {
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
        IdentitasCheckOut other = (IdentitasCheckOut) obj;
        if (nomorPesanan != other.nomorPesanan)
            return false;
        return true;
    }  
}