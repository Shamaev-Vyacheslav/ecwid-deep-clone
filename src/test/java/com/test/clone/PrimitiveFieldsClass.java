package com.test.clone;

public class PrimitiveFieldsClass {
    int intField;
    double doubleField;

    PrimitiveFieldsClass(int intField, double doubleField) {
        this.intField = intField;
        this.doubleField = doubleField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitiveFieldsClass that = (PrimitiveFieldsClass) o;

        if (intField != that.intField) return false;
        return Double.compare(that.doubleField, doubleField) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = intField;
        temp = Double.doubleToLongBits(doubleField);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
