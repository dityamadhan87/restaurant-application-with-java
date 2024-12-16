package CustomerPackage;

public class Guest extends Customer{
    public Guest(String guestId){
        super(guestId);
    }
    public Guest(String guestId, String firstName, String lastName, String openingBalance){
        super(guestId, firstName, lastName, openingBalance);
        this.customerType = "GUEST";
        this.memberDate = "-";
    }

    public Guest(){
        super();
    }
    
    @Override
    public long longTimeMember() {
        throw new UnsupportedOperationException("Guest not becomes member");
    }
}