package helper;

import java.util.LinkedList;
import java.util.List;

public class Round {

    private List<Shift> shifts;

    public Round() {
        this.shifts = new LinkedList<>();
    }

    public void addShift(Shift shift){
        this.shifts.add(shift);
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public Shift getShift(Integer index){
        return this.shifts.get(index);
    }

    @Override
    public String toString() {
        return "Round{" +
                "shifts=" + shifts +
                '}';
    }
}
