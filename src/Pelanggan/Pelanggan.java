package Pelanggan;

public class Pelanggan {
    private String idPelanggan;
    private String firstName;
    private String lastName;
    private String tanggalMenjadiMember;
    private int saldoAwal;

    public Pelanggan(String idPelanggan, String firstName, String lastName, String tanggalMenjadiMember,
            int saldoAwal) {
        this.idPelanggan = idPelanggan;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tanggalMenjadiMember = tanggalMenjadiMember;
        this.saldoAwal = saldoAwal;
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

    public int getSaldoAwal() {
        return saldoAwal;
    }
}