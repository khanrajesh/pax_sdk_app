package com.matm.matmsdk.vriddhi;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//
// Usage:
//
// Bitmap image = BitmapFactory.decodeFile(filename); - load image from file or from resources or inputStream (decodeResource, decodeStream)
// PrintImage = new HPManPrintImage(image); - create HPManPrintImage
// PrintImage.PrepareImage(HPManPrintImage.dither.floyd_steinberg,128);
// - convert color image to b&w image one of available algorithm (floyd_steinberg, matrix_2x2, matrix_4x4, threshold), bright_value = min 0 max 255
// PrintJob.SendDataRaw(PrintImage.getPrintImageData()); - Return print data array ready send to printer

public class PrintRasterImage {

public enum dither {floyd_steinberg, matrix_2x2, matrix_4x4, threshold};
private Bitmap bm_source;
private Bitmap bm_print;
private int bm_width, bm_height;
private int[] pixels;

public PrintRasterImage(Bitmap _source) {
   bm_source = _source;
   //scale image to max print area width 600px and divided by 8
   int width = bm_source.getWidth();
   int height = bm_source.getHeight();
   if (width > 576) {
       int newWidth = 576;
       float scaleWidth = ((float) newWidth) / width;
       int newHeight = (int) Math.floor(height * scaleWidth); //same ratio as width
       bm_source = getResizedBitmap(bm_source, newWidth, newHeight);
   } else {
       //Check divided by 8
       if ((double) Math.floor(width / 8) != (double) width / 8) {
           int newWidth = (int) Math.floor(width / 8);
           float scaleWidth = ((float) newWidth) / width;
           int newHeight = (int) Math.floor(height * scaleWidth); //same ratio as width
           bm_source = getResizedBitmap(bm_source, newWidth, newHeight);
       }
   }
   bm_width = bm_source.getWidth();
   bm_height = bm_source.getHeight();
   pixels = new int[width * height];
}

public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
   int width = bm.getWidth();
   int height = bm.getHeight();
   float scaleWidth = ((float) newWidth) / width;
   float scaleHeight = ((float) newHeight) / height;
   Matrix matrix = new Matrix();
   matrix.postScale(scaleWidth, scaleHeight);
   Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
   bm.recycle();
   return resizedBitmap;
}

public Bitmap getPrintImage() {
   return bm_print;
}

public Bitmap getSourceImage() {
   return bm_source;
}

public byte[] getPrintImageData() {
   ByteArrayOutputStream out = new ByteArrayOutputStream();
   int Pixel;
   int nDelay = 0;

   byte wh=(byte) ((bm_width/8)/256);
   byte wl=(byte) ((bm_width/8)%256);
   byte hh=(byte) ((bm_height)/256);
   byte hl=(byte) ((bm_height)%256);

   try {
       out.write(new byte[]{0x1d, 0x76, 0x30, 0x00,wl, wh, hl, hh}); // ESC/POS print image init command
   } catch (IOException e) {
       e.printStackTrace();
   }
   for (int y = 0; y < bm_height; y++)
   {
       for (int xbyte = 0; xbyte < bm_width/8; xbyte++)
       {
           int PrintByte = 0;
           for (int bit = 0; bit < 8; bit++)
           {
               PrintByte = PrintByte << 1;
               Pixel = pixels[(xbyte * 8) + bit + y * bm_width];
               if (Pixel==0xff000000)
                   PrintByte=PrintByte | 1;
           }
           out.write((byte) PrintByte);
       }
       for(nDelay = 0; nDelay<20000; nDelay++)//adding a delay after sending each line
       {}

   }
   return out.toByteArray();
}

public int getWidth(){
   return bm_width;
}

public int getHeight(){
   return bm_height;
}

public void PrepareImage(dither DitherMode,int bright_value) {
   switch (DitherMode) {
       case floyd_steinberg:
           Dither_Floyd_Steinberg(bright_value);
           break;
       case matrix_2x2:
           Dither_Matrix_2x2(bright_value);
           break;
       case matrix_4x4:
           Dither_Matrix_4x4(bright_value);
           break;
       case threshold:
           Dither_Threshold(bright_value);
           break;
   }
}

