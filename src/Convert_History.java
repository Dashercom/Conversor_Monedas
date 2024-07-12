import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Convert_History {
    private List<Convert_Record> history;

    public Convert_History() {
        history=new ArrayList<>();
    }

    public void addHistory(double amount, String fromcurrency, String tocurrency,
                           double rate, double convertedAmount) {
        history.add(new Convert_Record(amount, fromcurrency, tocurrency, rate, convertedAmount,LocalDateTime.now()));
    }

    public void showHistory() {
        if(history.isEmpty()) {
            System.out.println("No existe historial de conversiones");
        }else{
            for(Convert_Record record:history){
                System.out.println(record);
            }
        }
    }
}
