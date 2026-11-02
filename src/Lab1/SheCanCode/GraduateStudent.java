package Lab1.SheCanCode;

public class GraduateStudent extends Student {
    private String thesisTopic;
    public static final double MIN_PASSING = 2.0; // C

    public GraduateStudent(int id, String name, String email, String major, String thesisTopic) {
        super(id, name, email, major);
        this.thesisTopic = thesisTopic;
    }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    /** Graduate rule: anything below a C is a fail and counts as 0.0. */
    @Override protected double effectiveGradePoints(double gp) { return gp < MIN_PASSING ? 0.0 : gp; }

    @Override public double calculateGPA() { return weightedAverage(); }
    @Override public String getLevel() { return "Graduate (thesis: " + thesisTopic + ")"; }
}