public void Dither_Floyd_Steinberg(int bright_value) {
   int Pixel,l;
   int w=bm_width;
   int h=bm_height;
   bright_value=bright_value-128;
   if (bm_print!=null) bm_print.recycle(); // if all ready print image initialized recycle
   bm_print=bm_source.copy(bm_source.getConfig(),true); // copy new from scaled source
   bm_print.getPixels(pixels,0,bm_width,0,0,bm_width,bm_height); // get pixels to array
   w=w+1; h=h+1;
   int[] tab = new int[w*h];
   // 1 pass
   for (int y = 0; y < (h-1); y++) {
       for (int x = 0; x < (w-1); x++) {
           if ((x == w - 1) || (y == h - 1))
               tab[x + w * y] = 0;
           else {
               Pixel = pixels[x+y*bm_width];
               l = ((76 * Color.red(Pixel) + 151 * Color.blue(Pixel) + 29 * Color.green(Pixel)) / 256)+bright_value;
               tab[x + w * y] = l;
           }
       }
   }
   // 2 pass
   int offset,gc,g;
   for (int y = 0; y < (h-2); y++) {
       for (int x = 0; x < (w-2); x++) {
           offset = x + y * w;
           gc = tab[offset];
           if (gc < 128) g = 0;
           else g = 255;
           gc = gc - g;
           tab[offset] = g;
           tab[offset + 1] = tab[offset + 1] + gc * 7 / 16;
           tab[offset - 1 + w] = tab[offset - 1 + w] + gc * 3 / 16;
           tab[offset + w] = tab[offset + w] + gc * 5 / 16;
           tab[offset + 1 + w] = tab[offset + 1 + w] + gc / 16;
       }
   }
   // 3 pass
   for (int y = 0; y < (h-1); y++) {
       for (int x = 0; x < (w-1); x++) {
           if (tab[x + w * y] == 0) l = 0xff000000;
           else l = 0xffffffff;
           pixels[x+y*bm_width]=l;
       }
   }
   bm_print.setPixels(pixels, 0, bm_width, 0, 0, bm_width, bm_height);
}

public void Dither_Threshold(int bright_value) {
   int Pixel,l;
   bright_value=bright_value-128;
   if (bm_print!=null) bm_print.recycle(); // if all ready print image initialized recycle
   bm_print=bm_source.copy(bm_source.getConfig(),true); // copy new from scaled source
   bm_print.getPixels(pixels,0,bm_width,0,0,bm_width,bm_height); // get pixels to array
   for (int y = 0; y < bm_height; y++) {
       for (int x = 0; x < bm_width; x++) {
           Pixel = pixels[x+y*bm_width];
           l=((76*Color.red(Pixel)+150*Color.blue(Pixel)+30*Color.green(Pixel))/256)+bright_value;
           if (l<128) l=0xff000000; else l=0xffffffff;
           pixels[x+y*bm_width]=l;
       }
   }
   bm_print.setPixels(pixels, 0, bm_width, 0, 0, bm_width, bm_height);
}

public void Dither_Matrix_2x2(int bright_value) {
   int[] matrix = {32,160,222,96};
   int Pixel,l,m;
   bright_value=bright_value-128;
   if (bm_print!=null) bm_print.recycle(); // if all ready print image initialized recycle
   bm_print=bm_source.copy(bm_source.getConfig(), true); // copy new from scaled source
   bm_print.getPixels(pixels,0,bm_width,0,0,bm_width,bm_height); // get pixels to array
   for (int y = 0; y < bm_height; y++) {
       for (int x = 0; x < bm_width; x++) {
           Pixel = pixels[x+y*bm_width];
           l=((76*Color.red(Pixel)+150*Color.blue(Pixel)+30*Color.green(Pixel))/256)+bright_value;
           m=(x % 2)+(y % 2)*2;
           if (l<matrix[m]) l=0xff000000; else l=0xffffffff;
           pixels[x+y*bm_width]=l;
       }
   }
   bm_print.setPixels(pixels, 0, bm_width, 0, 0, bm_width, bm_height);
}

public void Dither_Matrix_4x4(int bright_value) {
   int[] matrix = {15,143,47,175,207,79,239,111,63,191,31,159,255,127,223,95};
   int Pixel,l,m;
   bright_value=bright_value-128;
   if (bm_print!=null) bm_print.recycle(); // if all ready print image initialized recycle
   bm_print=bm_source.copy(bm_source.getConfig(), true); // copy new from scaled source
   bm_print.getPixels(pixels,0,bm_width,0,0,bm_width,bm_height); // get pixels to array
   for (int y = 0; y < bm_height; y++) {
       for (int x = 0; x < bm_width; x++) {
           Pixel = pixels[x+y*bm_width];
           l=((76*Color.red(Pixel)+150*Color.blue(Pixel)+30*Color.green(Pixel))/256)+bright_value;
           m=(x % 4)+(y % 4)*4;
           if (l<matrix[m]) l=0xff000000; else l=0xffffffff;
           pixels[x+y*bm_width]=l;
       }
   }
   bm_print.setPixels(pixels, 0, bm_width, 0, 0, bm_width, bm_height);
}
}