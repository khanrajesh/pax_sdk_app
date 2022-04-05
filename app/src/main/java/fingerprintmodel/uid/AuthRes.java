package fingerprintmodel.uid;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "AuthRes", strict = false)
public class AuthRes {

    public AuthRes() {
    }

    @Attribute(name = "ret", required = false)
    public String ret;

    @Attribute(name = "code", required = false)
    public String code;

    @Attribute(name = "txn", required = false)
    public String txn;

    @Attribute(name = "err", required = false)
    public String err;

    @Attribute(name = "ts", required = false)
    public String ts;

    @Attribute(name = "actn", required = false)
    public String actn;

    @Attribute(name = "info", required = false)
    public String info;

}
