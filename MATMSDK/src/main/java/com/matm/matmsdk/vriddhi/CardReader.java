package com.matm.matmsdk.vriddhi;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CardReader {
private static final int NAME = 1;
private static final int SWD_OF = 2;
private static final int DOB = 3;
private static final int DL_NUM = 4;
private static final int ISS_AUTH = 5;
private static final int DOI = 6;
private static final int VALID_TP = 7;
private static final int VALID_NTP = 8;
private static final int VEH_INFO_1 = 9;
private static final int REG_NUM = 13;
private static final int REG_NAME = 14;
private static final int REG_UPTO = 15;
private static boolean MSRReadTest = false;
private static boolean DLTest = false;
private static boolean RCTest = false;
private static InputStream dis = null;
private static OutputStream dos = null;
private IAemCardScanner scannerImplementer;
private BluetoothSocket bluetoothSocket;

public enum CARD_TRACK {TRACK1, TRACK2};

public CardReader(BluetoothSocket socket, IAemCardScanner readerImpl)
{
   bluetoothSocket = socket;
   scannerImplementer = readerImpl;

   if (dis != null)
       dis = null;
   if (dos != null)
       dos = null;

   try
   {
       dis = bluetoothSocket.getInputStream();
       dos = bluetoothSocket.getOutputStream();
       new InputThread().start();
   }
   catch (IOException e)
   {
   }
}
public CardReader(Socket socket, IAemCardScanner readerImpl) {

   socket = socket;
   scannerImplementer = readerImpl;

   if (dis != null)
       dis = null;
   if (dos != null)
       dos = null;

   try
   {
       dis = socket.getInputStream();
       dos = socket.getOutputStream();
       new InputThread().start();
   }
   catch (IOException e)
   {
   }
}

private class InputThread extends Thread
{
   public void run()
   {
       try
       {
           byte ch;
           byte[] buffer = new byte[1];

           while (true)
           {
               dis.read(buffer);
               ch = buffer[0];

               readPacketFromPrinter((char) ch);

//					if (MSRReadTest == true) 
//					{
//						if (MSRvalidChar((char) ch) == 1)
//							readPacketFromPrinter((char) ch);
//					} 
//					else if (DLTest == true) 
//					{
//						readPacketFromPrinter((char) ch);
//					} 
//					else if (RCTest == true) 
//					{
//						readPacketFromPrinter((char) ch);
//					}
//					else
//					{
//						readPacketFromPrinter((char) ch);
//					}
           }
       }
       catch (Exception e)
       {
       }
   }

}

static String buffer = "";

private void readPacketFromPrinter(char ch)
{
   try
   {
       buffer = buffer + ch;
       //if (buffer.endsWith("MSRREAD"))
       //{
           String dataTrack1 = "";
           String dataTrack2 = "";
           Log.e("Buffer",buffer);
           if(buffer.contains("%"))
           {
               dataTrack1 = readTrack(CARD_TRACK.TRACK1, buffer);
               scannerImplementer.onScanMSR(dataTrack1, CARD_TRACK.TRACK1);
               MSRReadTest = false;
               buffer = "";				}
           else if(buffer.contains(";"))
           {

               dataTrack2 = readTrack(CARD_TRACK.TRACK2, buffer);


               //scannerImplementer.onScanMSR(dataTrack2, CARD_TRACK.TRACK2);
           }

           /*MSRReadTest = false;
           buffer = "";*/
       /*}
       else if (buffer.endsWith("MSRTMOT"))
       {
           scannerImplementer.onScanMSR(buffer, CARD_TRACK.TRACK1);

           buffer = "";
           MSRReadTest = false;
       }
       else if (buffer.endsWith("ICMODON"))
       {
           buffer = "";
       }
       else if (buffer.endsWith("ICMODOF"))
       {
           if (DLTest == true)
           {
               scannerImplementer.onScanDLCard(buffer);
               DLTest = false;
           }
           else
           {
               scannerImplementer.onScanRCCard(buffer);
               RCTest = false;
           }
           buffer = "";
       }
       else if (buffer.endsWith("ICTMOT"))
       {
           buffer = "";
           DLTest = false;
           RCTest = false;
       }*/
       /*else if ((buffer.contains("NOPAPER"))|| (buffer.contains("LOWBATT"))|| (buffer.contains("PAPEROK"))|| (buffer.contains("HIGHTEMP"))|| (buffer.contains("$$$"))|| (buffer.contains("&&&"))|| (buffer.contains("PRINTEROK")))
       {
       scannerImplementer.onScanPacket(buffer);
       buffer = "";
       } */
       else if(buffer.startsWith("~")&&buffer.endsWith("^")){
               scannerImplementer.onScanPacket(buffer);
               buffer = "";
       }

       else if(buffer.endsWith("RFD|"))
       {
           buffer = "";
       }
       else if(buffer.endsWith("^") && buffer.length() == 9)
       {
           scannerImplementer.onScanRFD(buffer);
           buffer = "";
       }
   }
   catch (Exception e)
   {
   }
}

public RCCardData decodeRCData(String tempBuff)
{
   RCCardData rcCardData = new RCCardData();
   char[] tempCharBuff = tempBuff.toCharArray();

   rcCardData.REG_NUM = getDataFromDL(tempCharBuff, REG_NUM);
   rcCardData.REG_NAME = getDataFromDL(tempCharBuff, REG_NAME);
   rcCardData.REG_UPTO = getDataFromDL(tempCharBuff, REG_UPTO);

   return rcCardData;
}

public DLCardData decodeDLData(String tempBuff)
{
   DLCardData dlCardData = new DLCardData();
   char[] tempCharBuff = tempBuff.toCharArray();

   dlCardData.NAME = getDataFromDL(tempCharBuff, NAME);
   dlCardData.SWD_OF = getDataFromDL(tempCharBuff, SWD_OF);
   dlCardData.DOB = getDataFromDL(tempCharBuff, DOB);
   dlCardData.DL_NUM = getDataFromDL(tempCharBuff, DL_NUM);
   dlCardData.ISS_AUTH = getDataFromDL(tempCharBuff, ISS_AUTH);
   dlCardData.DOI = getDataFromDL(tempCharBuff, DOI);
   dlCardData.VALID_TP = "VALID_TP:" + getDataFromDL(tempCharBuff, VALID_TP);
   dlCardData.VALID_NTP = getDataFromDL(tempCharBuff, VALID_NTP);

   return dlCardData;
}

private String getDataFromDL(char[] src, int PacketType)
{
   String result = "";

   int i = 0;
   int len;

   String str = "";

   boolean found = false;

   switch (PacketType)
   {
   case NAME:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc1)
           {
               found = true;
               break;
           }
       }

       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc1)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       else
           result = "NA";
       break;

   case SWD_OF:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc2)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc2)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       break;

   case DOB:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc3)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc3)
       {
           i++;
           len = src[i];
           i++;
           str = getHexValue(src[i]) + "-" + getHexValue(src[i + 1]) + "-"
                   + getHexValue(src[i + 2]) + ""
                   + getHexValue(src[i + 3]);
           result = str;
       }
       break;

   case DL_NUM:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc4)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc4)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       break;

   case ISS_AUTH:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc5)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc5)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       break;

   case DOI:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xca)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xca)
       {
           i++;
           len = src[i];
           i++;
           str = getHexValue(src[i]) + "-" + getHexValue(src[i + 1]) + "-"
                   + getHexValue(src[i + 2]) + ""
                   + getHexValue(src[i + 3]);
           result = str;
       }
       break;

   case VALID_TP:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc6)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc6)
       {
           i++;
           len = src[i];
           i++;
           str = getHexValue(src[i]) + "-" + getHexValue(src[i + 1]) + "-"
                   + getHexValue(src[i + 2]) + ""
                   + getHexValue(src[i + 3]);
           result = str;
       }
       break;

   case VALID_NTP:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc7)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc7)
       {
           i++;
           len = src[i];
           i++;
           str = getHexValue(src[i]) + "-" + getHexValue(src[i + 1]) + "-"
                   + getHexValue(src[i + 2]) + ""
                   + getHexValue(src[i + 3]);
           result = str;
       }
       break;

   case VEH_INFO_1:
       i = 0;
       for (int j = 0; j < 4; j++)
       {
           for (; i < src.length; i++)
           {
               if ((0xFF & src[i]) == 0xc8)
               {
                   found = true;
                   break;
               }
           }
           if (found == false)
           {
               result = "NA";
               return result;
           }
           if ((0xFF & src[i]) == 0xc8)
           {
               i++;

               len = src[i];
               i++;
               len = 46;
               str = new String(src, i, len);
               i = i + len;
               str = str + " " + getHexValue(src[i]) + "-"
                       + getHexValue(src[i + 1]) + "-"
                       + getHexValue(src[i + 2]) + ""
                       + getHexValue(src[i + 3]);
               result = result + " " + str;
           }
       }
       break;

   case REG_NUM:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc0)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc0)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       break;

   case REG_NAME:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc1)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc1)
       {
           i++;
           len = src[i];
           str = new String(src, i + 1, len);
           result = str;
       }
       break;

   case REG_UPTO:
       for (i = 0; i < src.length; i++)
       {
           if ((0xFF & src[i]) == 0xc4)
           {
               found = true;
               break;
           }
       }
       if (found == false)
       {
           result = "NA";
           return result;
       }
       if ((0xFF & src[i]) == 0xc4)
       {
           i++;
           len = src[i];
           i++;
           str = getHexValue(src[i]) + "-" + getHexValue(src[i + 1]) + "-"
                   + getHexValue(src[i + 2]) + ""
                   + getHexValue(src[i + 3]);
           result = str;
       }
       break;
   }

   return result;
}

