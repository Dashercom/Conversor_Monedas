import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Convert_Record {
    private double amount;
    private String fromcurrency;
    private String tocurrency;
    private double rate;
    private double convertedAmount;
    private LocalDateTime timeformat;

    public Convert_Record(double amount, String fromcurrency, String tocurrency,
                          double rate, double convertedAmount, LocalDateTime timeformat)
    {
        this.amount=amount;
        this.fromcurrency=fromcurrency;
        this.tocurrency=tocurrency;
        this.rate=rate;
        this.convertedAmount=convertedAmount;
        this.timeformat=timeformat;
    }

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = timeformat.format(format);
        return String.format("(%s): $ %f %s a %s a tasa %f = %f",formatDateTime,amount,fromcurrency,tocurrency,rate,convertedAmount);
    }
}
