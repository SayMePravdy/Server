package data;

public enum TicketType {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;


    public String toCsv() {
        if (this != null){
            switch (this) {
                case VIP:
                    return "VIP";
                case CHEAP:
                    return "CHEAP";
                case BUDGETARY:
                    return "BUDGETARY";
                case USUAL:
                    return "USUAL";
            }
        }
        return "";

    }
}