private String getHexValue(char ch)
{
   String str = "";
   str = Integer.toHexString(0xFF & ch);

   if (str.length() == 1)
       str = "0" + str;

   return str;
}

private String readTrack(CARD_TRACK track, String src)
{
   String result = "";
   String str = "";
   int index;

   switch (track)
   {
   case TRACK1:
       index = src.indexOf('%');
       result = src.substring(index, index + 76);
       break;

   case TRACK2:
       index = src.indexOf(';');
       result = src.substring(index, index + 37);
       break;
   }
   return result;
}

private int MSRvalidChar(char ch)
{
   switch (ch)
   {
   case 0x02:
       return 0;// start of text.
   case 0x03:
       return 0;// end of text.
   case 0x0D:
       return 0;// carriage return for non printable character.
   case 0x1C:
       return 0;// file seprator.
   }
   return 1;
}

/*	public void readMSR() throws IOException
{
   byte[] msrCmd = { 0x1C, 0x4D, 0x33 };
   dos.write(msrCmd);
   MSRReadTest = true;
}*/

public void readDL() throws IOException
{
   byte initCmd[] = { 0x1B, 0x4E };
   byte selectMainFile[] = { 0x7E, 0x42, 0x00, 0x08, 0x15, 0x00,
           (byte) 0xA4, 0x00, 0x00, 0x02, 0x40, 0x00, (byte) 0xB9, 0x7E };
   byte selectDF1[] = { 0x7E, 0x42, 0x00, 0x08, 0x15, 0x00, (byte) 0xA4,
           0x00, 0x00, 0x02, 0x40, 0x04, (byte) 0xBD, 0x7E };
   byte selectDF2[] = { 0x7E, 0x42, 0x00, 0x08, 0x15, 0x00, (byte) 0xA4,
           0x00, 0x00, 0x02, 0x40, 0x05, (byte) 0xBC, 0x7E };
   byte readDataDF1[] = { 0x7E, 0x42, 0x00, 0x06, 0x15, 0x00, (byte) 0xB0,
           0x00, 0x00, (byte) 0x90, 0x71, 0x7E };
   byte readDataDF2[] = { 0x7E, 0x42, 0x00, 0x06, 0x15, 0x00, (byte) 0xB0,
           0x00, 0x00, (byte) 0x90, 0x71, 0x7E };
   byte terminateICMode[] = { 0x7E, 0x04, 0x7E };

   DLTest = true;

   try
   {
       dos.write(initCmd);
       Thread.sleep(500);

       dos.write(selectMainFile);
       Thread.sleep(500);

       dos.write(selectDF1);
       Thread.sleep(500);

       dos.write(readDataDF1);
       Thread.sleep(500);

       dos.write(selectDF2);
       Thread.sleep(500);

       dos.write(readDataDF2);
       Thread.sleep(500);

       dos.write(terminateICMode);
   }
   catch (InterruptedException e)
   {

   }
}

