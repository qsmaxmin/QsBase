package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help;

public final class IntegerVariable {

    private int mValue;

    public IntegerVariable(int value) {
        mValue = value;
    }
    public IntegerVariable() {
    }

    public final int getValue() {
        return mValue;
    }

    public final void setValue(int value) {
        mValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o instanceof IntegerVariable) {
            return mValue == ((IntegerVariable)o).getValue();
        }

        if(o instanceof Integer) {
            return mValue == (Integer) o;
        }

        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.valueOf(mValue);
    }
}
