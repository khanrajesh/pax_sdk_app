package fingerprintmodel;





import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import fingerprintmodel.uid.Data;
import fingerprintmodel.uid.Skey;

@Root(name = "StartekPidData")
public class FM220PidData {

    public FM220PidData() {
    }
    @Element(name = "Resp",required = false)
                public Resp _Resp;

    @Element(name = "DeviceInfo",required = false)
    public FM220DeviceInfo _DeviceInfo;

    @Element(name = "Skey", required = false)
    public Skey _Skey;

     @Element(name = "Hmac",required = false)
    public String Hmac;

     @Element(name = "Data", required = false)
    public Data _Data;

}
