package fingerprintmodel.uid;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by SW11 on 9/3/2015.
 */
public class Skey {

    public Skey() {
    }

    @Attribute(name = "ci", required = false)
    public String ci;

    @Text(required = true)
    public String value;

}
