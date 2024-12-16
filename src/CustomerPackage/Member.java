package CustomerPackage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Member extends Customer{
    public Member(String memberId){
        super(memberId);
    }
    public Member(String memberId, String firstName, String lastName, String memberDate, String openingBalance){
        super(memberId, firstName, lastName, openingBalance);
        this.customerType = "MEMBER";
        this.memberDate = memberDate;
    }

    public Member(){
        super();
    }

    @Override
    public long longTimeMember() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate memberDate = LocalDate.parse(this.memberDate, formatter);

        long totalDays = ChronoUnit.DAYS.between(memberDate, LocalDate.now());
        return totalDays;
    }
}