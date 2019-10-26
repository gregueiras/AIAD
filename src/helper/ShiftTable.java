package helper;

import java.util.LinkedList;
import java.util.List;

public class ShiftTable {

    private List<Shift> shifts;

    public ShiftTable() {
        this.shifts = new LinkedList<>();
    }

    public void addShift(Shift shift){
        this.shifts.add(shift);
    }

    @Override
    public String toString() {
        return "ShiftTable{" +
                "shifts=" + shifts +
                '}';
    }
}
