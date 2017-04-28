package ziotom.timetostrike;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by valdemar on 28/04/2017.
 */

public class Giorno implements Comparable{
    private Integer numero;
    private Integer mese;
    private Integer anno;
    private Date date;

    public Giorno(String s){
        String[] listOfStrings = s.split("-");
        this.anno = Integer.getInteger(listOfStrings[0]);
        this.numero = Integer.getInteger(listOfStrings[1]);
        this.mese = Integer.getInteger(listOfStrings[2]);
    }

    public Date toDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = this.anno + "-" + this.mese + "-" + this.numero;
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Giorno p = (Giorno)o;
        if(this.anno>p.anno){
            return 1;
        } else if(this.anno<p.anno){
            return -1;
        }else{
            if(this.mese>p.mese){
                return 1;
            } else if(this.mese<p.mese){
                return -1;
            } else {
                if(this.numero>p.numero){
                    return 1;
                } else if(this.numero<p.numero){
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
}
