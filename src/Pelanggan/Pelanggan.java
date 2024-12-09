package Pelanggan;

public abstract class Pelanggan {
    String tipePelanggan;
    private String idPelanggan;
    private String firstName;
    private String lastName;
    private String saldoAwal;
    String tanggalMenjadiMember;

    public Pelanggan(String idPelanggan, String firstName, String lastName,
            String saldoAwal) {
        this.tipePelanggan = "-";
        this.idPelanggan = idPelanggan;
        this.firstName = firstName;
        this.lastName = lastName;
        this.saldoAwal = saldoAwal;
        this.tanggalMenjadiMember = "-";
    }

    public Pelanggan(String idPelanggan){
        this.idPelanggan = idPelanggan;
    
    }
    public Pelanggan(){
    }

    public String getTipePelanggan() {
        return tipePelanggan;
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

    public String getSaldoAwal() {
        return saldoAwal;
    }

    public String getTanggalMenjadiMember() {
        return tanggalMenjadiMember;
    }

    public void setSaldoAwal(String saldoAwal) {
        this.saldoAwal = saldoAwal;
    }

    public abstract long lamaMenjadiMember();

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
        if (obj == null || !(obj instanceof Pelanggan))
            return false;
        Pelanggan other = (Pelanggan) obj;
        return idPelanggan != null && idPelanggan.equals(other.idPelanggan);
    }
}