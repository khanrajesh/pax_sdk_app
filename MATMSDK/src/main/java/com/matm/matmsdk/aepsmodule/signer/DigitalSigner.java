package com.matm.matmsdk.aepsmodule.signer;

import android.content.Context;
import android.util.Log;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class DigitalSigner {

    private static final String KEY_STORE_TYPE = "PKCS12";
    private String alias;
    private KeyStore ks;
    private PrivateKey privateKey;

    public DigitalSigner(String keyStoreFile, char[] keyStorePassword, Context context) {
        try {
            this.ks = KeyStore.getInstance(KEY_STORE_TYPE);
            this.ks.load(context.getAssets().open(keyStoreFile), keyStorePassword);
            this.alias = this.ks.aliases().nextElement();
            this.privateKey = (PrivateKey) this.ks.getKey(alias, keyStorePassword);
        } catch (Exception e) {
            Log.e("eror","erro");
            e.printStackTrace();
        }
    }

    public String signXML(String xmlDocument) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document signedDocument = sign(dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlDocument))));
            StringWriter stringWriter = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(signedDocument), new StreamResult(stringWriter));
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while digitally signing the XML document", e);
        }
    }

    private Document sign(Document xmlDoc) throws Exception {
        X509Certificate x509Cert = (X509Certificate) this.ks.getCertificate(this.alias);
        XMLSignature signature = new XMLSignature(xmlDoc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
//        XMLSignature signature = new XMLSignature(xmlDoc, StringUtils.EMPTY, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
        xmlDoc.getDocumentElement().appendChild(signature.getElement());
        Transforms transforms = new Transforms(xmlDoc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
//        signature.addDocument(StringUtils.EMPTY, transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
        X509Data x509Data = new X509Data(xmlDoc);
        signature.getKeyInfo().add(x509Data);
        x509Data.addSubjectName(x509Cert.getSubjectX500Principal().getName());
        x509Data.addCertificate(x509Cert);
        signature.sign(this.privateKey);
        return xmlDoc;
    }



    static {

        Init.init();
        try {
            ElementProxy.setDefaultPrefix("http://www.w3.org/2000/09/xmldsig#","");
        } catch (XMLSecurityException e) {
            e.printStackTrace();
        }
    }
}
