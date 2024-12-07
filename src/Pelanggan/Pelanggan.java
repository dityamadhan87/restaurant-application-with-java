package Pelanggan;

public class Pelanggan {
    private String idPelanggan;
    private String firstName;
    private String lastName;
    private String tanggalMenjadiMember;
    private String saldoAwal;

    public Pelanggan(String idPelanggan, String firstName, String lastName, String tanggalMenjadiMember,
            String saldoAwal) {
        this.idPelanggan = idPelanggan;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tanggalMenjadiMember = tanggalMenjadiMember;
        this.saldoAwal = saldoAwal;
    }

    public Pelanggan(String idPelanggan){
        this.idPelanggan = idPelanggan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getTanggalMenjadiMember() {
        return tanggalMenjadiMember;
    }

    public String getSaldoAwal() {
        return saldoAwal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idPelanggan == null) ? 0 : idPelanggan.hashCode());
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
        Pelanggan other = (Pelanggan) obj;
        if (idPelanggan == null) {
            if (other.idPelanggan != null)
                return false;
        } else if (!idPelanggan.equals(other.idPelanggan))
            return false;
        return true;
    }
}