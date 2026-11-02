package Lab1.SheCanCode;

public class UndergraduateStudent extends Student {
    private int yearLevel; // 1-4

    public UndergraduateStudent(int id, String name, String email, String major, int yearLevel) {
        super(id, name, email, major);
        this.yearLevel = yearLevel;
    }

    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }

    /** Undergrad rule: every grade counts as earned (a D = 1.0 still earns points). */
    @Override protected double effectiveGradePoints(double gp) { return gp; }

    @Override public double calculateGPA() { return weightedAverage(); }
    @Override public String getLevel() { return "Undergraduate (year " + yearLevel + ")"; }
}
