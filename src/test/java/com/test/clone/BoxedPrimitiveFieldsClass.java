package com.test.clone;

import org.junit.Ignore;

@Ignore
public class BoxedPrimitiveFieldsClass implements Comparable {
    Integer intField;
    Double doubleField;

    BoxedPrimitiveFieldsClass(Integer intField, Double doubleField) {
        this.intField = intField;
        this.doubleField = doubleField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoxedPrimitiveFieldsClass that = (BoxedPrimitiveFieldsClass) o;

        if (intField != null ? !intField.equals(that.intField) : that.intField != null) return false;
        return doubleField != null ? doubleField.equals(that.doubleField) : that.doubleField == null;

    }

    @Override
    public int hashCode() {
        int result = intField != null ? intField.hashCode() : 0;
        result = 31 * result + (doubleField != null ? doubleField.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (!(o instanceof BoxedPrimitiveFieldsClass)) {
            return 1;
        }
        return intField - ((BoxedPrimitiveFieldsClass) o).intField;
    }
}