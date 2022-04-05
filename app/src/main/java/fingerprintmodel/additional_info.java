package fingerprintmodel;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "additional_info")
public class additional_info {

    public additional_info() {
    }

    @ElementList(name = "Param", required = false, inline = true)
    public List<Param> params;
}
