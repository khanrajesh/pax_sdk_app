package fingerprintmodel;



import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import fingerprintmodel.uid.Data;
import fingerprintmodel.uid.Skey;

@Root(name = "MorphoPidData")
public class MorphoPidData {



    public MorphoPidData() {
    }

    @Element(name = "Resp", required = false)
    public Resp _Resp;

    @Element(name = "DeviceInfo", required = false)
    public MorphoDeviceInfo _DeviceInfo;

    @Element(name = "Skey", required = false)
    public Skey _Skey;

    @Element(name = "Hmac", required = false)
    public String _Hmac;

    @Element(name = "Data", required = false)
    public Data _Data;

}
