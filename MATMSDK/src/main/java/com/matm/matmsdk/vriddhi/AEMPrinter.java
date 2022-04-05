package com.matm.matmsdk.vriddhi;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.usb.UsbDevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class AEMPrinter {
    BluetoothSocket bluetoothSocket;
    Socket socket;
    int effectivePrintWidth = 48;
    //	UsbController usbCtrl = null;
    UsbDevice dev = null;
    Context context;

    public AEMPrinter(BluetoothSocket socket) {
        bluetoothSocket = socket;
    }

    public AEMPrinter(Socket socket)
    {
        socket = socket;
    }

    // New Change
    public byte[] ESC_font = new byte[]{0x1b};
    public byte[] ESC_alignmenText = new byte[]{0x61};
    public static final byte FONT_NORMAL = 0X00; //font normal
    public static final byte FONT_CALIBRI = 0X01; // font Calibri
    public static final byte FONT_Tahoma = 0X02; // font tahoma
    public static final byte FONT_Verdana = 0X03; // font verdana
    public static final byte DOUBLE_HIEGHT = 0X10;
    public static final byte DOUBLE_WIDTH = 0X20;
    public static final byte DOUBLE_UNDERLINE = (byte) 0X80;
    public static final byte LEFT = 0X00;
    public static final byte CENTER = 0X01;
    public static final byte RIGHT = 0X02;
    private static final byte NEGATIVE_TEXT = 0X01;
    public static final byte FONT_001 = 0X03;
    public static final byte FONT_002 = 0X14;
    public static final byte FONT_003 = 0X16;


    //Three Inch Commands
    public byte[] ESC_dollors_nL_nHp = new byte[]{27, 36, 0, 0};
    public byte[] ESC_dollors_nL_nH = new byte[]{27, 36, 0, 0};
    public byte[] GS_w_n = new byte[]{29, 119, 3};
    public byte[] GS_H_n = new byte[]{29, 72, 0};
    public byte[] GS_f_n = new byte[]{29, 102, 0};
    public byte[] GS_h_n = new byte[]{29, 104, -94};
    public byte[] GS_k_m_n_ = new byte[]{29, 107, 65, 12};
    public byte[] GS_exclamationmark_n = new byte[]{29, 33, 0};
    public byte[] ESC_M_n = new byte[]{27, 77, 0};
    public byte[] GS_E_n = new byte[]{27, 69, 0};
    public byte[] ESC_line_n = new byte[]{27, 45, 0};
    public byte[] ESC_lbracket_n = new byte[]{27, 123, 0};
    public byte[] GS_B_n = new byte[]{29, 66, 0};
    public byte[] ESC_V_n = new byte[]{27, 86, 0};
    public byte[] FS_line_n = new byte[]{28, 45, 0};
    public byte[] ESC_a_n = new byte[]{27, 97, 0};
    public byte[] ESC_3_n = new byte[]{27, 51, 0};
    public byte[] ESC_SP_n = new byte[]{27, 32, 0};
    public byte[] GS_W_nL_nH = new byte[]{29, 87, 118, 2};
    public byte[] ESC_font_normal = new byte[]{27, 33, 0};

    // checked
    public byte[] ESC_FONT_NORMAL = new byte[]{27, 33, 0}; //FONT_NORMAL  checked
    public byte[] ESC_FONT_CALIBRI = new byte[]{27, 33, 1}; //FONT_CALIBRI checked
    public byte[] ESC_FONT_TAHOMA = new byte[]{27, 33, 2}; // FONT_Tahoma checked
    public byte[] ESC_FONT_VERDANA = new byte[]{27, 33, 3}; // FONT_Verdana checked
    public byte[] ESC_LEFT = new byte[]{27, 97, 0}; // LEFT
    public byte[] ESC_RIGHT = new byte[]{27, 97, 2}; // RIGHT
    public byte[] ESC_CENTER = new byte[]{27, 97, 1}; // CENTER
    public byte[] ESC_NEGATIVETEXT = new byte[]{29, 66, 1}; // Negative text
    public byte[] ESC_DOUBLE_HIEGHT = new byte[]{27, 33, 16}; // DOUBLE_HIEGHT
    public byte[] ESC_DOUBLE_WIDTH = new byte[]{27, 33, 32}; // DOUBLE_WIDTH
    public byte[] ESC_DOUBLE_UNDERLINE = new byte[]{27, 33, 80}; // DOUBLE_UNDERLINE
    public byte[] ESC_Initialize_printer = new byte[]{27, 64}; // DOUBLE_UNDERLINE

    public static final byte FONT_SIZE_001 = 0X03;
    public static final byte FONT_SIZE_002 = 0X14;
    public static final byte FONT_SIZE_003 = 0X16;

    // New Commands By Ankit sir
    public byte[] ESC_HORIZANTAL_TAB = new byte[]{0x09};
    //  public byte[] ESC_HORIZANTAL_TAB_POSITION = new byte[]{27,68,};
    public byte[] ESC_BOLD_ADDED = new byte[]{0x1B,0x21,0x08}; // checked done
    public byte[] ESC_FEED_PAPERLINE = new byte[]{0x1B,0x64,0x01}; // checked done
    public byte[] ESC_FEED_MINIMUM_UNIT = new byte[]{0x1B,0x4A,0x01}; // checked done

    public byte[] ESC_BOLD = new byte[]{0x1B,0x45,0x01}; // checked done
    public byte[] ESC_FEED_AUTOCUT = new byte[]{0x1D,0x56,0x01}; //checked done
    public byte[] ESC_PARTIAL_CUT = new byte[]{0x1B,0x6D}; // checked done
    public byte[] ESC_LEFT_MARGIN = new byte[]{0x1D,0x4C,0x01};  // checked done
    //	public byte[] ESC_FEED_AUTOCUT1 = new byte[]{0x1D,0x56,0x49};
    public byte[] ESC_CHANGECHARACTERFONT = new byte[]{0x1B,0x4D,0x01};  // checked done
    public byte[] ESC_Select_character_size = new byte[]{0x1D,0x21,0x01}; // checked done
    public byte[] ESC_IMAGE_ALIGNMENT = new byte[]{0x1B,0x61,0x01};

    public void POS__PARTIALCUT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_BOLD_ADDED});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_FEED_PAPERLINE() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FEED_PAPERLINE});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_FEED_MINIMUM_UNIT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FEED_MINIMUM_UNIT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_BOLD() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_BOLD});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_FEED_AUTOCUT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FEED_AUTOCUT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_PARTIAL_CUT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_PARTIAL_CUT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_LEFT_MARGIN() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_LEFT_MARGIN});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_CHANGECHARACTERFONT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_CHANGECHARACTERFONT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_Select_character_size() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_Select_character_size});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }
    public void POS__ESC_IMAGE_ALIGNMENT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_IMAGE_ALIGNMENT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchNORMAL() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FONT_NORMAL});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchCALIBRI() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FONT_CALIBRI});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchTAHOMA() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FONT_TAHOMA});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchVERDANA() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_FONT_VERDANA});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchLEFT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_LEFT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchRIGHT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_RIGHT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInchCENTER() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_CENTER});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchNEGATIVETEXT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_NEGATIVETEXT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchUNDERLINE() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_DOUBLE_UNDERLINE});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchDOUBLEHIEGHT() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_DOUBLE_HIEGHT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchDOUBLEWIDTH() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_DOUBLE_WIDTH});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS__FontThreeInchInitialize_printer() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_Initialize_printer});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public byte[] ESC_NEGATIVE_TEXT = new byte[]{27, 77, 3};
    public void POS__FontThreeInch() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_NEGATIVE_TEXT});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_FontThreeInch() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_font_normal});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }


    public byte[] ESC_font_highlight = new byte[]{27, 33, 1};
    public void POS_Font_highlight_ThreeInch() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_font_highlight});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public byte[] ESC_font_bold = new byte[]{27, 33, 3};
    public void POS_Font_bold_ThreeInch() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{ESC_font_bold});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_S_AlignThreeInch(int align) throws IOException {
        if(align >= 0 && align <= 2) {
            byte[] data = ESC_a_n;
            data[2] = (byte)align;
            bluetoothSocket.getOutputStream().write(data, 0, data.length);
        }
    }

    public byte[] LF = new byte[]{10};
    public byte[] CR = new byte[]{13};
    public void POS_FeedLineThreeInch() throws IOException {
        byte[] data = byteArraysToBytes(new byte[][]{CR, LF});
        bluetoothSocket.getOutputStream().write(data, 0, data.length);
    }

    public void POS_SetRightSpacing(int nDistance) throws IOException {
        if (!(nDistance < 0 | nDistance > 255)) {
            byte[] data = ESC_SP_n;
            data[2] = (byte)nDistance;
            bluetoothSocket.getOutputStream().write(data, 0, data.length);
        }
    }

    public void POS_S_SetAreaWidth(int nWidth) throws IOException {
        if (!(nWidth < 0 | nWidth > 65535)) {
            byte nL = (byte)(nWidth % 256);
            byte nH = (byte)(nWidth / 256);
            GS_W_nL_nH[2] = nL;
            GS_W_nL_nH[3] = nH;
            byte[] data = GS_W_nL_nH;
            bluetoothSocket.getOutputStream().write(data, 0, data.length);
        }
    }

    public void POS_SetLineHeightThreeInch(int nHeight) throws IOException {
        if(nHeight >= 0 && nHeight <= 255) {
            byte[] data = ESC_3_n;
            data[2] = (byte)nHeight;
            bluetoothSocket.getOutputStream().write(data, 0, data.length);
        }
    }

    public void POS_S_TextOutThreeInch(String pszString, String encoding, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) throws IOException {
        if(!(nOrgx > '\uffff' | nOrgx < 0 | nWidthTimes > 7 | nWidthTimes < 0 | nHeightTimes > 7 | nHeightTimes < 0 | nFontType < 0 | nFontType > 4 | pszString.length() == 0)) {
            ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
            ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
            byte[] intToWidth = new byte[]{0, 16, 32, 48, 64, 80, 96, 112};
            byte[] intToHeight = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
            GS_exclamationmark_n[2] = (byte)(intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);
            byte[] tmp_ESC_M_n = ESC_M_n;
            if(nFontType != 0 && nFontType != 1) {
                tmp_ESC_M_n = new byte[0];
            } else {
                tmp_ESC_M_n[2] = (byte)nFontType;
            }
            GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
            ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
            GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
            ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
            Object pbString = null;
            byte[] pbString1;
            try {
                pbString1 = pszString.getBytes(encoding);
            } catch (UnsupportedEncodingException var13) {
                return;
            }
            byte[] data = byteArraysToBytes(new byte[][]{ESC_dollors_nL_nH, GS_exclamationmark_n, tmp_ESC_M_n, GS_E_n, ESC_line_n, FS_line_n, ESC_lbracket_n, GS_B_n, ESC_V_n, pbString1});
            bluetoothSocket.getOutputStream().write(data, 0, data.length);

        }
    }

    public void printThreeInch(String text) throws IOException
    {
        if (bluetoothSocket == null)
            return;

//		byte[] dataBytes = text.getBytes();
//
//		byte[] dataPacket = new byte[dataBytes.length + 4];
//
//		dataPacket[0] = 0x0B;
//		dataPacket[1] = 0x02;
//		dataPacket[2] = TEXT_ALIGNMENT;
//
//		int i = 0;
//
//		for(i = 0; i < dataBytes.length; i++)
//		{
//			dataPacket[i + 3] = dataBytes[i];
//		}
//
//		dataPacket[i + 3] = 0x03;

//
        byte header[] = null;
        byte strbuf[] = null;
        // header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x05 };
        header = new byte[] { 0x1b, 0x40/*, 0x1c, 0x26, 0x1b, 0x39, 0x01*/ };
        strbuf =text.getBytes();
        byte buffer[] = byteArraysToBytes(new byte[][] {header, strbuf });
        bluetoothSocket.getOutputStream().write(buffer, 0, buffer.length);

        //setCarriageReturn();
    }

    // printing barcode
    public void printBarcodeThreeInch(String barcodeData, BARCODE_TYPE barcodetype, BARCODE_HEIGHT barcodeheight) throws IOException
    {
        if (bluetoothSocket == null)
            return;
        byte[] barcodepacket = createBarcodePacketThreeInch(barcodeData.getBytes(), barcodetype, barcodeheight);
        if(barcodepacket == null)
            return;
        bluetoothSocket.getOutputStream().write(barcodepacket, 0, barcodepacket.length);
    }

    private byte[] createBarcodePacketThreeInch(byte[] barcodeBytes, BARCODE_TYPE barcodetype, BARCODE_HEIGHT height)
    {
        if(barcodetype == BARCODE_TYPE.CODE39)
        {
            int type=0x41 + BARCODE_TYPE_CODE39;
            ESC_dollors_nL_nH[2] = 0;
            ESC_dollors_nL_nH[3] = 0;
            GS_w_n[2] = 2;
            GS_h_n[2] = 96;
            GS_f_n[2] = 0;
            GS_H_n[2] = 2;
            GS_k_m_n_[2] = 69;
            GS_k_m_n_[3] = (byte)(barcodeBytes.length);

            byte[] barcodePacket = new byte[barcodeBytes.length+6];
            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = (byte) type; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length + 2); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);
            barcodePacket[4] = 0x2A;
            byte[] data = byteArraysToBytes(new byte[][]{this.ESC_dollors_nL_nH, this.GS_w_n, this.GS_h_n, this.GS_f_n, this.GS_H_n, this.GS_k_m_n_, barcodeBytes});

            int i = 0;

            for(i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 5] = barcodeBytes[i];
            }

            barcodePacket[i + 5] = 0x2A;

            return data;
        }else if(barcodetype == BARCODE_TYPE.UPCA)

        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];
            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_UPCA; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }

            return barcodePacket;
        }
        else if(barcodetype == BARCODE_TYPE.EAN13) {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];

            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_EAN13; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }
            return barcodePacket;

        }
        else if(barcodetype == BARCODE_TYPE.EAN8)
        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];
            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_EAN8; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }

            return barcodePacket;
        }

        return null;
    }

    public void setFontType(byte FONT) throws IOException
    {
        bluetoothSocket.getOutputStream().write(FONT);
    }

    public void sendByte(byte bt) throws IOException
    {
        bluetoothSocket.getOutputStream().write(bt);
    }

    public void sendByteArrayBT(byte[] byteArr) throws IOException
    {
        bluetoothSocket.getOutputStream().write(byteArr, 0, byteArr.length);
    }

    //	public static final byte DOUBLE_WIDTH = 0X04;
    public static final byte DOUBLE_HEIGHT = 0X08;
    public static final byte ESCAPE_SEQ = 0X1b;
    public static final byte ESCAPE_a = 0X61;
    public static final byte ESCAPE_CENTER = 0X01;
    public static final byte ESCAPE_EXCL = 0X21;
    public static final byte ESCAPE_DOUBLE_HEIGHT = 0X10;


    public void setFontSize(byte DOUBLE_DIMENSION) throws IOException
    {
        bluetoothSocket.getOutputStream().write(DOUBLE_DIMENSION);
    }

    private static final byte NEGATIVE_CHAR = 0X0E;

    public void printInNegative() throws IOException
    {
        bluetoothSocket.getOutputStream().write(NEGATIVE_CHAR);
    }

    private static final byte UNDERLINE = 0X15;

    public void enableUnderline() throws IOException
    {
        bluetoothSocket.getOutputStream().write(UNDERLINE);
    }

    private static final byte LINE_FEED = 0X0A;
    private static final byte CARRIAGE_RETURN = 0X0D;

    public void setLineFeed(int noOfFeeds) throws IOException
    {
        for(int i = 0; i < noOfFeeds ; i++)
            bluetoothSocket.getOutputStream().write(LINE_FEED);
    }

    public void setCarriageReturn() throws IOException
    {
        bluetoothSocket.getOutputStream().write(LINE_FEED);
    }

    public static final byte TEXT_ALIGNMENT_LEFT = 0X01;
    public static final byte TEXT_ALIGNMENT_RIGHT = 0X02;
    public static final byte TEXT_ALIGNMENT_CENTER = 0X03;

    // printing text
    public void print(String text) throws IOException
    {
        if (bluetoothSocket == null)
            return;

//		byte[] dataBytes = text.getBytes();
//
//		byte[] dataPacket = new byte[dataBytes.length + 4];
//
//		dataPacket[0] = 0x0B;
//		dataPacket[1] = 0x02;
//		dataPacket[2] = TEXT_ALIGNMENT;
//
//		int i = 0;
//
//		for(i = 0; i < dataBytes.length; i++)
//		{
//			dataPacket[i + 3] = dataBytes[i];
//		}
//
//		dataPacket[i + 3] = 0x03;
//

        bluetoothSocket.getOutputStream().write(text.getBytes(), 0, text.getBytes().length);

        //setCarriageReturn();
    }

    public void PrintHindi(String text) throws IOException
    {
        if (bluetoothSocket == null)
            return;


        int ctr2, ctr1, ctr3  = 0;
        int addFlag = 0, nextCtrFlag = 0, E0Flag = 0, hindiFlag = 0;
        //final byte[] btArr = data.getBytes(Charset.forName("UTF-8"));
        byte[] btArr = text.getBytes();
        final byte[] newArr = new byte[1000];
        final byte[] arrToSend = new byte[1000];
        byte b1;//, b2, b3;
        ctr2=0;

        for(ctr1 = 0; ctr1<btArr.length; ctr1++)
        {
            b1 = btArr[ctr1];
            nextCtrFlag = 0;
            if((b1 > 0) && (b1 < 127))//english characters/spl chars
            {
                newArr[ctr2] = b1;
                addFlag = 0;
                nextCtrFlag = 1;
                hindiFlag = 0;
            }
            else if(b1 == -32)//0xE0
            {
                addFlag = 0;
                E0Flag = 1;
                hindiFlag = 0;
            }
            else if(b1 == -91)
            {
                if(E0Flag == 1)
                {
                    addFlag = 1;
                    E0Flag = 2;
                    hindiFlag = 0;
                }
                else if(E0Flag == 2)
                    hindiFlag = 1;
            }
            else if(b1 == -92)
            {
                if(E0Flag == 1)
                {
                    addFlag = 0;
                    E0Flag = 2;
                    hindiFlag = 0;
                }
                else if(E0Flag == 2)
                    hindiFlag = 1;

            }
            else if(b1 < 0)//hindi chars
                hindiFlag = 1;
            if(hindiFlag == 1)
            {
                if(addFlag == 1)
                    newArr[ctr2] = (byte)(b1 + 128 - 64) ;
                else
                    newArr[ctr2] = b1;
                nextCtrFlag = 1;
                addFlag = 0;
                E0Flag = 0;
                hindiFlag = 0;
            }
            if(nextCtrFlag ==1)
                ctr2++;
        }
        b1 = -123;
        byte b2;

        for (ctr1 = 0; ctr1<ctr2; ctr1++)
        {
            if(newArr[ctr1] == -108) //ou
            {
                //m_AemPrinter.setFontType(b1); //a
                arrToSend[ctr3++] = b1;
                b2 = -52;
                // m_AemPrinter.setFontType(b2);
                arrToSend[ctr3++] = b2;
            }
            else if(newArr[ctr1] == -109) //o
            {
                //m_AemPrinter.setFontType(b1);
                arrToSend[ctr3++] = b1;
                b2 = -53;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);

            }
            else if(newArr[ctr1] == -122) //aa
            {
                //m_AemPrinter.setFontType(b1);
                arrToSend[ctr3++] = b1;
                b2 = -66;
                arrToSend[ctr3++] = b2;
                // m_AemPrinter.setFontType(b2);
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -107)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -73))//ksha
            {
                b2 = -87;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//ksha
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -107)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//kra
            {
                b2 = -124;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for kra
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -105)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//gra
            {
                b2 = -122;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for gra
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -105)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -81))//gya
            {
                b2 = -114;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for gya
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -86)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//pra
            {
                b2 = -115;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for pra
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -97)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//tra
            {
                b2 = -69;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for tra
                ctr1+=2;
            }

            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -92)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//thra
            {
                b2 = -111;
                arrToSend[ctr3++] = b2;
                ///m_AemPrinter.setFontType(b2);//send code for thra
                ctr1+=2;
            }

            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -92)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -92))//ttha
            {
                b2 = -116;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for ttha
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -74)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//shra
            {
                b2 = -79;
                arrToSend[ctr3++] = b2;
                // m_AemPrinter.setFontType(b2);//send code for shra
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -90)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -80))//dra
            {
                b2 = -108;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for dra
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -90)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -75))//dva
            {
                b2 = -70;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for dva
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -90)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -81))//dya
            {
                b2 = -119;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for dya
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -71)&&(newArr[ctr1+1] == -51)&&(newArr[ctr1+2] == -81))//hya
            {
                b2 = -110;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for hya
                ctr1+=2;
            }
            else if(((ctr2-ctr1)>=2)&&(newArr[ctr1] == -80)&&(newArr[ctr1+1] == -51))//r matra
            {
                //m_AemPrinter.setFontType(newArr[ctr1+2]);
                arrToSend[ctr3++] = newArr[ctr1+2];
                if(ctr2-ctr1 > 2)
                {
                    if((newArr[ctr1+3] == -66) || (newArr[ctr1+3] == -65) || (newArr[ctr1+3] == -64))//matras
                    {
                        //m_AemPrinter.setFontType(newArr[ctr1+3]);
                        arrToSend[ctr3++] = newArr[ctr1+3];
                        ctr1++;
                    }
                }
                b2 = -67;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for r matra
                ctr1+=2;
            }
            else if((ctr2-ctr1 > 1) && (newArr[ctr1] == -80)&& (newArr[ctr1+1] == -63))//ru
            {
                b2 = -76;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for ru
                ctr1+=1;
            }
            else if((ctr2-ctr1 > 1) && (newArr[ctr1] == -80)&& (newArr[ctr1+1] == -62))//roo
            {
                b2 = -77;
                arrToSend[ctr3++] = b2;
                //m_AemPrinter.setFontType(b2);//send code for roo
                ctr1+=1;
            }
            else
                arrToSend[ctr3++] = newArr[ctr1];
            //m_AemPrinter.setFontType(newArr[ctr1]);
        }

        // m_AemPrinter.print(s);

        int len = ctr3;
        for (ctr1 = 0; ctr1 < len; ctr1++)
        {
            if(ctr1 > 0) {
                if (arrToSend[ctr1] == -65)//small e matra
                {
                    arrToSend[ctr1] = arrToSend[ctr1 - 1];
                    arrToSend[ctr1 - 1] = -65;

                    if (ctr1 > 2) {
                        if (arrToSend[ctr1 - 2] == -51)//in case of halant character
                        {
                            byte temp = arrToSend[ctr1 - 3];
                            arrToSend[ctr1 - 3] = -65; //small e matra
                            arrToSend[ctr1 - 2] = temp;
                            arrToSend[ctr1 - 1] = -51;
                        }
                    }
                }

            }

        }

        int matraFlag = 0;
        ctr2 = 0;
        for (ctr1 = 0; ctr1 < len; ctr1++)
        {
            if(matraFlag == 2)
            {
                matraFlag = 0;
                b2 = -60;
                newArr[ctr2++] = b2; //send additional or-ing character for better visibility of matra
            }
            newArr[ctr2++] = arrToSend[ctr1];
            if(matraFlag > 0)
                matraFlag++;
            if(arrToSend[ctr1] == -65) //small e matra
                matraFlag = 1;
        }
        if(matraFlag == 2)//in case matra is at last character
        {
            matraFlag = 0;
            b2 = -60;
            newArr[ctr2++] = b2;
            //m_AemPrinter.setFontType(b2); //send additional or-ing character for better visibility of matra
        }

        bluetoothSocket.getOutputStream().write(newArr, 0,ctr2);
        setCarriageReturn();

    }

    private final byte BARCODE_TYPE_UPCA = 0x41;
    private final byte BARCODE_TYPE_EAN13 = 0x43;
    private final byte BARCODE_TYPE_EAN8 = 0X44;
    private final byte BARCODE_TYPE_CODE39 = 0X45;
    private final byte BARCODE_TYPE_CODE128 = 0X73;

    public void printImage(Bitmap resizedBitmap) {
        com.matm.matmsdk.vriddhi.PrintRasterImage PrintRasterImage = new com.matm.matmsdk.vriddhi.PrintRasterImage(getResizedBitmap(resizedBitmap));
        PrintRasterImage.PrepareImage(com.matm.matmsdk.vriddhi.PrintRasterImage.dither.floyd_steinberg, 128);
        byte[] imgStr =PrintRasterImage.getPrintImageData();
        try
        {
            bluetoothSocket.getOutputStream().write(imgStr, 0, imgStr.length);
        }
        catch(IOException e)
        {
            System.out.print("IOException ");
        }
    }

    public enum BARCODE_TYPE {UPCA, EAN13, EAN8, CODE39,CODE128};


    public enum BARCODE_HEIGHT {DOUBLEDENSITY_FULLHEIGHT, TRIPLEDENSITY_FULLHEIGHT, DOUBLEDENSITY_HALFHEIGHT, TRIPLEDENSITY_HALFHEIGHT};



	/*public Bitmap createQRCode(String text) throws WriterException
	{

		Writer writer = new QRCodeWriter();
		String finalData = Uri.encode(text, "UTF-8");

		BitMatrix bm = writer.encode(finalData, BarcodeFormat.QR_CODE, 350, 255);
	    Bitmap bitmap = Bitmap.createBitmap(350, 255, Config.ARGB_8888);

	    for(int i = 0; i < 350; i++)
	    {
	    	for(int j = 0; j < 255; j++)
	    	{
	    		bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
	    	}
	    }

	    return bitmap;
	}*/

    // printing barcode
    public void printBarcode(String barcodeData, BARCODE_TYPE barcodetype, BARCODE_HEIGHT barcodeheight) throws IOException
    {

        if (bluetoothSocket == null)
            return;

        byte[] barcodepacket = createBarcodePacket(barcodeData.getBytes(), barcodetype, barcodeheight);

        if(barcodepacket == null)
            return;

        bluetoothSocket.getOutputStream().write(barcodepacket, 0, barcodepacket.length);

    }

    private byte[] createBarcodePacket(byte[] barcodeBytes, BARCODE_TYPE barcodetype, BARCODE_HEIGHT height)
    {
        if(barcodetype == BARCODE_TYPE.CODE39)
        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 6];

            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_CODE39; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length+2); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);
            barcodePacket[4] = 0x2A;

            int i = 0;

            for(i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 5] = barcodeBytes[i];
            }

            barcodePacket[i + 5] = 0x2A;

            return barcodePacket;
        }else if(barcodetype == BARCODE_TYPE.UPCA)
        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];
            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_UPCA; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }

            return barcodePacket;
        }else if(barcodetype == BARCODE_TYPE.EAN13)
        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];

            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_EAN13; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }

            return barcodePacket;

        }
        else if(barcodetype == BARCODE_TYPE.EAN8)
        {
            byte[] barcodePacket = new byte[barcodeBytes.length + 4];
            barcodePacket[0] = 0x1D;
            barcodePacket[1] = 0x6B;
            barcodePacket[2] = BARCODE_TYPE_EAN8; // barcode type
            barcodePacket[3] = (byte) (barcodeBytes.length); //length of barcode data
            //barcodePacket[4] = getBarcodeHeight(height);

            for(int i = 0; i < barcodeBytes.length; i++)
            {
                barcodePacket[i + 4] = barcodeBytes[i];
            }

            return barcodePacket;
        }

        return null;
    }

    private byte getBarcodeHeight(BARCODE_HEIGHT height)
    {
        byte mode = 0x61;

        if(height == BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT)
        {
            mode = 0x61;
        }else if(height == BARCODE_HEIGHT.TRIPLEDENSITY_FULLHEIGHT)
        {
            mode = 0x62;
        }else if(height == BARCODE_HEIGHT.DOUBLEDENSITY_HALFHEIGHT)
        {
            mode = 0x63;
        }else if(height == BARCODE_HEIGHT.TRIPLEDENSITY_HALFHEIGHT)
        {
            mode = 0x64;
        }

        return mode;
    }
    Context m_Context;
    public static final byte IMAGE_LEFT_ALIGNMENT = 0x6C;
    public static final byte IMAGE_CENTER_ALIGNMENT = 0x63;
    public static final byte IMAGE_RIGHT_ALIGNMENT = 0x72;


    public Bitmap getResizedBitmap(Bitmap bm)
    {
        int newWidth = 248	;
        int newHeight = 297;
        int reqWidth = (int) Math.round(effectivePrintWidth*8);
        int width = bm.getWidth();
        int height = bm.getHeight();
        if(width==reqWidth){
            return bm;
        }
        else if(width<reqWidth&&width>16){
            int diff = width%8;
            if(diff!=0){
                newWidth = width - diff;
                newHeight = (int) (width - diff)*height/width;
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                // CREATE A MATRIX FOR THE MANIPULATION
                Matrix matrix = new Matrix();
                // RESIZE THE BIT MAP
                matrix.postScale(scaleWidth, scaleHeight);

                // "RECREATE" THE NEW BITMAP
                Bitmap resizedBitmap = Bitmap.createBitmap(
                        bm, 0, 0, width, height, matrix, false);
                bm.recycle();
                return resizedBitmap;
            }
        }
        else if(width>16){
            newWidth = reqWidth;
            newHeight = (int) reqWidth*height/width;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
        return bm;
    }

    private boolean deleteFile()
    {
//		File dir = getFilesDir();
        return m_Context.deleteFile("my_monochrome_image.bmp");

    }

    public void printBytes(byte[] printBytes) throws IOException
    {

        if (bluetoothSocket == null)
            return;

        if(printBytes == null)
            return;

        bluetoothSocket.getOutputStream().write(printBytes, 0, printBytes.length);

    }
    public static byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;

        for(int send = 0; send < data.length; ++send) {
            length += data[send].length;
        }

        byte[] var6 = new byte[length];
        int k = 0;
        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                var6[k++] = data[i][j];
            }
        }
        return var6;
    }
}
