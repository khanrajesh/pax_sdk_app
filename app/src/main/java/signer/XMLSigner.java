package signer;


import android.util.Log;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * Refer Site:  https://gist.github.com/rafaelwkerr/a585d324d3e534a2c16b
 * Gradle Or Jar for signer: https://mvnrepository.com/artifact/xml-security/xmlsec/1.3.0
 */
public class XMLSigner {

    private static final String URI = "#NFe13140782373077000171650290000030531000030538";

    public static String generateSignXML(String inputXML, InputStream privateKeyStream, String keyPass) {
        try {
            InputStream stream = new ByteArrayInputStream(inputXML.getBytes());
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().parse(stream);
            stream.close();
            Init.init();
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "");
            XMLSignature xmlSignature = new XMLSignature(doc, URI, XMLSignature.ALGO_ID_SIGNATURE_RSA);

            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

            xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(privateKeyStream, keyPass.toCharArray());
            String alias = keyStore.aliases().nextElement();
            KeyStore.PrivateKeyEntry dpkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry
                    (alias, new KeyStore.PasswordProtection(keyPass.toCharArray()));
            Key privateKey = dpkEntry.getPrivateKey();
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

            xmlSignature.addKeyInfo(cert);
            xmlSignature.addKeyInfo(cert.getPublicKey());

            xmlSignature.sign(privateKey);
            doc.getDocumentElement().appendChild(xmlSignature.getElement());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS).canonicalizeSubtree(doc));
            return outputStream.toString();
        } catch (Exception e){
            Log.e("Error", "Error while generating Sign XML", e);
            return "";
        }
    }


}