public void readRC()
{
   byte initCmd[] = { 0x1B, 0x4E };
   byte selectMainFile[] = { 0x7E, 0x42, 0x00, 0x08, 0x15, 0x00,
           (byte) 0xA4, 0x00, 0x00, 0x02, 0x50, 0x00, (byte) 0xA9, 0x7E };
   byte selectDF1[] = { 0x7E, 0x42, 0x00, 0x08, 0x15, 0x00, (byte) 0xA4,
           0x00, 0x00, 0x02, 0x50, 0x03, (byte) 0xAA, 0x7E };
   byte readDataDF1[] = { 0x7E, 0x42, 0x00, 0x06, 0x15, 0x00, (byte) 0xB0,
           0x00, 0x00, (byte) 0x90, 0x71, 0x7E };
   byte terminateICMode[] = { 0x7E, 0x04, 0x7E };

   RCTest = true;

   try
   {
       dos.write(initCmd);
       Thread.sleep(500);

       dos.write(selectMainFile);
       Thread.sleep(500);

       dos.write(selectDF1);
       Thread.sleep(500);

       dos.write(readDataDF1);
       Thread.sleep(500);

       dos.write(terminateICMode);
   }
   catch (Exception e)
   {
       e.printStackTrace();
   }
}

public class RCCardData
{
   public String REG_NUM;
   public String REG_NAME;
   public String REG_UPTO;
}

