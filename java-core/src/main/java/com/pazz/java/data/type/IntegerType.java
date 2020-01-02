package com.pazz.java.data.type;

/**
 * Integer 缓存池 -128~127
 */
public class IntegerType {

    public static void main(String[] args) {
        int a = 32;
        int b = 45;
        System.out.println("二进制a: " + Integer.toBinaryString(a));  //100000
        System.out.println("二进制b: " + Integer.toBinaryString(b));  //101101

        System.out.println(a & b);      //如果相对应位都是1，则结果为1，否则为0
        System.out.println(a | b);      //如果相对应位都是0，则结果为0，否则为1
        System.out.println(a ^ b);      //如果相对应位值相同，则结果为0，否则为1

        System.out.println(a >>> 3);   // (右移三位)  c除2^3
        System.out.println(a >> 1);    // (右移二位)  c除2^1
        System.out.println(a << 2);    // (左移二位)  c乘2^2
    }

}