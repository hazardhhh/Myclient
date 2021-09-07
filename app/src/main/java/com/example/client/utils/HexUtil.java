package com.example.client.utils;

//@Name: com.ucstar.android.p84q.C056c
public final class HexUtil {
   //@Name: sf0a<char[]>
   private static final char[] HexDIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   //@Name: sf0b<int[]>
   private static final int[] sf0b = new int[]{60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};

   //@Name: sm0a(byte[])
   public static String bytes2Hex(byte[] data) {
      StringBuilder v2SBuilder = new StringBuilder();

      for(int i = 0; i < data.length; ++i) {
         v2SBuilder.append(byte2Hex(data[i]));
      }

      return v2SBuilder.toString();
   }

   //@Name: sm0a(java.lang.String)
   public static byte[] hex2Bytes(String pStr) {
      byte[] data = new byte[pStr.length() / 2];

      for(int i = 0; i < data.length; ++i) {
         int v3I = toDigit(pStr.charAt(2 * i));  	//一个byte被转换为两个HEX字符，故编码长度是原来的2倍
         int v4I = toDigit(pStr.charAt(2 * i + 1));
         if(v3I == -1 || v4I == -1) {
            return null;
         }

         data[i] = (byte)((v3I << 4) + v4I);
      }

      return data;
   }

   //@Name: sm0a(byte)
   private static String byte2Hex(byte ch) {
      long v1L = (long)ch;
      StringBuilder v5SBuilder = new StringBuilder(2);

      for(int i = 0; i < 2; ++i) {
         int index = (int)(v1L >> sf0b[i + 14] & 0xF); //实际效果是高位右移4位（除以16），低位不移位
         v5SBuilder.append(HexDIGITS[index]);
      }

      return v5SBuilder.toString();
   }

   //@Name: sm0a(char)
   private static int toDigit(char ch) {
	   if(ch >= '0' && ch <= '9')
		   return ch-'0';
	   if(ch >= 'a' && ch <= 'z')
		   return ch-'a' + 10;
	   if(ch >= 'A' && ch <= 'Z')
		   return ch-'A' +10;

	   return -1;
   }
}
