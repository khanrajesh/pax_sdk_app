package com.matm.matmsdk.vriddhi;

public interface IAemCardScanner {

public void onScanMSR(String buffer, com.matm.matmsdk.vriddhi.CardReader.CARD_TRACK cardtrack);

public void onScanDLCard(String buffer);

public void onScanRCCard(String buffer);

public void onScanRFD(String buffer);

public void onScanPacket(String buffer);
}
