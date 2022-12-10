package cn.edu.wku.Locks.Utils;

public class ObjWithBoolean<T> {
    // The object
    private T obj;
    // The boolean
    private boolean boo;

    // Disable the non-arg constructor
    private ObjWithBoolean() {

    };

    public ObjWithBoolean(T obj, boolean boo) {
        this.obj = obj;
        this.boo = boo;
    }

    public T getObj() {
        return obj;
    }

    public boolean getBoolean() {
        return boo;
    }


}
