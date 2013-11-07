package com.mm.tool.batchmatch;

public class NumberConverter {
	/**
	 * 64进制和10进制int的转换类
	 * 
	 */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(base64toDecimalInt("3z8rE"));
	}
	
	public static String decimalInttoBase64(int input) {
        String b64 = "+-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String my64mediaId = "";
        int r = 0;
        char c = 0;
        int spotsPlanId2 = input;
        if (spotsPlanId2 == 0) {
            my64mediaId = "+";
        } else {
            while (spotsPlanId2 > 0) {
                r = spotsPlanId2 % 64;
                c = b64.charAt(r);
                my64mediaId = c + my64mediaId;
                spotsPlanId2 = spotsPlanId2 / 64;
            }
        }
        return my64mediaId;
    }
	
	public static int base64toDecimalInt(String base64) {
        String b64 = "+-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        char indexChar = 0;
        int index = 0;
        int power = 0;
        int total = 0;

        if (base64 != null) {
            for (int i = 0; i < base64.length(); i++) {
                indexChar = base64.charAt(i);
                index = b64.indexOf(indexChar);
                power = base64.length() - i - 1;
                total += index * Math.pow(64, power);
            }
        }
        return total;
    }
}
