package com.cosylab.vdct.dbd;

public class TestDBD {
    public static void main(String[] args) {
        DBDResolver resolver = new DBDResolver();
        DBDData data = new DBDData();
        resolver.resolveDBD(data, "c://temp//iocShell.dbd");
        
        System.out.println(data);
    }
}
