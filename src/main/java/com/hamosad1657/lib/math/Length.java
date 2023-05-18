package com.hamosad1657.lib.math;

public class Length {
    private double meters;

    /**
     * Constructs a new Length of zero.
     */
    public Length() {
        this.meters = 0.0;
    }
    /**
     * @param length
     * @param lengthUnit
     */
    public Length(double length, HaUnits.Length lengthUnit) {
        switch (lengthUnit) {
            case kMeters:
                this.meters = length;
            case kCM:
                this.meters = length / 100.0;
            case kMM:
                this.meters = length / 1000.0;
            case kInches:
                this.meters = HaUnitConvertor.inchesToMeters(length);
            case kFt:
                this.meters = HaUnitConvertor.ftToMeters(length);
        }
    }
    public static Length fromMeters(double meters) {
        return new Length(meters);
    }
    public static Length fromCM(double CM) {
        return new Length(CM, HaUnits.Length.kCM);
    }
    public static Length fromMM(double MM) {
        return new Length(MM, HaUnits.Length.kMM);
    }
    public static Length fromInches(double inches) {
        return new Length(inches, HaUnits.Length.kInches);
    }
    public static Length fromFt(double ft) {
        return new Length(ft, HaUnits.Length.kFt);
    }

    public double getLength(HaUnits.Length lengthUnit) {
        switch (lengthUnit) {
            case kMeters:
                return this.meters;
            case kCM:
                return this.meters * 100.0;
            case kMM:
                return this.meters * 1000.0;
            case kInches:
                return HaUnitConvertor.metersToInches(this.meters);
            case kFt:
                return HaUnitConvertor.metersToFt(this.meters);
            default:
                return 0;
        }
    }
    public double getM() {
        return this.meters;
    }
    public double getCM() {
        return this.getLength(HaUnits.Length.kCM);
    }
    public double getMM() {
        return this.getLength(HaUnits.Length.kMM);
    }
    public double getInches() {
        return this.getLength(HaUnits.Length.kInches);
    }
    public double getFt() {
        return this.getLength(HaUnits.Length.kFt);
    }

    private Length(double meters) {
        this.meters = meters;
    }

	@Override
	public String toString() {
		return "Length in meters: " + Double.toString(this.meters);
	}
}