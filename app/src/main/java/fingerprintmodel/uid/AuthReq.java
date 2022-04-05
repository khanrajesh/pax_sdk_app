package fingerprintmodel.uid;



import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "Auth")
@Namespace(reference = "http://www.uidai.gov.in/authentication/uid-auth-request/2.0")
public class AuthReq {

    public AuthReq() {
    }

    @Attribute(name = "uid", required = true)
    public String uid;

    @Attribute(name = "rc", required = true)
    public String rc;

    @Attribute(name = "tid", required = true)
    public String tid;

    @Attribute(name = "ac", required = true)
    public String ac;

    @Attribute(name = "sa", required = true)
    public String sa;

    @Attribute(name = "ver", required = true)
    public String ver;

    @Attribute(name = "txn", required = true)
    public String txn;

    @Attribute(name = "lk", required = true)
    public String lk;

    @Element(name = "Uses", required = false)
    public Uses uses;

    @Element(name = "Meta", required = false)
    public Meta meta;

    @Element(name = "Skey", required = false)
    public Skey skey;

    @Element(name = "Data", required = false)
    public Data data;

    @Element(name = "Hmac", required = false)
    public String Hmac;

    @Element(name = "freshnessFactor", required = false)
    public String freshnessFactor;

}
