package Pelanggan;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Member extends Pelanggan{
    public Member(String idMember){
        super(idMember);
    }
    public Member(String idMember, String firstName, String lastName, String tanggalMenjadiMember, String saldoAwal){
        super(idMember, firstName, lastName, saldoAwal);
        this.tipePelanggan = "MEMBER";
        this.tanggalMenjadiMember = tanggalMenjadiMember;
    }

    public Member(){
        super();
    }

    @Override
    public Period lamaMenjadiMember() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate tanggalMenjadiMember = LocalDate.parse(this.tanggalMenjadiMember, formatter);

        Period period = Period.between(tanggalMenjadiMember, LocalDate.now());
        return period;
    }
}