public class DLCardData
{
   public String NAME;
   public String SWD_OF;
   public String DOB;
   public String DL_NUM;
   public String ISS_AUTH;
   public String DOI;
   public String VALID_TP;
   public String VALID_NTP;
   public String VEH_INFO_1;
}

public class MSRCardData
{
   public String m_cardNumber;
   public String m_AccoundHolderName;
   public String m_expiryDate;
   public String m_serviceCode;
   public String m_pvkiNumber;
   public String m_pvvNumber;
   public String m_cvvNumber;
}

class AdditionalAndDiscritionaryData
{
   String expiryDate;
   String serviceCode;
   String Pvki;
   String pvv;
   String Cvv;
}

public MSRCardData decodeCreditCard(String buffer, CARD_TRACK track)
{

   byte[] bytesBuffer = new byte[buffer.length()];

   for(int i = 0; i < buffer.length(); i++)
   {
       bytesBuffer[i] = (byte) buffer.charAt(i);
   }

   MSRCardData creditDetails = new MSRCardData();

   creditDetails.m_cardNumber = getAccountNumber(bytesBuffer, track);
   creditDetails.m_AccoundHolderName = getAccountHolderName(bytesBuffer, track);

   AdditionalAndDiscritionaryData additionalDetails = getAdditionalDetails(bytesBuffer, track);

   creditDetails.m_expiryDate = additionalDetails.expiryDate;
   creditDetails.m_serviceCode = additionalDetails.serviceCode;
   creditDetails.m_pvkiNumber = additionalDetails.Pvki;
   creditDetails.m_pvvNumber= additionalDetails.pvv;
   creditDetails.m_cvvNumber= additionalDetails.Cvv;
   // here decoding goes

   return creditDetails;
}

