package Pelanggan;

import java.time.Period;

public class Guest extends Pelanggan{
    public Guest(String idGuest){
        super(idGuest);
    }
    public Guest(String idGuest, String firstName, String lastName, String saldoAwal){
        super(idGuest, firstName, lastName, saldoAwal);
        this.tipePelanggan = "GUEST";
        this.tanggalMenjadiMember = "-";
    }

    public Guest(){
        super();
    }
    
    @Override
    public Period lamaMenjadiMember() {
        throw new UnsupportedOperationException("Guest tidak menjadi member");
    }
}