package com.qsmaxmin.qsbase.common.viewbind;

final class ViewInfo {
    public int value;
    public int parentId;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewInfo viewInfo = (ViewInfo) o;
        return value == viewInfo.value && parentId == viewInfo.parentId;
    }

    @Override public int hashCode() {
        int result = value;
        result = 31 * result + parentId;
        return result;
    }
}