String getAccountNumber(byte[] bytesBuffer, CARD_TRACK type)
{
   StringBuffer accountnumber = new StringBuffer();
   int FS_Value = 0;
   int startIndex = 0;
   if(type == CARD_TRACK.TRACK1)
   {
       FS_Value = 94;
       startIndex = 2;
   }
   else if(type == CARD_TRACK.TRACK2)
   {
       FS_Value = 61;
       startIndex = 1;
   }
   // this will max go upto 19 digits only
   for(int i = startIndex; i < bytesBuffer.length; i++)
   {
       if(bytesBuffer[i] == FS_Value)
           break;

       accountnumber.append((char)bytesBuffer[i]);
   }

   return accountnumber.toString();
}

String getAccountHolderName(byte[] bytesBuffer, CARD_TRACK type)
{
   if(type == CARD_TRACK.TRACK2)
   {
       return "NA";
   }

   StringBuffer accountName = new StringBuffer();

   int FS_Value = 0;
   int startIndex = 0;

   if(type == CARD_TRACK.TRACK1)
   {
       FS_Value = 94;
       startIndex = 2;
   }



   int startofNameOfIndex = 0;
   // this will max go upto 19 digits only
   for(int i = startIndex; i < bytesBuffer.length; i++)
   {
       if(bytesBuffer[i] == FS_Value)
       {
           startofNameOfIndex = i;
           break;
       }
   }

   for(int i = startofNameOfIndex+1; i < bytesBuffer.length; i++)
   {
       if(bytesBuffer[i] == FS_Value)
           break;

       accountName.append((char)bytesBuffer[i]);
   }

   return accountName.toString();
}

AdditionalAndDiscritionaryData getAdditionalDetails(byte[] bytesBuffer, CARD_TRACK type)
{
   AdditionalAndDiscritionaryData additionalData = new AdditionalAndDiscritionaryData();
   StringBuffer expirationDateAndServiceCode = new StringBuffer();

   int FS_Value = 0;
   int startIndex = 0;
   int FS_Count = 1;

   if(type == CARD_TRACK.TRACK1)
   {
       FS_Value = 94;
       startIndex = 2;
       FS_Count = 2;

   }
   else if(type == CARD_TRACK.TRACK2)
   {
       FS_Value = 61;
       startIndex = 1;
       FS_Count = 1;
   }

   int startOfDataIndex = 0;
   int count = 0;

   for(int i = startIndex; i < bytesBuffer.length; i++)
   {
       if(bytesBuffer[i] == FS_Value)
       {
           count++;

           if(count == FS_Count)
           {
               startOfDataIndex = i+1;
               break;
           }
       }
   }

   for(int i = startOfDataIndex; i < startOfDataIndex + 15; i++)
   {
       expirationDateAndServiceCode.append((char)bytesBuffer[i]);

       if(i == startOfDataIndex + 1)
           expirationDateAndServiceCode.append("/");

       if(i == startOfDataIndex + 3)
           expirationDateAndServiceCode.append("-");

       if(i == startOfDataIndex + 6)
           expirationDateAndServiceCode.append("-");

       if(i == startOfDataIndex + 7)
           expirationDateAndServiceCode.append("-");

       if(i == startOfDataIndex + 11)
           expirationDateAndServiceCode.append("-");
   }

   String [] additionalDataFields = expirationDateAndServiceCode.toString().split("-");
   additionalData.expiryDate = additionalDataFields[0];
   additionalData.serviceCode = additionalDataFields[1];
   additionalData.Pvki = additionalDataFields[2];
   additionalData.pvv = additionalDataFields[3];
   additionalData.Cvv= additionalDataFields[4];

   return additionalData;
}


}
